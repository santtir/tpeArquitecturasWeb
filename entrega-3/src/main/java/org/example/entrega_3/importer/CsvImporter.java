package org.example.entrega_3.importer;

import org.example.entrega_3.model.Carrera;
import org.example.entrega_3.model.Estudiante;
import org.example.entrega_3.model.Inscripcion;
import org.example.entrega_3.repository.CarreraRepository;
import org.example.entrega_3.repository.EstudianteRepository;
import org.example.entrega_3.repository.InscripcionRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.*;

/**
 * Importa TUS CSV:
 *
 * 1) resources/data/carreras.csv
 *    id_carrera,carrera,duracion
 *
 * 2) resources/data/estudiantes.csv
 *    DNI,nombre,apellido,edad,genero,ciudad,LU
 *
 * 3) resources/data/estudianteCarrera.csv
 *    id,id_estudiante,id_carrera,inscripcion,graduacion,antiguedad
 *
 */
@Component
public class CsvImporter implements CommandLineRunner {

    private final EstudianteRepository estRepo;
    private final CarreraRepository carRepo;
    private final InscripcionRepository insRepo;

    public CsvImporter(EstudianteRepository estRepo,
                       CarreraRepository carRepo,
                       InscripcionRepository insRepo) {
        this.estRepo = estRepo;
        this.carRepo = carRepo;
        this.insRepo = insRepo;
    }

    @Override
    public void run(String... args) {
        try {
            Map<Integer, Carrera> carrerasByCsvId = importCarreras();
            Map<Integer, Estudiante> estByDni = importEstudiantes();
            importInscripciones(carrerasByCsvId, estByDni);
        } catch (Exception e) {
            System.out.println("[CSV] ERROR general: " + e.getMessage());
        }
    }

    // ======================= CARRERAS =======================
    private Map<Integer, Carrera> importCarreras() throws Exception {
        var res = new ClassPathResource("data/carreras.csv");
        Map<Integer, Carrera> map = new HashMap<>();
        if (!res.exists()) { System.out.println("[CSV] carreras.csv NO existe"); return map; }

        try (var br = new BufferedReader(new InputStreamReader(res.getInputStream(), StandardCharsets.UTF_8))) {
            String header = readNext(br); // id_carrera,carrera,duracion
            if (header == null) return map;

            int count = 0;
            for (String line = readNext(br); line != null; line = readNext(br)) {
                String[] t = split(line);
                if (t.length < 2) continue;

                int csvId = parseInt(t[0], -1);     // id_carrera
                String nombre = unquote(t[1]);      // carrera
                if (csvId < 0 || nombre.isBlank()) continue;

                Carrera c = carRepo.findByNombreIgnoreCase(nombre)
                        .orElseGet(() -> carRepo.save(Carrera.builder().nombre(nombre).build()));
                map.put(csvId, c);
                count++;
            }
            System.out.println("[CSV] Carreras importadas: " + count);
            return map;
        }
    }

    // ======================= ESTUDIANTES =======================
    private Map<Integer, Estudiante> importEstudiantes() throws Exception {
        var res = new ClassPathResource("data/estudiantes.csv");
        Map<Integer, Estudiante> map = new HashMap<>();
        if (!res.exists()) { System.out.println("[CSV] estudiantes.csv NO existe"); return map; }

        try (var br = new BufferedReader(new InputStreamReader(res.getInputStream(), StandardCharsets.UTF_8))) {
            String header = readNext(br); // DNI,nombre,apellido,edad,genero,ciudad,LU
            if (header == null) return map;

            int count = 0, skipped = 0;
            for (String line = readNext(br); line != null; line = readNext(br)) {
                String[] t = split(line);
                if (t.length < 7) { skipped++; continue; }

                int dni = parseInt(t[0], -1);
                String nombre   = unquote(t[1]);
                String apellido = unquote(t[2]);
                int edad        = parseInt(t[3], 0);
                String genero   = normalizeGenero(unquote(t[4]));
                String ciudad   = unquote(t[5]);
                String lu       = unquote(t[6]);

                if (dni < 0 || lu.isBlank()) { skipped++; continue; }

                Estudiante e = Estudiante.builder()
                        .nombres(nombre)
                        .apellido(apellido)
                        .edad(edad)
                        .genero(genero)
                        .nroDocumento(String.valueOf(dni))
                        .ciudadResidencia(ciudad)
                        .nroLibretaUniversitaria(lu)
                        .build();

                Estudiante saved = estRepo.findByNroLibretaUniversitaria(lu)
                        .orElseGet(() -> estRepo.save(e));

                map.put(dni, saved);
                count++;
            }
            System.out.println("[CSV] Estudiantes importados: " + count + " (saltados: " + skipped + ")");
            return map;
        }
    }

    // ======================= INSCRIPCIONES =======================
    private void importInscripciones(Map<Integer, Carrera> carrerasByCsvId,
                                     Map<Integer, Estudiante> estByDni) throws Exception {
        var res = new ClassPathResource("data/estudianteCarrera.csv");
        if (!res.exists()) { System.out.println("[CSV] estudianteCarrera.csv NO existe"); return; }

        try (var br = new BufferedReader(new InputStreamReader(res.getInputStream(), StandardCharsets.UTF_8))) {
            String header = readNext(br); // id,id_estudiante,id_carrera,inscripcion,graduacion,antiguedad
            if (header == null) return;

            int count = 0, skipped = 0;
            for (String line = readNext(br); line != null; line = readNext(br)) {
                String[] t = split(line);
                if (t.length < 6) { skipped++; continue; }

                int dniCsv        = parseInt(t[1], -1);
                int carreraCsvId  = parseInt(t[2], -1);
                int anioInscRaw   = parseInt(t[3], 0);
                int anioGradRaw   = parseInt(t[4], 0);
                int antig         = parseInt(t[5], 0);

                Estudiante est = estByDni.get(dniCsv);
                Carrera car = carrerasByCsvId.get(carreraCsvId);
                if (est == null || car == null) { skipped++; continue; }

                // Validación suave de años (evita valores como 202, 0, etc.)
                Integer anioInsc = (anioInscRaw >= 1900 && anioInscRaw <= 2100) ? anioInscRaw : null;
                Integer anioGrad = (anioGradRaw >= 1900 && anioGradRaw <= 2100) ? anioGradRaw : null;

                Inscripcion ins = Inscripcion.builder()
                        .estudiante(est)
                        .carrera(car)
                        .fechaInscripcion(anioInsc != null ? java.time.LocalDate.of(anioInsc, 1, 1) : null)
                        .antiguedadEnAnios(antig)
                        .graduado(anioGrad != null)          // egresado si hay año de graduación válido
                        .anioEvento(anioInsc)                 // para conteo de inscriptos por año
                        .anioGraduacion(anioGrad)             // <-- clave para (h)
                        .build();

                insRepo.save(ins);
                count++;
            }
            System.out.println("[CSV] Inscripciones importadas: " + count + " (saltadas: " + skipped + ")");
        }
    }

    // ======================= helpers =======================
    private static String readNext(BufferedReader br) throws Exception {
        String line;
        while ((line = br.readLine()) != null) {
            line = line.replace("\uFEFF", "").trim(); // saca BOM
            if (!line.isBlank() && !line.startsWith("#")) return line;
        }
        return null;
    }

    private static String[] split(String line) {
        String sep = line.contains(";") ? ";" : ",";
        String[] parts = line.split(sep, -1);
        for (int i = 0; i < parts.length; i++) parts[i] = unquote(parts[i]);
        return parts;
    }

    private static String unquote(String s) {
        if (s == null) return "";
        String x = s.trim();
        if (x.startsWith("\"") && x.endsWith("\"") && x.length() >= 2) {
            x = x.substring(1, x.length() - 1);
        }
        return x.trim();
    }

    private static int parseInt(String s, int defVal) {
        try { return (s == null || s.isBlank()) ? defVal : Integer.parseInt(s.trim()); }
        catch (Exception e) { return defVal; }
    }

    private static String normalizeGenero(String g) {
        String s = (g == null) ? "" : g.trim().toLowerCase(Locale.ROOT);
        if (s.startsWith("m")) return "M";                 // Male / Masculino / M
        if (s.startsWith("f")) return "F";                 // Female / Femenino / F
        return "X";                                        // otros (Non-binary, etc.)
    }
}

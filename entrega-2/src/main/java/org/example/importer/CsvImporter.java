package org.example.importer;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import org.example.modelo.Carrera;
import org.example.modelo.Estudiante;
import org.example.modelo.EstudianteCarrera;
import org.example.repository.CarreraRepository;
import org.example.repository.EstudianteRepository;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;


public class CsvImporter {

    private final EntityManager em;
    private final EstudianteRepository estRepo;
    private final CarreraRepository carRepo;

    // Mapa para enlazar id_carrera (CSV) -> id entidad Carrera (DB)
    private final Map<Integer, Long> carreraCsvIdToEntityId = new HashMap<>();

    public CsvImporter(EntityManager em,
                       EstudianteRepository estRepo,
                       CarreraRepository carRepo) {
        this.em = em;
        this.estRepo = estRepo;
        this.carRepo = carRepo;
    }

    // =========================
    // ===== UTILIDADES CSV ====
    // =========================

    private List<String[]> readCsv(String classpath) {
        try {
            InputStream is = Thread.currentThread()
                    .getContextClassLoader()
                    .getResourceAsStream(classpath);
            if (is == null) {
                throw new IllegalArgumentException("No se encontró el recurso en classpath: " + classpath);
            }
            try (CSVReader r = new CSVReaderBuilder(new InputStreamReader(is, StandardCharsets.UTF_8))
                    .withSkipLines(0)
                    .build()) {
                return r.readAll();
            }
        } catch (Exception e) {
            throw new RuntimeException("Error leyendo CSV: " + classpath, e);
        }
    }

    private Map<String, Integer> headerIndex(String[] header) {
        Map<String, Integer> idx = new HashMap<>();
        for (int i = 0; i < header.length; i++) {
            idx.put(header[i].trim().toLowerCase(Locale.ROOT), i);
        }
        return idx;
    }

    private String get(Map<String, Integer> idx, String[] row, String... candidates) {
        for (String c : candidates) {
            Integer i = idx.get(c.toLowerCase(Locale.ROOT));
            if (i != null && i < row.length) {
                return row[i] == null ? null : row[i].trim();
            }
        }
        return null;
    }

    private Integer toInt(String s) {
        try {
            return (s == null || s.isBlank()) ? null : Integer.parseInt(s.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    // ===============================
    // ========== IMPORTS =============
    // ===============================

    /**
     * carrera.csv: id_carrera, carrera, duracion
     * - Usa "carrera" como nombre.
     * - Llena el mapa id_carrera(CSV) -> id entidad (DB).
     */
    public void importarCarreras(String classpathCsv) {
        List<String[]> all = readCsv(classpathCsv);
        if (all.isEmpty()) return;

        String[] header = all.get(0);
        Map<String, Integer> idx = headerIndex(header);

        em.getTransaction().begin();
        try {
            for (int r = 1; r < all.size(); r++) {
                String[] row = all.get(r);

                Integer idCsv = toInt(get(idx, row, "id_carrera"));
                String nombre = get(idx, row, "carrera", "nombre");

                if (nombre == null || nombre.isBlank()) continue;

                Long entityId;
                var existente = carRepo.porNombre(nombre);
                if (existente.isPresent()) {
                    entityId = existente.get().getId();
                } else {
                    Carrera c = new Carrera(nombre);
                    em.persist(c);        // persistimos directo (evitamos transacción anidada)
                    em.flush();           // asegura ID (IDENTITY) para llenar el mapa
                    entityId = c.getId();
                }

                if (idCsv != null) {
                    carreraCsvIdToEntityId.put(idCsv, entityId);
                }
            }
            em.getTransaction().commit();
        } catch (RuntimeException ex) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw ex;
        }
    }

    /**
     * estudiantes.csv: DNI, nombre, apellido, edad, genero, ciudad, LU
     * - Documento = DNI
     * - nroLibreta = LU
     */
    public void importarEstudiantes(String classpathCsv) {
        List<String[]> all = readCsv(classpathCsv);
        if (all.isEmpty()) return;

        String[] header = all.get(0);
        Map<String, Integer> idx = headerIndex(header);

        em.getTransaction().begin();
        try {
            for (int r = 1; r < all.size(); r++) {
                String[] row = all.get(r);

                String dni   = get(idx, row, "dni");
                String nom   = get(idx, row, "nombre", "nombres");
                String ape   = get(idx, row, "apellido");
                Integer edad = toInt(get(idx, row, "edad"));
                String gen   = get(idx, row, "genero");
                String ciu   = get(idx, row, "ciudad");
                String lu    = get(idx, row, "lu", "nro_libreta");

                if (dni == null || dni.isBlank()) continue;

                if (estRepo.porDocumento(dni).isEmpty()) {
                    Estudiante e = Estudiante.builder()
                            .nroLibreta(lu)
                            .nombres(nom)
                            .apellido(ape)
                            .edad(edad)
                            .genero(gen)
                            .documento(dni)
                            .ciudadResidencia(ciu)
                            .build();
                    em.persist(e); // persistimos directo (evita transacción dentro de otra)
                }
            }
            em.getTransaction().commit();
        } catch (RuntimeException ex) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw ex;
        }
    }

    /**
     * estudianteCarrera.csv: id, id_estudiante(DNI), id_carrera(CSV), inscripcion, graduacion, antiguedad
     * - Enlaza por DNI y por id_carrera del CSV (mapeado previamente con importarCarreras).
     * - inscripcion -> anioIngreso
     * - graduacion (si viene año) -> graduado=true + anioGraduacion
     * - antiguedad: se ignora (derivado)
     */
    public void importarEstudianteCarrera(String classpathCsv) {
        List<String[]> all = readCsv(classpathCsv);
        if (all.isEmpty()) return;

        String[] header = all.get(0);
        Map<String, Integer> idx = headerIndex(header);

        em.getTransaction().begin();
        try {
            for (int r = 1; r < all.size(); r++) {
                String[] row = all.get(r);

                String dniEst    = get(idx, row, "id_estudiante", "dni");
                Integer idCarCsv = toInt(get(idx, row, "id_carrera"));
                Integer anioIng  = toInt(get(idx, row, "inscripcion", "anio_ingreso"));
                Integer anioGrad = toInt(get(idx, row, "graduacion", "anio_graduacion"));
                // antiguedad -> ignorado

                if (dniEst == null || idCarCsv == null || anioIng == null) continue;

                var estOpt = estRepo.porDocumento(dniEst);
                if (estOpt.isEmpty()) continue;

                Long carreraEntityId = carreraCsvIdToEntityId.get(idCarCsv);
                if (carreraEntityId == null) {
                    // si no encontramos el mapping, no podemos enlazar (CSV desincronizados)
                    continue;
                }

                Long estId = estOpt.get().getId();

                // ¿ya existe esa matrícula?
                boolean existe = existeMatricula(estId, carreraEntityId, anioIng);
                if (!existe) {
                    Estudiante refE = em.getReference(Estudiante.class, estId);
                    Carrera refC    = em.getReference(Carrera.class, carreraEntityId);

                    EstudianteCarrera ec = EstudianteCarrera.builder()
                            .estudiante(refE)
                            .carrera(refC)
                            .anioIngreso(anioIng)
                            .graduado(false)
                            .build();
                    em.persist(ec);
                    // si trae año de graduación, se setea abajo
                }

                if (anioGrad != null) {
                    // actualizar la matrícula (recién creada o existente)
                    EstudianteCarrera ec = buscarMatricula(estId, carreraEntityId, anioIng);
                    if (ec != null) {
                        ec.setGraduado(true);
                        ec.setAnioGraduacion(anioGrad);
                        em.merge(ec);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (RuntimeException ex) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw ex;
        }
    }

    // ===============================
    // ====== HELPERS PRIVADOS =======
    // ===============================

    private boolean existeMatricula(Long estId, Long carId, Integer anio) {
        try {
            em.createQuery(
                            "SELECT ec.id FROM EstudianteCarrera ec " +
                                    "WHERE ec.estudiante.id = :eid AND ec.carrera.id = :cid AND ec.anioIngreso = :anio",
                            Long.class
                    )
                    .setParameter("eid", estId)
                    .setParameter("cid", carId)
                    .setParameter("anio", anio)
                    .setMaxResults(1)
                    .getSingleResult();
            return true;
        } catch (NoResultException ex) {
            return false;
        }
    }

    private EstudianteCarrera buscarMatricula(Long estId, Long carId, Integer anio) {
        try {
            return em.createQuery(
                            "SELECT ec FROM EstudianteCarrera ec " +
                                    "WHERE ec.estudiante.id = :eid AND ec.carrera.id = :cid AND ec.anioIngreso = :anio",
                            EstudianteCarrera.class
                    )
                    .setParameter("eid", estId)
                    .setParameter("cid", carId)
                    .setParameter("anio", anio)
                    .setMaxResults(1)
                    .getSingleResult();
        } catch (NoResultException ex) {
            return null;
        }
    }
}

package org.example.entrega_3.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.entrega_3.dto.CarreraConCantidadDTO;
import org.example.entrega_3.dto.ReporteCarreraAnualDTO;
import org.example.entrega_3.repository.InscripcionRepository;
import org.example.entrega_3.service.ReporteService;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ReporteServiceImpl implements ReporteService {

    private final InscripcionRepository inscripcionRepo;


    /*
    * Devuelve las carreras con cantidad de estudiantes actualmente inscriptos,
    * ordenadas de mayor a menor cantidad.
    *
    *
    * */
    @Override
    public List<CarreraConCantidadDTO> carrerasOrdenadasPorInscriptos() {
        return inscripcionRepo.carrerasOrdenadasPorInscriptos();
    }

    @Override
    public List<ReporteCarreraAnualDTO> reporteCarrerasAnual() {
        // clave: (carreraId, carreraNombre, año)
        record Key(Long id, String nombre, Integer anio) {}

        Map<Key, long[]> acc = new java.util.LinkedHashMap<>();

        // 1) inscriptos por anioEvento
        for (ReporteCarreraAnualDTO r : inscripcionRepo.inscriptosPorCarreraYAnioDTO()) {
            var k = new Key(r.getIdCarrera(), r.getCarrera(), r.getAnio());
            acc.computeIfAbsent(k, __ -> new long[]{0L, 0L})[0] += r.getInscriptos();
        }

        // 2) egresados por anioGraduacion
        for (ReporteCarreraAnualDTO r : inscripcionRepo.egresadosPorCarreraYAnioDTO()) {
            var k = new Key(r.getIdCarrera(), r.getCarrera(), r.getAnio());
            acc.computeIfAbsent(k, __ -> new long[]{0L, 0L})[1] += r.getEgresados();
        }

        // 3) a DTO ordenado: carrera A–Z, año asc
        return acc.entrySet().stream()
                .sorted(java.util.Comparator
                        .<java.util.Map.Entry<Key,long[]>, String>comparing(e -> e.getKey().nombre())
                        .thenComparingInt(e -> e.getKey().anio()))
                .map(e -> new ReporteCarreraAnualDTO(
                        e.getKey().id(),
                        e.getKey().nombre(),
                        e.getKey().anio(),
                        e.getValue()[0],
                        e.getValue()[1]
                ))
                .toList();
    }

}
package org.example.entrega_3.controller;

import lombok.RequiredArgsConstructor;
import org.example.entrega_3.dto.CarreraConCantidadDTO;
import org.example.entrega_3.dto.ReporteCarreraAnualDTO;
import org.example.entrega_3.service.ReporteService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/reporte")
@RequiredArgsConstructor
public class ReporteController {

    private final ReporteService service;

    // (f) Carreras con estudiantes inscriptos, ordenadas por cantidad desc
    @GetMapping("/carreras-inscriptos")
    public List<CarreraConCantidadDTO> carrerasOrdenadas() {
        return service.carrerasOrdenadasPorInscriptos();
    }

    // (h) Reporte por carrera y año: inscriptos y egresados
    @GetMapping("/carreras-anual")
    public List<ReporteCarreraAnualDTO> reporteCarrerasAnual() {
        return service.reporteCarrerasAnual();
    }
}
package org.example.entrega_3.service;


import org.example.entrega_3.dto.CarreraConCantidadDTO;
import org.example.entrega_3.dto.ReporteCarreraAnualDTO;

import java.util.List;

public interface ReporteService {
    List<CarreraConCantidadDTO> carrerasOrdenadasPorInscriptos(); //f
    List<ReporteCarreraAnualDTO> reporteCarrerasAnual(); //f
}
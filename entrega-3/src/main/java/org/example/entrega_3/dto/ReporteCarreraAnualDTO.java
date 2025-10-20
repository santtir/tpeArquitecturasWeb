package org.example.entrega_3.dto;

public class ReporteCarreraAnualDTO {
    private final Long idCarrera;
    private final String carrera;
    private final Integer anio;
    private final Long inscriptos;
    private final Long egresados;

    public ReporteCarreraAnualDTO(Long idCarrera, String carrera, Integer anio, Long inscriptos, Long egresados) {
        this.idCarrera = idCarrera;
        this.carrera = carrera;
        this.anio = anio;
        this.inscriptos = inscriptos;
        this.egresados = egresados;
    }

    public Long getIdCarrera() { return idCarrera; }
    public String getCarrera() { return carrera; }
    public Integer getAnio() { return anio; }
    public Long getInscriptos() { return inscriptos; }
    public Long getEgresados() { return egresados; }
}

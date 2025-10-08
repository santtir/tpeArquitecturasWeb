// dto/CarreraCantidadDTO.java
package org.example.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter @ToString @AllArgsConstructor
public class CarreraCantidadDTO {
    private String nombreCarrera;
    private Long cantidad;
}

package com.braiangzz.gestion_gastronomica.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO genérico para respuestas simples de confirmación.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MessageResponseDto {

    private String message;
}

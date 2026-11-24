package com.braiangzz.gestion_gastronomica.controller;

import com.braiangzz.gestion_gastronomica.dto.response.MessageResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Endpoint de prueba para verificar que la aplicación está activa.
 */
@RestController
@RequestMapping("/api/health")
@Tag(name = "Health", description = "Estado de la aplicación")
public class HealthController {

    @GetMapping
    @Operation(summary = "Verificar estado", description = "Devuelve un mensaje confirmando que la API responde")
    public ResponseEntity<MessageResponseDto> health() {
        return ResponseEntity.ok(new MessageResponseDto("API Gestión Gastronómica operativa"));
    }
}

package com.braiangzz.gestion_gastronomica.exception;

/**
 * Se lanza cuando un recurso solicitado no existe (HTTP 404).
 */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }
}

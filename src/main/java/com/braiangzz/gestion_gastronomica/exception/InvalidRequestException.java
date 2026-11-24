package com.braiangzz.gestion_gastronomica.exception;

/**
 * Se lanza cuando la petición contiene datos inválidos de entrada (HTTP 400).
 */
public class InvalidRequestException extends RuntimeException {

    public InvalidRequestException(String message) {
        super(message);
    }
}

package com.braiangzz.gestion_gastronomica.exception;

/**
 * Se lanza cuando se incumple una regla de negocio o hay conflicto de estado (HTTP 409).
 */
public class BusinessException extends RuntimeException {

    public BusinessException(String message) {
        super(message);
    }
}

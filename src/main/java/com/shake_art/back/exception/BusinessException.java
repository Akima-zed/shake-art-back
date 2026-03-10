package com.shake_art.back.exception;

/**
 * Exception metier pour les regles fonctionnelles violees.
 *
 * <p>Le code permet de categoriser finement l'erreur dans la reponse API.</p>
 */
public class BusinessException extends RuntimeException {

    private final String code;

    /**
     * Cree une exception metier avec le code par defaut {@code BUSINESS_ERROR}.
     */
    public BusinessException(String message) {
        this("BUSINESS_ERROR", message);
    }

    /**
     * Cree une exception metier avec un code explicite.
     */
    public BusinessException(String code, String message) {
        super(message);
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
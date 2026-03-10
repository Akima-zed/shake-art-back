package com.shake_art.back.exception;

/**
 * Exception technique pour les erreurs d'infrastructure (I/O, stockage, etc.).
 *
 * <p>Cette exception est differente d'une erreur metier: elle indique un
 * probleme technique cote serveur.</p>
 */
public class TechnicalException extends RuntimeException {

    public TechnicalException(String message) {
        super(message);
    }

    public TechnicalException(String message, Throwable cause) {
        super(message, cause);
    }
}

package com.shake_art.back.exception;

import java.time.Instant;
import java.util.Map;

/**
 * Contrat JSON unique des erreurs renvoyees par l'API.
 *
 * <p>Toutes les erreurs applicatives, de validation et de securite doivent
 * utiliser cette structure afin de garantir une consommation previsible cote
 * frontend.</p>
 */
public class ApiErrorResponse {

    private final Instant timestamp;
    private final int status;
    private final String error;
    private final String code;
    private final String message;
    private final String path;
    private final Map<String, Object> details;

    /**
     * Construit une reponse d'erreur standardisee.
     */
    public ApiErrorResponse(int status, String error, String code, String message, String path, Map<String, Object> details) {
        this.timestamp = Instant.now();
        this.status = status;
        this.error = error;
        this.code = code;
        this.message = message;
        this.path = path;
        this.details = details;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public int getStatus() {
        return status;
    }

    public String getError() {
        return error;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public String getPath() {
        return path;
    }

    public Map<String, Object> getDetails() {
        return details;
    }
}
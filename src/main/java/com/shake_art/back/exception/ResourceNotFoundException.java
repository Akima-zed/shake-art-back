package com.shake_art.back.exception;

/**
 * Exception levee quand une ressource demandee n'existe pas.
 */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }
}
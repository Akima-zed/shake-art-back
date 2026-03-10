package com.shake_art.back.exception;

/**
 * Exception metier (erreur fonctionnelle).
 *
 * <p>Elle est utilisee quand la requete est techniquement valide,
 * mais ne respecte pas une regle du domaine.
 * Exemple: fichier trop volumineux, valeur interdite, etat non autorise.</p>
 */
public class BusinessException extends RuntimeException {

    private final String code;

    /**
     * Cree une erreur metier avec le code par defaut {@code BUSINESS_ERROR}.
     */
    public BusinessException(String message) {
        this("BUSINESS_ERROR", message);
    }

    /**
     * Cree une erreur metier avec un code personnalise.
     */
    public BusinessException(String code, String message) {
        super(message);
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
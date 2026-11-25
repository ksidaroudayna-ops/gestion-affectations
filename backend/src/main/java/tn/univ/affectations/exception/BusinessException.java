package tn.univ.affectations.exception;

/** Violation d'une règle de gestion (RGx). */
public class BusinessException extends RuntimeException {
    private final String rule;
    public BusinessException(String rule, String message) { super(message); this.rule = rule; }
    public String getRule() { return rule; }
}

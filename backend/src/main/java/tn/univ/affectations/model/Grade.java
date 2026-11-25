package tn.univ.affectations.model;

/** Charge horaire annuelle maximale selon le grade (RG5). */
public enum Grade {
    PROFESSEUR(192), MAITRE_CONFERENCES(192), MAITRE_ASSISTANT(224), ASSISTANT(240), VACATAIRE(120);

    private final int chargeMax;
    Grade(int chargeMax) { this.chargeMax = chargeMax; }
    public int getChargeMax() { return chargeMax; }
}

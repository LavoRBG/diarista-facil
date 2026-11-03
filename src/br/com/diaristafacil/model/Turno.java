package br.com.diaristafacil.model;

/** Período do dia em que a diarista fica na casa do cliente. */
public enum Turno {
    MANHA("Manhã (7h às 12h)"),
    TARDE("Tarde (13h às 17h)"),
    INTEGRAL("Integral (7h às 17h)");

    private final String rotulo;

    Turno(String rotulo) { this.rotulo = rotulo; }

    /** Dois turnos se chocam quando são iguais ou quando um deles é integral. */
    public boolean conflitaCom(Turno outro) {
        return this == outro || this == INTEGRAL || outro == INTEGRAL;
    }

    @Override
    public String toString() { return rotulo; }
}

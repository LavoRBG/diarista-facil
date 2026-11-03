package br.com.diaristafacil.dao;

/** Lançada quando a diarista já tem compromisso no dia e turno escolhidos. */
public class AgendaOcupadaException extends Exception {
    public AgendaOcupadaException(String mensagem) {
        super(mensagem);
    }
}

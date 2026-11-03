package br.com.diaristafacil.model;

/** Profissional que executa as faxinas. */
public class Diarista {
    private int id;
    private String nome;
    private String telefone;
    private boolean ativa;

    public Diarista(int id, String nome, String telefone, boolean ativa) {
        this.id = id;
        this.nome = nome;
        this.telefone = telefone;
        this.ativa = ativa;
    }

    public int getId() { return id; }
    public String getNome() { return nome; }
    public String getTelefone() { return telefone; }
    public boolean isAtiva() { return ativa; }

    @Override
    public String toString() { return nome; }
}

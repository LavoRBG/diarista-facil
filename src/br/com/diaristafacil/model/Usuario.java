package br.com.diaristafacil.model;

/** Pessoa que acessa o sistema (a atendente ou uma das diaristas). */
public class Usuario {
    private int id;
    private String nome;
    private String login;
    private String perfil;

    public Usuario(int id, String nome, String login, String perfil) {
        this.id = id;
        this.nome = nome;
        this.login = login;
        this.perfil = perfil;
    }

    public int getId() { return id; }
    public String getNome() { return nome; }
    public String getLogin() { return login; }
    public String getPerfil() { return perfil; }
}

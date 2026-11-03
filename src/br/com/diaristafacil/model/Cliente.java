package br.com.diaristafacil.model;

/** Representa um cliente que contrata as faxinas. */
public class Cliente {
    private int id;
    private String nome;
    private String telefone;
    private String endereco;
    private String bairro;
    private String observacao;

    public Cliente() { }

    public Cliente(String nome, String telefone, String endereco, String bairro, String observacao) {
        this.nome = nome;
        this.telefone = telefone;
        this.endereco = endereco;
        this.bairro = bairro;
        this.observacao = observacao;
    }

    /** Verifica os campos obrigatórios e devolve a mensagem de erro, ou null se estiver tudo certo. */
    public String validar() {
        if (nome == null || nome.isBlank()) {
            return "Informe o nome do cliente.";
        }
        String digitos = telefone == null ? "" : telefone.replaceAll("\\D", "");
        if (digitos.length() < 10 || digitos.length() > 11) {
            return "Telefone inválido. Use DDD + número (10 ou 11 dígitos).";
        }
        if (endereco == null || endereco.isBlank()) {
            return "Informe o endereço onde será feita a faxina.";
        }
        return null;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone; }
    public String getEndereco() { return endereco; }
    public void setEndereco(String endereco) { this.endereco = endereco; }
    public String getBairro() { return bairro; }
    public void setBairro(String bairro) { this.bairro = bairro; }
    public String getObservacao() { return observacao; }
    public void setObservacao(String observacao) { this.observacao = observacao; }

    @Override
    public String toString() { return nome + " - " + bairro; }
}

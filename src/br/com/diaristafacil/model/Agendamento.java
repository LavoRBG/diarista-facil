package br.com.diaristafacil.model;

import java.time.LocalDate;

/** Faxina marcada para um cliente, com uma diarista, em uma data e turno. */
public class Agendamento {
    public static final String AGENDADO = "AGENDADO";
    public static final String CONCLUIDO = "CONCLUIDO";
    public static final String CANCELADO = "CANCELADO";

    private int id;
    private Cliente cliente;
    private Diarista diarista;
    private Servico servico;
    private LocalDate data;
    private Turno turno;
    private double valor;
    private String status = AGENDADO;
    private boolean pago;

    public Agendamento(Cliente cliente, Diarista diarista, Servico servico,
                       LocalDate data, Turno turno, double valor) {
        this.cliente = cliente;
        this.diarista = diarista;
        this.servico = servico;
        this.data = data;
        this.turno = turno;
        this.valor = valor;
    }

    public String validar() {
        if (cliente == null) return "Selecione o cliente.";
        if (diarista == null) return "Selecione a diarista.";
        if (servico == null) return "Selecione o tipo de serviço.";
        if (data == null) return "Informe uma data válida (dd/mm/aaaa).";
        if (turno == null) return "Selecione o turno.";
        if (valor <= 0) return "Informe o valor da faxina.";
        return null;
    }

    public void cancelar() { this.status = CANCELADO; }

    /** Muda a faxina de dia e/ou turno, mantendo cliente, diarista e serviço. */
    public void remarcar(LocalDate novaData, Turno novoTurno) {
        this.data = novaData;
        this.turno = novoTurno;
    }

    public void registrarPagamento() { this.pago = true; }
    public void concluir() { this.status = CONCLUIDO; }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public Cliente getCliente() { return cliente; }
    public Diarista getDiarista() { return diarista; }
    public Servico getServico() { return servico; }
    public LocalDate getData() { return data; }
    public Turno getTurno() { return turno; }
    public double getValor() { return valor; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public boolean isPago() { return pago; }
    public void setPago(boolean pago) { this.pago = pago; }
}

package model;

import java.time.LocalDate;

public class Emprestimo {
    private static final int PRAZO_DIAS = 14;

    private String isbnLivro;
    private String idUsuario;
    private LocalDate dataEmprestimo;
    private LocalDate dataDevolucao;
    private boolean ativo;

    public Emprestimo() {}

    public Emprestimo(String isbnLivro, String idUsuario) {
        this.isbnLivro = isbnLivro;
        this.idUsuario = idUsuario;
        this.dataEmprestimo = LocalDate.now();
        this.ativo = true;
    }

    public LocalDate getDataPrazo() {
        return this.dataEmprestimo.plusDays(PRAZO_DIAS);
    }

    public boolean estaAtrasado() {
        if (!ativo) return false;
        return LocalDate.now().isAfter(getDataPrazo());
    }

    public void encerrarEmprestimo() {
        this.ativo = false;
        this.dataDevolucao = LocalDate.now();
    }

    // Getters e Setters
    public String getIsbnLivro() { return isbnLivro; }
    public void setIsbnLivro(String isbnLivro) { this.isbnLivro = isbnLivro; }
    public String getIdUsuario() { return idUsuario; }
    public void setIdUsuario(String idUsuario) { this.idUsuario = idUsuario; }
    public LocalDate getDataEmprestimo() { return dataEmprestimo; }
    public void setDataEmprestimo(LocalDate dataEmprestimo) { this.dataEmprestimo = dataEmprestimo; }
    public LocalDate getDataDevolucao() { return dataDevolucao; }
    public void setDataDevolucao(LocalDate dataDevolucao) { this.dataDevolucao = dataDevolucao; }
    public boolean isAtivo() { return ativo; }
    public void setAtivo(boolean ativo) { this.ativo = ativo; }
}

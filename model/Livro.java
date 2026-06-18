package model;

public class Livro {
    private String titulo;
    private String autor;
    private String isbn;
    private int totalCopias;
    private int copiasDisponiveis;

    // Construtor padrão necessário para o Jackson (JSON)
    public Livro() {}

    public Livro(String titulo, String autor, String isbn, int totalCopias) {
        this.titulo = titulo;
        this.autor = autor;
        this.isbn = isbn;
        this.totalCopias = totalCopias;
        this.copiasDisponiveis = totalCopias;
    }

    public boolean emprestar() {
        if (this.copiasDisponiveis > 0) {
            this.copiasDisponiveis--;
            return true;
        }
        return false;
    }

    public void devolver() {
        if (this.copiasDisponiveis < this.totalCopias) {
            this.copiasDisponiveis++;
        }
    }

    // Getters e Setters (Essenciais para o Jackson mapear o JSON)
    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
    public String getAutor() { return autor; }
    public void setAutor(String autor) { this.autor = autor; }
    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) { this.isbn = isbn; }
    public int getTotalCopias() { return totalCopias; }
    public void setTotalCopias(int totalCopias) { this.totalCopias = totalCopias; }
    public int getCopiasDisponiveis() { return copiasDisponiveis; }
    public void setCopiasDisponiveis(int copiasDisponiveis) { this.copiasDisponiveis = copiasDisponiveis; }
}

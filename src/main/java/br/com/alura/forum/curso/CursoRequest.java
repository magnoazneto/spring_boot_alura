package br.com.alura.forum.curso;

public class CursoRequest {

    private final String nome;
    private final String categoria;

    public CursoRequest(String nome, String categoria) {
        this.nome = nome;
        this.categoria = categoria;
    }

    public String getNome() {
        return nome;
    }

    public String getCategoria() {
        return categoria;
    }

    public Curso toModel() {
        return new Curso(this.nome, this.categoria);
    }
}

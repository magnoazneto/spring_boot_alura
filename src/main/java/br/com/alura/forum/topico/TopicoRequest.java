package br.com.alura.forum.topico;

import br.com.alura.forum.curso.Curso;
import br.com.alura.forum.curso.CursoRepository;

public class TopicoRequest {

    private String titulo;
    private String mensagem;
    private String nomeCurso;

    public TopicoRequest(String titulo, String mensagem, String nomeCurso) {
        this.titulo = titulo;
        this.mensagem = mensagem;
        this.nomeCurso = nomeCurso;
    }

    public Topico toModel(CursoRepository cursoRepo){
        Curso curso = cursoRepo.findByNome(this.nomeCurso);
        return new Topico(this.titulo, this.mensagem, curso);
    }
    public String getTitulo() {
        return titulo;
    }

    public String getMensagem() {
        return mensagem;
    }

    public String getNomeCurso() {
        return nomeCurso;
    }
}

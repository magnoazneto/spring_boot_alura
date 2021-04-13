package br.com.alura.forum.topico;

import java.time.LocalDateTime;
import java.util.List;

public class TopicoRequest {

    private String titulo;
    private String mensagem;
    private Long autorId;
    private Long cursoId;
    private List<Integer> respostasIds;

    public TopicoRequest(String titulo,
                         String mensagem,
                         Long autorId,
                         Long cursoId,
                         List<Integer> respostasIds) {
        this.titulo = titulo;
        this.mensagem = mensagem;
        this.autorId = autorId;
        this.cursoId = cursoId;
        this.respostasIds = respostasIds;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getMensagem() {
        return mensagem;
    }

    public Long getAutorId() {
        return autorId;
    }

    public Long getCursoId() {
        return cursoId;
    }

    public List<Integer> getRespostasIds() {
        return respostasIds;
    }
}

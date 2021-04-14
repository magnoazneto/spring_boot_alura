package br.com.alura.forum.topico;

import br.com.alura.forum.resposta.RespostaDto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class TopicoDetalheDto {

    private final Long id;
    private final String titulo;
    private final String mensagem;
    private final LocalDateTime dataCriacao;
    private final String nomeAutor;
    private final StatusTopico status;
    private final List<RespostaDto> respostas;

    public TopicoDetalheDto(Topico topico) {
        this.id = topico.getId();
        this.titulo = topico.getTitulo();
        this.mensagem = topico.getMensagem();
        this.dataCriacao = topico.getDataCriacao();
        this.nomeAutor = topico.getAutor().getNome();
        this.status = topico.getStatus();
        this.respostas = new ArrayList<>();
        this.respostas.addAll(topico.getRespostas().stream().map(RespostaDto::new).collect(Collectors.toList()));
    }

    public Long getId() {
        return id;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getMensagem() {
        return mensagem;
    }

    public LocalDateTime getDataCriacao() {
        return dataCriacao;
    }

    public String getNomeAutor() {
        return nomeAutor;
    }

    public StatusTopico getStatus() {
        return status;
    }

    public List<RespostaDto> getRespostas() {
        return respostas;
    }
}

package br.com.alura.forum.topico;

import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;

public class TopicoPutFormRequest {
    @NotBlank
    @Length(min = 5)
    private final String titulo;

    @NotBlank @Length(min = 10)
    private final String mensagem;

    public TopicoPutFormRequest(@NotBlank @Length(min = 5) String titulo,
                                @NotBlank @Length(min = 10) String mensagem) {
        this.titulo = titulo;
        this.mensagem = mensagem;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getMensagem() {
        return mensagem;
    }

    public Topico atualizar(Long id, TopicoRepository topicoRepo) {
        Topico topico = topicoRepo.getOne(id);

        topico.setTitulo(this.titulo);
        topico.setMensagem(this.mensagem);

        return topico;
    }
}

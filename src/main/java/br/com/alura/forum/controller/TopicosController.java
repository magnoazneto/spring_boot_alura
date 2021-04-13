package br.com.alura.forum.controller;

import br.com.alura.forum.modelo.Curso;
import br.com.alura.forum.modelo.Topico;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/topicos")
public class TopicosController {

    @GetMapping("/all")
    public List<Topico> listaTopicos(){
        Topico topico = new Topico("Duvida",
                "Duvida com Spring",
                new Curso("Spring", "Programacao"));

        return Arrays.asList(topico, topico, topico);
    }
}

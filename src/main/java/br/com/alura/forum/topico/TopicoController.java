package br.com.alura.forum.topico;

import br.com.alura.forum.modelo.Curso;
import br.com.alura.forum.modelo.Topico;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/topicos")
public class TopicoController {

    @GetMapping("/all")
    public List<TopicoDto> listaTopicos(){
        Topico topico = new Topico("Duvida",
                "Duvida sobre Spring",
                new Curso("Spring Boot", "Programacao"));

        return TopicoDto.converter(Arrays.asList(topico, topico, topico));
    }
}

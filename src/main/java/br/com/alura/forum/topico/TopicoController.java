package br.com.alura.forum.topico;

import br.com.alura.forum.curso.Curso;
import br.com.alura.forum.curso.CursoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/topico")
public class TopicoController {

    @Autowired
    private TopicoRepository topicoRepo;

    private CursoRepository cursoRepo;

    @GetMapping("/all")
    public List<TopicoDto> listaTopicos(){
        List<Topico> topicos = topicoRepo.findAll();
        return TopicoDto.converter(topicos);
    }
}

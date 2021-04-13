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

    @GetMapping
    public List<TopicoDto> listaTopico(String nomeCurso){
        List<Topico> topicos = null;
        if(nomeCurso == null){
            topicos = topicoRepo.findAll();
        } else{
            topicos = topicoRepo.findByCurso_Nome(nomeCurso);
        }
        return TopicoDto.converter(topicos);
    }
}

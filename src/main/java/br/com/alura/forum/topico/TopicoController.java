package br.com.alura.forum.topico;

import br.com.alura.forum.curso.Curso;
import br.com.alura.forum.curso.CursoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/topico")
public class TopicoController {

    @Autowired
    private TopicoRepository topicoRepo;

    @Autowired
    private CursoRepository cursoRepo;

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

    @PostMapping
    public ResponseEntity<TopicoDto> criaTopico(@RequestBody TopicoRequest request, UriComponentsBuilder uriBuilder){
        Topico topico = request.toModel(cursoRepo);
        topicoRepo.save(topico);

        URI uri = uriBuilder.path("/topico/{id}").buildAndExpand(topico.getId()).toUri();
        return ResponseEntity.created(uri).body(new TopicoDto(topico));
    }
}

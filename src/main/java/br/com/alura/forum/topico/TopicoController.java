package br.com.alura.forum.topico;

import br.com.alura.forum.curso.Curso;
import br.com.alura.forum.curso.CursoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import javax.transaction.Transactional;
import javax.validation.Valid;
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

    @GetMapping("/{id}")
    public TopicoDetalheDto detalhaTopico(@PathVariable Long id){
        Topico topico = topicoRepo.getOne(id);
        return new TopicoDetalheDto(topico);
    }

    @PostMapping
    @Transactional
    public ResponseEntity<TopicoDto> criaTopico(@RequestBody @Valid TopicoRequest request, UriComponentsBuilder uriBuilder){
        Topico topico = request.toModel(cursoRepo);
        topicoRepo.save(topico);

        URI uri = uriBuilder.path("/topico/{id}").buildAndExpand(topico.getId()).toUri();
        return ResponseEntity.created(uri).body(new TopicoDto(topico));
    }

    @PutMapping("/{id}")
    @Transactional
    public ResponseEntity<TopicoDto> atualizaTopico(@PathVariable Long id,
                                                    @RequestBody @Valid TopicoPutFormRequest request){
        Topico topicoAtualizado = request.atualizar(id, topicoRepo);

        return ResponseEntity.ok(new TopicoDto(topicoAtualizado));
    }

    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity<?> removeTopico(@PathVariable Long id){
        topicoRepo.deleteById(id);
        return ResponseEntity.ok().build();
    }
}

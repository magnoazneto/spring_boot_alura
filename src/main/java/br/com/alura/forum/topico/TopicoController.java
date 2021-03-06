package br.com.alura.forum.topico;

import br.com.alura.forum.curso.CursoRepository;
import br.com.alura.forum.utils.MailSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import javax.transaction.Transactional;
import javax.validation.Valid;
import java.net.URI;
import java.util.Optional;

@RestController
@RequestMapping("/topico")
public class TopicoController {

    @Autowired
    private TopicoRepository topicoRepo;

    @Autowired
    private CursoRepository cursoRepo;

    @Autowired
    private MailSender mailSender;



    @GetMapping
    @Cacheable(value = "listaDeTopicos")
    public Page<TopicoDto> listaTopico(@RequestParam(required = false) String nomeCurso,
                                       Pageable paginacao){

        if(nomeCurso == null){
            Page<Topico> topicos = topicoRepo.findAll(paginacao);
            return TopicoDto.converter(topicos);
        }

        Page<Topico> topicos = topicoRepo.findByCurso_Nome(nomeCurso, paginacao);

        mailSender.enviar();
        return TopicoDto.converter(topicos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TopicoDetalheDto> detalhaTopico(@PathVariable Long id){
        Optional<Topico> topico = topicoRepo.findById(id);

        return topico.map(value -> {
            return ResponseEntity.ok(new TopicoDetalheDto(value));
        }).orElseGet(() -> ResponseEntity.notFound().build());

    }

    @PostMapping
    @Transactional
    @CacheEvict(value = "listaDeTopicos", allEntries = true)
    public ResponseEntity<TopicoDto> criaTopico(@RequestBody @Valid TopicoRequest request, UriComponentsBuilder uriBuilder){
        Topico topico = request.toModel(cursoRepo);
        topicoRepo.save(topico);

        URI uri = uriBuilder.path("/topico/{id}").buildAndExpand(topico.getId()).toUri();
        return ResponseEntity.created(uri).body(new TopicoDto(topico));
    }

    @PutMapping("/{id}")
    @Transactional
    @CacheEvict(value = "listaDeTopicos", allEntries = true)
    public ResponseEntity<TopicoDto> atualizaTopico(@PathVariable Long id,
                                                    @RequestBody @Valid TopicoPutFormRequest request){

        Optional<Topico> possivelTopico = topicoRepo.findById(id);
        return possivelTopico.map(value -> {
            Topico topicoAtualizado = request.atualizar(id, topicoRepo);
            return ResponseEntity.ok(new TopicoDto(value));
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @Transactional
    @CacheEvict(value = "listaDeTopicos", allEntries = true)
    public ResponseEntity<?> removeTopico(@PathVariable Long id){

        Optional<Topico> possivelTopico = topicoRepo.findById(id);
        return possivelTopico.map(value -> {
            topicoRepo.deleteById(id);
            return ResponseEntity.ok().build();
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }
}

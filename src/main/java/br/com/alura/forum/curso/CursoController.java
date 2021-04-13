package br.com.alura.forum.curso;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;

@RestController
@RequestMapping("/curso")
public class CursoController {

    @Autowired
    EntityManager manager;

    @PostMapping("/novo")
    @Transactional
    public CursoDto criaCurso(@RequestBody CursoRequest request){
        Curso novoCurso = request.toModel();
        manager.persist(novoCurso);
        return new CursoDto(novoCurso);
    }
}

package br.com.alura.forum.usuario;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import javax.validation.Valid;

@RestController
@RequestMapping("/usuario")
public class UsuarioController {

    @Autowired
    EntityManager manager;

    @PostMapping("/novo")
    @Transactional
    public UsuarioDto criaUsuario(@RequestBody @Valid UsuarioRequest usuario){
        Usuario novoUsuario = usuario.toModel();
        manager.persist(novoUsuario);
        return new UsuarioDto(novoUsuario);
    }
}

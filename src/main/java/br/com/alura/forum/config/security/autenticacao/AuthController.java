package br.com.alura.forum.config.security.autenticacao;

import br.com.alura.forum.config.security.autenticacao.dto.LoginRequest;
import br.com.alura.forum.config.security.autenticacao.dto.TokenDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/auth")
@Profile(value = {"prod", "test"})
public class AuthController {

    @Autowired
    private AuthenticationManager authManager;

    @Autowired
    private TokenService tokenService;

    @PostMapping
    public ResponseEntity<TokenDto> autenticar(@RequestBody @Valid LoginRequest request){
        UsernamePasswordAuthenticationToken dadosLogin = request.converter();

        Authentication authentication = authManager.authenticate(dadosLogin);
        String token = tokenService.gerarToken(authentication);

        return ResponseEntity.ok(new TokenDto(token, "Bearer"));
    }
}

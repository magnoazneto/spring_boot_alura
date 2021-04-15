package br.com.alura.forum.config.security.autenticacao;


import br.com.alura.forum.usuario.Usuario;
import br.com.alura.forum.usuario.UsuarioRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.Date;
import java.util.Optional;

@Service
public class TokenService {

    @Value("${forum.jwt.expiration}")
    private String expiration;

    @Value("${forum.jwt.secret}")
    private String secret;

    @Autowired
    private UsuarioRepository usuarioRepository;

    public String gerarToken(Authentication authentication) {

        Usuario logado = (Usuario) authentication.getPrincipal();
        Date hoje = new Date();
        Date dataExpiracao = new Date(hoje.getTime() + Long.parseLong(expiration));

        return  Jwts.builder()
                .setIssuer("API do fórum da Alura")
                .setSubject(logado.getId().toString())
                .setIssuedAt(hoje)
                .setExpiration(dataExpiracao)
                .signWith(SignatureAlgorithm.HS256, secret)
                .compact();
    }

    public boolean isTokenValido(String token) {
        try{
            Jwts.parser().setSigningKey(this.secret).parseClaimsJws(token);
            return true;
        } catch(Exception e){
            return false;
        }
    }

    public Usuario getUsuario(String token) {
        Claims claims = Jwts.parser().setSigningKey(this.secret).parseClaimsJws(token).getBody();
        Optional<Usuario> usuario = usuarioRepository.findById(Long.parseLong(claims.getSubject()));
        Assert.notNull(usuario, "Problema ao encontrar usuario.");
        return usuario.get();
    }
}

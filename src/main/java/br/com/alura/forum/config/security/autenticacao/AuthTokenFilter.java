package br.com.alura.forum.config.security.autenticacao;

import br.com.alura.forum.usuario.Usuario;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class AuthTokenFilter extends OncePerRequestFilter {

    private TokenService tokenService;

    public AuthTokenFilter(TokenService tokenService){
        this.tokenService = tokenService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String token = recuperarToken(request);
        boolean valido = tokenService.isTokenValido(token);
        if(valido){
            autenticarCliente(token);
        }

        filterChain.doFilter(request, response);
    }

    private void autenticarCliente(String token) {
        Usuario usuario = tokenService.getUsuario(token);

        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(usuario,
                null,
                usuario.getAuthorities());

        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    private String recuperarToken(HttpServletRequest request) {
        String token = request.getHeader("Authorization");

        if (token == null || token.isEmpty() || !token.startsWith("Bearer")) {
            return null;
        }

        String tokeOut = token.substring(7, token.length());
        return tokeOut;

    }
}

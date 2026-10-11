package CRUD.MarcenariaRA.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    public JwtAuthenticationFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String authorizationHeader = request.getHeader("Authorization");

        System.out.println("========== JWT ==========");
        System.out.println("URL: " + request.getRequestURI());
        System.out.println("AUTH EXISTE: " + (authorizationHeader != null));

        if (authorizationHeader == null ||
                !authorizationHeader.startsWith("Bearer ")) {

            System.out.println("SEM JWT");

            filterChain.doFilter(request, response);
            return;
        }

        String token = authorizationHeader.substring(7);

        if (jwtService.tokenValido(token)) {

            System.out.println("JWT VÁLIDO!");

            String email = jwtService.extrairEmail(token);
            System.out.println("EMAIL: " + email);

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            email,
                            null,
                            Collections.emptyList()
                    );

            SecurityContextHolder
                    .getContext()
                    .setAuthentication(authentication);

        } else {
            System.out.println("JWT INVÁLIDO!");
        }

        filterChain.doFilter(request, response);
    }
}
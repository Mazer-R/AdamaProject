package com.adama.backoffice.security.filter;

import com.adama.backoffice.security.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    public JwtAuthenticationFilter(JwtService jwtService, UserDetailsService userDetailsService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");

        // 1. Verificar el header Authorization
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            final String jwt = authHeader.substring(7);

            // 2. Validar el token JWT
            if (!jwtService.validateToken(jwt)) {
                log.warn("JWT inválido o expirado");
                filterChain.doFilter(request, response);
                return;
            }

            // 3. Extraer información del token
            String username = jwtService.extractUsername(jwt);
            if (username == null) {
                log.warn("JWT no contiene un username válido");
                filterChain.doFilter(request, response);
                return;
            }

            // 4. Cargar UserDetails desde la base de datos
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            // 5. Crear autenticación con los authorities del UserDetails
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    userDetails, null, userDetails.getAuthorities() // Usamos los authorities de la BD
                    );

            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authentication);

            log.debug("Usuario autenticado: {} con roles: {}", username, userDetails.getAuthorities());

        } catch (Exception e) {
            log.error("Error en el proceso de autenticación", e);
        }

        filterChain.doFilter(request, response);
    }
}

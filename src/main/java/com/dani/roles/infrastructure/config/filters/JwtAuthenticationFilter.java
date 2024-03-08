package com.dani.roles.infrastructure.config.filters;

import com.dani.roles.infrastructure.adapters.output.persistence.entities.UserEntity;
import com.dani.roles.infrastructure.config.jwt.JwtUtil;
import com.dani.roles.infrastructure.config.service.UserDetailsServiceImpl;
import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/*
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private JwtUtil jwtUtil;

    public JwtAuthenticationFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {


        UserEntity userEntity = null;

        String username = "";
        String password = "";

        try {
            userEntity = new ObjectMapper().readValue(request.getInputStream(), UserEntity.class);
            username = userEntity.getUsername();
            password = userEntity.getPassword();
        } catch (StreamReadException e) {
            throw new RuntimeException(e);
        } catch (DatabindException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username, password);

        return getAuthenticationManager().authenticate(authenticationToken);
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {

        // Obtener los detalles del usuario
        User user = (User) authResult.getPrincipal();
        // generar el token de acceso usando nuestro JwtUtil
        String token = jwtUtil.generateAccessToken(user.getUsername());
        // agregar el token al encabezado de la respuesta
        response.addHeader("Authorization", token);
        Map<String, Object> httpResponse = new HashMap<>();
        httpResponse.put("token", token);
        httpResponse.put("Message", "Authentication successful");
        httpResponse.put("User", user.getUsername());

        response.getWriter().write(new ObjectMapper().writeValueAsString(httpResponse));
        response.setStatus(HttpStatus.OK.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().flush();
        super.successfulAuthentication(request, response, chain, authResult);
    }
}
 */

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil tokenProvider;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // obtenemos el token de la solicitud HTTP
        String token = obtenerJwtDeLaSolicitud(request);

        // validamos el token
        if (StringUtils.hasText(token) && tokenProvider.isTokenValid(token)) {
            // obtenemos el username del token
            String username = tokenProvider.getUsernameFromToken(token);

            // cargamos el usuario asociado al token
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);


            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

            authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            // establecemos la seguridad
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        }

        filterChain.doFilter(request, response);
    }


    // Bearer Token de acceso
    private String obtenerJwtDeLaSolicitud(HttpServletRequest request){
        String bearerToken = request.getHeader("Authorization");

        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer")) {

            return bearerToken.substring(7,bearerToken.length());
        }
        return null;
    }



}

# Crear la clase JWTUtil
Nos proveera los metodos necesarios para trabajar con el Token JWT

# Por cierto debemos instalar las dependencias de JWT
```xml
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-api</artifactId>
    <version>0.11.5</version>
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-impl</artifactId>
    <version>0.11.5</version>
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-jackson</artifactId>
    <version>0.11.5</version>
</dependency>
```

# Terminando el JwtUtil
```java
package com.dani.roles.infrastructure.config.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.function.Function;

@Component
@Slf4j
public class JwtUtil {
    @Value("${jwt.secret.key}")
    private String jwtSecretKey;
    @Value("${jwt.time.expiration}")
    private String timeExpiration;



    //TODO 1: Generar token de accesso
    public String generateAccessToken(String username){
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + Long.parseLong(timeExpiration)))
                .signWith(getSignatureKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    //TODO 1.1: Obtener firma del token -> signWith del generarAccessTOken
    // enviaremos una firma encriptada para volver a encriptarla en el generateAccessToken
    public Key getSignatureKey(){
        byte[] KeyBytes = Decoders.BASE64.decode(jwtSecretKey);
        return Keys.hmacShaKeyFor(KeyBytes);
    }

    //TODO 2: Validar token de accesso
    public boolean isTokenValid(String token){
        try{
            Jwts.parserBuilder()
                    .setSigningKey(getSignatureKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            return true;
        } catch (Exception e){
            log.error("token invalido: {}", e.getMessage());
            return false;
        }
    }

    //TODO 3: Obtener todos los CLAIMS(INFORMACION) dentro del token
    public Claims extraerTodaLaInformacionDelToken(String token){
        return Jwts.parserBuilder()
                .setSigningKey(getSignatureKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // TODO 4: Una vez obtenido todos los claims, podemos obtener uno solo
    public <T> T getClaim(String token, Function<Claims, T> claimsResolver) {
        Claims claims = extraerTodaLaInformacionDelToken(token);
        return claimsResolver.apply(claims);
    }

    // TODO 5: Obtener el usuario del token
    public String getUsernameFromToken(String token){
        return getClaim(token, Claims::getSubject);
    }
}
```

# A continuacion debemos crear 2 FILTROS
- Uno para Authentication
- Otro para Authorization

## EN la raiz de la carpeta config, crearemos una carpeta filters
Y crearemos 2 clases
- JwtAuthenticationFilter

```java
package com.dani.roles.infrastructure.config.filters;

import com.dani.roles.infrastructure.adapters.output.persistence.entities.UserEntity;
import com.dani.roles.infrastructure.config.security.jwt.JwtUtil;
import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

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
```
## Una vez creado el filtro de autenticacion, CREAREMOS NUESTRO SERVICE DE USERDETAILSERVICEIMPL
```java
package com.dani.roles.infrastructure.config.service;

import com.dani.roles.application.service.UserService;
import com.dani.roles.infrastructure.adapters.output.persistence.entities.RoleEntity;
import com.dani.roles.infrastructure.adapters.output.persistence.entities.UserEntity;
import com.dani.roles.infrastructure.adapters.output.persistence.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository repository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity userDetails =  repository.findByUsername(username);

        return User.withUsername(userDetails.getUsername())
                .password(userDetails.getPassword())
                .authorities(mapearRoles(userDetails.getRoles()))
                .build();
    }

    private Collection<? extends GrantedAuthority> mapearRoles(Set<RoleEntity> roles) {

        return roles.stream()
                .map(rol -> new SimpleGrantedAuthority("ROLE_" + rol.getName()))
                .collect(Collectors.toList());
    }
}
```

## UNA VEZ HECHO ESTO, NOS DIRIGIMOS A NUESTRO SECURITYCONFIG PARA AGREGARLO

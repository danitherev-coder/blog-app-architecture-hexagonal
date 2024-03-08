package com.dani.roles.infrastructure.config.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
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
    public String generateAccessToken(Authentication authentication){
        String username = authentication.getName();
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

package br.com.jopaulo.apispring.service;

import java.io.IOException;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import br.com.jopaulo.apispring.ApplicationContextLoad;
import br.com.jopaulo.apispring.model.Usuario;
import br.com.jopaulo.apispring.repository.UsuarioRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Service
@Component
public class JWTTokenAutenticacaoService {

	private static final long EXPIRATION_TIME =	7200000; // 2 horas
	
	private static final String SECRET = "SenhaExtremamenteSecreta";
	
	private static final String TOKEN_PREFIX = "Bearer";
	
	private static final String HEADER_STRING = "Authorization";
	
	// gera o token de autenticação e add ao cabeçalho e resposta http
	public void addAuthentication(HttpServletResponse response, String username) throws IOException {
		
		// gera o token
		String JWT = Jwts.builder()
				.setSubject(username)
				.setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
				.signWith(SignatureAlgorithm.HS512, SECRET).compact();
		
		// junta o token com o prefixo
		String token = TOKEN_PREFIX + " " + JWT;
		
		// add no cabeçalho http
		response.addHeader(HEADER_STRING, token);
		
		// escreve o token como respota no corpo http
		response.getWriter().write("{\"Authorization\": \""+token+"\"}");
	}
	
	public Authentication getAuthentication(HttpServletRequest request) {
		
		String token = request.getHeader(HEADER_STRING);
		
		if (token != null) {
			String user = Jwts.parser().setSigningKey(SECRET).parseClaimsJws(token.replace(TOKEN_PREFIX, "")).getBody().getSubject();
			
			if (user != null) {
				Usuario usuario = ApplicationContextLoad.getApplicationContext().getBean(UsuarioRepository.class).findUserByLogin(user);
				
				if (usuario != null) {					
					return new UsernamePasswordAuthenticationToken(usuario.getLogin(), usuario.getPassword(), usuario.getAuthorities());
				}				
			} 			
		} 
		
		return null;
	}
}

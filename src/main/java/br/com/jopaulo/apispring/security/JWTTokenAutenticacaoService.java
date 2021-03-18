package br.com.jopaulo.apispring.security;

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
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Service
@Component
public class JWTTokenAutenticacaoService {

	private static final long EXPIRATION_TIME =	86400000; // 1 dia
	
	private static final String SECRET = "SenhaExtremamenteSecreta";
	
	private static final String TOKEN_PREFIX = "Bearer";
	
	private static final String HEADER_STRING = "Authorization";
	
	// gera o token de autenticação e add ao cabeçalho e resposta http
	public void addAuthentication(HttpServletResponse response, String username) throws IOException {
		
		// monta o token
		String JWT = Jwts.builder()
				.setSubject(username)
				.setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
				.signWith(SignatureAlgorithm.HS512, SECRET).compact();
		
		// junta o token com o prefixo
		String token = TOKEN_PREFIX + " " + JWT;
		
		// add no cabeçalho http
		response.addHeader(HEADER_STRING, token);
		
		ApplicationContextLoad.getApplicationContext().getBean(UsuarioRepository.class).atualizaTokenUser(JWT, username);
		
		// libera resposta para portas diferentes que usam a api ou clientes web
		liberacaoCors(response);
		
		// escreve o token como respota no corpo http
		response.getWriter().write("{\"Authorization\": \""+token+"\"}");
	}
	
	// retorna usuário válido com token ou null
	public Authentication getAuthentication(HttpServletRequest request, HttpServletResponse response) {
		
		String token = request.getHeader(HEADER_STRING);
		
		try {
			if (token != null) {
				
				String tokenLimpo = token.replace(TOKEN_PREFIX, "").trim();
				
				String user = Jwts.parser().setSigningKey(SECRET).parseClaimsJws(tokenLimpo).getBody().getSubject();
				
				if (user != null) {
					Usuario usuario = ApplicationContextLoad.getApplicationContext().getBean(UsuarioRepository.class).findUserByLogin(user);
					
					if (usuario != null) {					
						
						if (tokenLimpo.equalsIgnoreCase(usuario.getToken())) {
							
							return new UsernamePasswordAuthenticationToken(
									usuario.getLogin(),
									usuario.getPassword(),
									usuario.getAuthorities());
						}
					}				
				} 			
			}
		} catch (ExpiredJwtException e) {
			try {
				response.getOutputStream().println("Sua sessão expirou.  Efetue login novamente");
			} catch (IOException e1) {}
		} 
		
		liberacaoCors(response);
		
		return null;
	}

	private void liberacaoCors(HttpServletResponse response) {

		if (response.getHeader("Access-Control-Allow-Origin") == null) {
			response.addHeader("Access-Control-Allow-Origin", "*");		
		}
		
		if (response.getHeader("Access-Control-Allow-Headers") == null) {
			response.addHeader("Access-Control-Allow-Headers", "*");		
		}
		
		if (response.getHeader("Access-Control-Request-Headers") == null) {
			response.addHeader("Access-Control-Request-Headers", "*");		
		}
		
		if (response.getHeader("Access-Control-Allow-Methods") == null) {
			response.addHeader("Access-Control-Allow-Methods", "*");		
		}
	}
}

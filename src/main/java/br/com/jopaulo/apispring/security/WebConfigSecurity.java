package br.com.jopaulo.apispring.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import br.com.jopaulo.apispring.service.ImplementacaoUserDetailsService;

@Configurable
@EnableWebSecurity
public class WebConfigSecurity extends WebSecurityConfigurerAdapter {
	
	@Autowired
	private ImplementacaoUserDetailsService implementacaoUserDetailsService;
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		
		// ativa a proteção contra usuários que não estão validados por token
		http.csrf().csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
		// ativa a permissão para acesso da página inicial (login)
		.disable().authorizeRequests().antMatchers("/").permitAll()
		.antMatchers("/index").permitAll()
		// liberando CORS
		.antMatchers(HttpMethod.OPTIONS, "/**").permitAll()
		// redireciona para a página de login após o usuário fazer o logout
		.anyRequest().authenticated().and().logout().logoutSuccessUrl("/index")
		// invalida o usuário
		.logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
		// filtra requisições de login para autenticação
		.and().addFilterBefore(new JWTLoginFilter("/login", authenticationManager()), UsernamePasswordAuthenticationFilter.class)
		// Filtra demais requisições para verificar a presença do TOKEN JWT no HEADER HTTP
		.addFilterBefore(new JWTAPIAutenticacaoFilter(), UsernamePasswordAuthenticationFilter.class);		
	}
	
	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		
		auth.userDetailsService(implementacaoUserDetailsService)
			.passwordEncoder(new BCryptPasswordEncoder());
	}

}

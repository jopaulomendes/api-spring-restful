package br.com.jopaulo.apispring.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.com.jopaulo.apispring.model.Usuario;

@RestController
@RequestMapping(value = "/usuario")
public class IndexController {
	
	@GetMapping(value = "/", produces = "application/json")
	public ResponseEntity<Usuario> init() {
		
		Usuario usuario = new Usuario();
		usuario.setId(50L);
		usuario.setNome("João Paulo da Mata Mendes");
		usuario.setLogin("jp_cbc@hotmail.com");
		usuario.setSenha("1234");
		
		Usuario usuario2 = new Usuario();
		usuario.setId(55L);
		usuario.setNome("Adnari Mendes");
		usuario.setLogin("adnari@hotmail.com");
		usuario.setSenha("abcd");
		
		List<Usuario> usuarios = new ArrayList<Usuario>();
		usuarios.add(usuario);
		usuarios.add(usuario2);
		
		return new ResponseEntity(usuarios, HttpStatus.OK);
	}

}

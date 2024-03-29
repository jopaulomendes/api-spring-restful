package br.com.jopaulo.apispring.controller;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;

import br.com.jopaulo.apispring.model.Usuario;
import br.com.jopaulo.apispring.model.UsuarioDTO;
import br.com.jopaulo.apispring.repository.UsuarioRepository;

@RestController
@RequestMapping(value = "/usuario")
public class IndexController {
	
	@Autowired
	private UsuarioRepository usuarioRepository;
	
	@GetMapping(value = "/{id}/relatoriopdf", produces = "application/pdf")
	public ResponseEntity<Usuario> relatorio(@PathVariable (value = "id") Long id) {
		
		Optional<Usuario> usuario = usuarioRepository.findById(id);
		
		return new ResponseEntity<Usuario>(usuario.get(), HttpStatus.OK);
	}
	
	@GetMapping(value = "/{id}", produces = "application/json")
	@CacheEvict(value = "cacheuser", allEntries = true)
	@CachePut("cacheusuarios")
	public ResponseEntity<UsuarioDTO> init(@PathVariable (value = "id") Long id) {
		
		Optional<Usuario> usuario = usuarioRepository.findById(id);
		
		return new ResponseEntity<UsuarioDTO>(new UsuarioDTO(usuario.get()), HttpStatus.OK);
	}
	
	@DeleteMapping(value = "/{id}", produces = "application/text")
	public String delete (@PathVariable("id") Long id) {
		
		usuarioRepository.deleteById(id);
		
		return "Usuário de código " + id + " deletado com sucesso";
	}
	
	@GetMapping(value = "/", produces = "application/json")
	@CacheEvict(value = "cacheusuarios", allEntries = true)
	@CachePut("cacheusuarios")
	public ResponseEntity<List<Usuario>> usuario() throws InterruptedException{
		
		List<Usuario> list = (List<Usuario>) usuarioRepository.findAll();
		
		return new ResponseEntity<List<Usuario>>(list, HttpStatus.OK);
	}
	
	// consulta de usuário por nome
	@GetMapping(value = "/pesquisarUsuario/{nome}", produces = "application/json")
	@CacheEvict(value = "cacheusuarios", allEntries = true)
	@CachePut("cacheusuarios")
	public ResponseEntity<List<Usuario>> usuarioPorNome(@PathVariable("nome") String nome) throws InterruptedException{
		
		List<Usuario> list = (List<Usuario>) usuarioRepository.findUserByNome(nome);
		
		return new ResponseEntity<List<Usuario>>(list, HttpStatus.OK);
	}
	
	@PostMapping(value = "/", produces = "application/json")
	public ResponseEntity<Usuario> cadastrar(@RequestBody Usuario usuario) throws Exception {
		
		for (int pos = 0; pos < usuario.getTelefones().size(); pos++) {
			usuario.getTelefones().get(pos).setUsuario(usuario);
		}
		
//		************** consumindo api externa de cep *******************
//		URL url = new URL("https://viacep.com.br/ws/"+ usuario.getCep() +"/json/");
//		URLConnection connection = url.openConnection();
//		InputStream inputStream = connection.getInputStream();
//		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
//		
//		String cep = "";
//		StringBuilder jsonCep = new StringBuilder();
//		
//		while ((cep = bufferedReader.readLine()) != null) {
//			jsonCep.append(cep);
//			
//		}
		
		//converte os dados da api externa para a classe usuário
//		Usuario userAux = new Gson().fromJson(jsonCep.toString(), Usuario.class);  		
//		usuario.setCep(userAux.getCep());
//		usuario.setLogradouro(userAux.getLogradouro());
//		usuario.setComplemento(userAux.getComplemento());
//		usuario.setBairro(userAux.getBairro());
//		usuario.setLocalidade(userAux.getLocalidade());
//		usuario.setUf(userAux.getUf());
//		usuario.setDdd(userAux.getDdd());
//		*************** consumindo api externa de cep ****************************
		
		String senhaCriptografada = new BCryptPasswordEncoder().encode(usuario.getSenha());
		usuario.setSenha(senhaCriptografada);
		Usuario  usuarioSalvo = usuarioRepository.save(usuario);
		
		return new ResponseEntity<Usuario>(usuarioSalvo, HttpStatus.OK);		
	}
	
	@PutMapping(value = "/", produces = "application/json")
	public ResponseEntity<Usuario> atualizar(@RequestBody Usuario usuario) {
		
		for (int pos = 0; pos < usuario.getTelefones().size(); pos++) {
			usuario.getTelefones().get(pos).setUsuario(usuario);
		}
		
		Usuario userTemporario = usuarioRepository.findById(usuario.getId()).get();
		
		if (!userTemporario.getSenha().equals(usuario.getSenha())) {
			String senhaCriptografada = new BCryptPasswordEncoder().encode(usuario.getSenha());
			usuario.setSenha(senhaCriptografada);
		}
		
		Usuario  usuarioSalvo = usuarioRepository.save(usuario);
		
		return new ResponseEntity<Usuario>(usuarioSalvo, HttpStatus.OK);		
	}

}

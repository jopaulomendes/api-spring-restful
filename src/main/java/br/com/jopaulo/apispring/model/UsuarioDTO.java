package br.com.jopaulo.apispring.model;

import java.io.Serializable;

public class UsuarioDTO implements Serializable {

	private static final long serialVersionUID = 1L;

	private String login;
	private String nome;
	private String cpf;
	private String senha;

	public UsuarioDTO(Usuario usuario) {
		super();
		this.login = usuario.getLogin();
		this.nome = usuario.getNome();
		this.cpf = usuario.getCpf();
		this.senha = usuario.getSenha();
	}

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getCpf() {
		return cpf;
	}

	public void setCpf(String cpf) {
		this.cpf = cpf;
	}

	public String getSenha() {
		return senha;
	}

	public void setSenha(String senha) {
		this.senha = senha;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}
}

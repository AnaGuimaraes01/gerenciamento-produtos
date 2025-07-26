package com.pedidos.model;

public class Cliente {
    private int id;
    private String nome;
    private String email;
    private String cpf; //adicionando CPF para o CRUD

public Cliente (int id, String nome, String email, String cpf){
    this.id = id;
    this.nome = nome;
    this.email = email;
    this.cpf = cpf;
}
    public int getId() {
        return id;     
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getNome() {
        return nome;
    }
    public void setNome(String nome) {
        this.nome = nome;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public String getCpf() {
        return cpf;
    }
    public void setCpf(String cpf) {
        this.cpf = cpf;
    }
}

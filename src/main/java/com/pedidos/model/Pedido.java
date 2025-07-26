package com.pedidos.model;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class Pedido {
    private int id;
    private int clienteId;
    private BigDecimal valorTotal;
    private String status;
    private Timestamp dataCriacao;
    private String codigo; // adicionando para usar no CRUD

    public Pedido(int id, int clienteId, BigDecimal valorTotal, String status, Timestamp dataCriacao, String codigo) {
        this.id = id;
        this.clienteId = clienteId;
        this.valorTotal = valorTotal;
        this.status = status;
        this.dataCriacao = dataCriacao;
        this.codigo = codigo;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getClienteId() {
        return clienteId;
    }

    public void setClienteId(int clienteId) {
        this.clienteId = clienteId;
    }

    public BigDecimal getValorTotal() {
        return valorTotal;
    }

    public void setValorTotal(BigDecimal valorTotal) {
        this.valorTotal = valorTotal;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Timestamp getDataCriacao() {
        return dataCriacao;
    }

    public void setDataCriacao(Timestamp dataCriacao) {
        this.dataCriacao = dataCriacao;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }
}
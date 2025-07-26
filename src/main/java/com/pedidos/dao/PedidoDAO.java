package com.pedidos.dao;

import com.pedidos.model.Pedido;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PedidoDAO {
    private Connection connection;
    public PedidoDAO(Connection connection) {
        this.connection = connection;
    }

    //CRUD abaixo (inserir, listar, atualizar e deletar)
    public void inserirPedido(Pedido pedido) {
        String sql = "INSERT INTO pedidos (cliente_id, valor_total, status, data_criacao, codigo) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, pedido.getClienteId());
            stmt.setBigDecimal(2, pedido.getValorTotal());
            stmt.setString(3, pedido.getStatus());
            stmt.setTimestamp(4, pedido.getDataCriacao());
            stmt.setString(5, pedido.getCodigo());
            stmt.executeUpdate();
            System.out.println("Pedido cadastrado com sucesso.");
        } catch (SQLException e) {
            System.out.println("Erro ao cadastrar pedido: " + e.getMessage());
        }
    }

    public List<Pedido> listarPedidos() {
        List<Pedido> pedidos = new ArrayList<>();
        String sql = "SELECT * FROM pedidos ORDER BY id";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Pedido pedido = new Pedido(
                        rs.getInt("id"),
                        rs.getInt("cliente_id"),
                        rs.getBigDecimal("valor_total"),
                        rs.getString("status"),
                        rs.getTimestamp("data_criacao"),
                        rs.getString("codigo"));
                pedidos.add(pedido);
            }
        } catch (SQLException e) {
            System.out.println("Erro ao listar pedidos: " + e.getMessage());
        }
        return pedidos;
    }

    public void atualizarPedido(Pedido pedido) {
    String sql = "UPDATE pedidos SET cliente_id = ?, valor_total = ?, status = ?, data_criacao = ? WHERE codigo = ?";
    try (PreparedStatement stmt = connection.prepareStatement(sql)) {
        stmt.setInt(1, pedido.getClienteId());
        stmt.setBigDecimal(2, pedido.getValorTotal());
        stmt.setString(3, pedido.getStatus());
        stmt.setTimestamp(4, pedido.getDataCriacao());
        stmt.setString(5, pedido.getCodigo());

        int linhas = stmt.executeUpdate();

        if (linhas > 0) {
            System.out.println("Pedido atualizado com sucesso.");
        } else {
            System.out.println("Pedido não encontrado para atualização.");
        }
    } catch (SQLException e) {
        System.out.println("Erro ao atualizar pedido: " + e.getMessage());
    }
}

    public void deletarPedido(String codigo) {
    String sql = "DELETE FROM pedidos WHERE codigo = ?";
    try (PreparedStatement stmt = connection.prepareStatement(sql)) {
        stmt.setString(1, codigo);
        int linhas = stmt.executeUpdate();

        if (linhas > 0) {
            System.out.println("Pedido removido com sucesso.");
        } else {
            System.out.println("Pedido não encontrado para remoção.");
        }
    } catch (SQLException e) {
        System.out.println("Erro ao deletar pedido: " + e.getMessage());
    }
}

    // Classe auxiliar para resultado da contagem por status
    public static class StatusCount {
        private String status;
        private int quantidade;

        public StatusCount(String status, int quantidade) {
            this.status = status;
            this.quantidade = quantidade;
        }

        public String getStatus() {
            return status;
        }

        public int getQuantidade() {
            return quantidade;
        }
    }
}

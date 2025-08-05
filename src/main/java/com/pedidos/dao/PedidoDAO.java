package com.pedidos.dao;

import com.pedidos.model.Pedido;
import com.pedidos.model.Produto;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PedidoDAO {
    private Connection connection;
    public PedidoDAO(Connection connection) {
        this.connection = connection;
    }

    // CRUD abaixo (inserir, listar, atualizar e deletar)
    public void inserirPedido(Pedido pedido, List<Produto> produtosComprados, List<Integer> quantidades) {
        String sqlPedido = "INSERT INTO pedidos (cliente_id, valor_total, status, data_criacao, codigo) VALUES (?, ?, ?, ?, ?) RETURNING id";
        String sqlInfo = "INSERT INTO informacoes_pedidos (pedido_id, produto_id, quantidade) VALUES (?, ?, ?)"; // necessário inserir também na tab intermediária

        try (PreparedStatement stmtPedido = connection.prepareStatement(sqlPedido)) {
            // Insere o pedido e pega o ID gerado
            stmtPedido.setInt(1, pedido.getClienteId());
            stmtPedido.setBigDecimal(2, pedido.getValorTotal());
            stmtPedido.setString(3, pedido.getStatus());
            stmtPedido.setTimestamp(4, pedido.getDataCriacao());
            stmtPedido.setString(5, pedido.getCodigo());

            ResultSet rs = stmtPedido.executeQuery();
            int pedidoId = 0;
            if (rs.next()) {
                pedidoId = rs.getInt("id");
            }

            // Insere os produtos do pedido na tabela informacoes_pedidos
            try (PreparedStatement stmtInfo = connection.prepareStatement(sqlInfo)) {
                for (int i = 0; i < produtosComprados.size(); i++) {
                    Produto p = produtosComprados.get(i);
                    int qtd = quantidades.get(i);

                    stmtInfo.setInt(1, pedidoId);
                    stmtInfo.setInt(2, p.getId());
                    stmtInfo.setInt(3, qtd);
                    stmtInfo.addBatch();
                }
                stmtInfo.executeBatch();
            }

            System.out.println("Pedido e itens cadastrados com sucesso.");
        } catch (SQLException e) {
            System.out.println("Erro ao cadastrar pedido e itens: " + e.getMessage());
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

    // Método para consultar a view com as informações dos pedido
    public void listarInformacoesCompletasPedidos() {
        String sql = "SELECT * FROM informacoes_completas_pedidos";

        try (Statement stmt = connection.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                System.out.println(
                        "Cliente ID: " + rs.getInt("cliente_id") +
                                " | Pedido ID: " + rs.getInt("pedido_id") +
                                " | Data: " + rs.getTimestamp("data_criacao") +
                                " | Cliente: " + rs.getString("nome_cliente") +
                                " | Produto: " + rs.getString("nome_produto") +
                                " | Preço: " + rs.getBigDecimal("preco_unitario") +
                                " | Quantidade: " + rs.getInt("quantidade") +
                                " | Subtotal: " + rs.getBigDecimal("subtotal"));
            }
        } catch (SQLException e) {
            System.out.println("Erro ao consultar as informações: " + e.getMessage());
        }
    }

    // Método para chamar a procedure
    public void atualizarStatusPedido(String codigoPedido, String novoStatus) {
        String sql = "CALL atualizar_status_pedido(?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, codigoPedido);
            stmt.setString(2, novoStatus);
            stmt.execute();
            System.out.println("Status do pedido atualizado (via Procedure).");
        } catch (SQLException e) {
            System.out.println("Erro ao atualizar status: " + e.getMessage());
        }
    }
}

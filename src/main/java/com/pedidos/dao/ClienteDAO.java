package com.pedidos.dao;

import com.pedidos.model.Cliente;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ClienteDAO {
    private Connection connection;
    public ClienteDAO(Connection connection) {
        this.connection = connection;
    }

    // CRUD abaixo (inserir, listar, atualizar e deletar)
    public void inserirCliente(Cliente cliente) {
        String sql = "INSERT INTO clientes (nome, email, cpf) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, cliente.getNome());
            stmt.setString(2, cliente.getEmail());
            stmt.setString(3, cliente.getCpf());
            stmt.executeUpdate();
            System.out.println("Cliente cadastrado com sucesso.");
        } catch (SQLException e) {
            System.out.println("Erro ao cadastrar cliente: " + e.getMessage());
        }
    }

    public List<Cliente> listarClientes() {
        List<Cliente> clientes = new ArrayList<>();
        String sql = "SELECT * FROM clientes ORDER BY id";
        try (Statement stmt = connection.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Cliente cliente = new Cliente(
                        rs.getInt("id"),
                        rs.getString("nome"),
                        rs.getString("email"),
                        rs.getString("cpf"));
                clientes.add(cliente);
            }
        } catch (SQLException e) {
            System.out.println("Erro ao listar clientes: " + e.getMessage());
        }
        return clientes;
    }

    public void atualizarCliente(Cliente cliente) {
        String sql = "UPDATE clientes SET email = ? WHERE cpf = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, cliente.getEmail());
            stmt.setString(2, cliente.getCpf()); // usa CPF no WHERE

            int linhas = stmt.executeUpdate();

            if (linhas > 0) {
                System.out.println("Cliente atualizado com sucesso.");
            } else {
                System.out.println("Cliente não encontrado para atualização.");
            }
        } catch (SQLException e) {
            System.out.println("Erro ao atualizar cliente: " + e.getMessage());
        }
    }

    public void deletarCliente(String cpf) {
        String sql = "DELETE FROM clientes WHERE cpf = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, cpf); // usa CPF no WHERE
            int linhas = stmt.executeUpdate();

            if (linhas > 0) {
                System.out.println("Cliente removido com sucesso.");
            } else {
                System.out.println("Cliente não encontrado para remoção.");
            }
        } catch (SQLException e) {
            System.out.println("Erro ao deletar cliente: " + e.getMessage());
        }
    }

    // Uso do JOIN e SUM
    public double consultarTotalGastoPorCliente(String cpf) {
        String sql = "SELECT c.nome, COALESCE(SUM(p.valor_total), 0) AS total " +
                "FROM clientes c " +
                "JOIN pedidos p ON c.id = p.cliente_id " +
                "WHERE c.cpf = ? " +
                "GROUP BY c.nome";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, cpf);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String nome = rs.getString("nome");
                    double total = rs.getDouble("total");
                    System.out.println("Cliente: " + nome + ", Total gasto: " + total);
                    return total;
                } else {
                    System.out.println("Cliente não encontrado ou sem pedidos.");
                }
            }
        } catch (SQLException e) {
            System.out.println("Erro ao consultar total gasto: " + e.getMessage());
        }
        return 0;
    }

    // Chamada da FUNCTION PostgreSQL (total de pedidos do cliente)
    public int consultarTotalPedidosCliente(String cpf) {
        String sql = "SELECT obter_total_pedidos(?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, cpf);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1);
            } else {
                System.out.println("Nenhum pedido encontrado para o cliente.");
            }
        } catch (SQLException e) {
            System.out.println("Erro ao consultar total de pedidos: " + e.getMessage());
        }
        return 0;
    }

}

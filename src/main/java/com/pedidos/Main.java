package com.pedidos;

import com.pedidos.util.Conexao;
import java.sql.Connection;
import java.sql.SQLException;

public class Main {
    public static void main(String[] args) {
        try {
            Connection conn = Conexao.conectar();
            if (conn != null) {
                System.out.println("Conexão bem-sucedida com o banco de dados!");
                conn.close();
            }
        } catch (SQLException e) {
            System.out.println("Erro ao conectar com o banco: " + e.getMessage());
        }
    }
}
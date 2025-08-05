package com.pedidos;

import com.pedidos.dao.*;
import com.pedidos.util.Conexao;
import com.pedidos.menu.*;

import java.sql.Connection;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws Exception {
        Connection connection = Conexao.conectar();
        ClienteDAO clienteDAO = new ClienteDAO(connection);
        ProdutoDAO produtoDAO = new ProdutoDAO(connection);
        PedidoDAO pedidoDAO = new PedidoDAO(connection);
        Scanner sc = new Scanner(System.in);

        while (true) {
            System.out.println("\n  MENU PRINCIPAL");
            System.out.println("1 - Gerenciar Clientes");
            System.out.println("2 - Gerenciar Produtos");
            System.out.println("3 - Gerenciar Pedidos");
            System.out.println("0 - Sair");
            System.out.print("Escolha: ");
            String opcao = sc.nextLine();

            switch (opcao) {
                case "0":
                    connection.close();
                    sc.close();
                    System.out.println("Encerrando o sistema...");
                    return;
                case "1":
                    MenuCliente.exibirMenu(sc, clienteDAO);
                    break;
                case "2":
                    MenuProduto.exibirMenu(sc, produtoDAO);
                    break;
                case "3":
                    MenuPedido.exibirMenu(sc, pedidoDAO, produtoDAO, clienteDAO);
                    break;
                default:
                    System.out.println("Opção inválida!");
            }
        }
    }
}

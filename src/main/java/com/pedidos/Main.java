package com.pedidos;

import com.pedidos.dao.*;
import com.pedidos.model.*;
import com.pedidos.util.Conexao;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
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
                    menuClientes(sc, clienteDAO);
                    break;
                case "2":
                    menuProdutos(sc, produtoDAO);
                    break;
                case "3":
                    menuPedidos(sc, pedidoDAO, produtoDAO);
                    break;
                default:
                    System.out.println("Opção inválida!");
            }
        }
    }

    //GERêNCIA DE CLIENTES
    private static void menuClientes(Scanner sc, ClienteDAO dao) {
        System.out.println("\n  GERÊNCIA DE CLIENTES");
        System.out.println("1 - Cadastrar");
        System.out.println("2 - Listar");
        System.out.println("3 - Atualizar");
        System.out.println("4 - Deletar");
        System.out.println("5 - Consultar Total Gasto");
        System.out.println("6 - Consultar Total de Pedidos");
        System.out.print("Escolha: ");
        String opcao = sc.nextLine();

        switch (opcao) {
            case "1":
                System.out.print("Nome: ");
                String nome = sc.nextLine();
                System.out.print("Email: ");
                String email = sc.nextLine();
                System.out.print("CPF: ");
                String cpf = sc.nextLine();
                dao.inserirCliente(new Cliente(0, nome, email, cpf));
                break;

            case "2":
                dao.listarClientes().forEach(c ->
                        System.out.println(c.getId() + " - " + c.getNome() + " | " + c.getEmail() + " | CPF:" + c.getCpf()));
                break;

            case "3":
                System.out.print("CPF do cliente: ");
                cpf = sc.nextLine();
                System.out.print("Novo nome: ");
                nome = sc.nextLine();
                System.out.print("Novo email: ");
                email = sc.nextLine();
                dao.atualizarCliente(new Cliente(0, nome, email, cpf));
                break;

            case "4":
                System.out.print("CPF do cliente a deletar: ");
                dao.deletarCliente(sc.nextLine());
                break;

            case "5":
                System.out.print("CPF do cliente: ");
                dao.consultarTotalGastoPorCliente(sc.nextLine());
                break;

            case "6":
                System.out.print("CPF do cliente: ");
                int total = dao.consultarTotalPedidosCliente(sc.nextLine());
                System.out.println("Total de pedidos: " + total);
                break;

            default:
                System.out.println("Opção inválida!");
        }
    }

    //GERÊNCIA DE PRODUTOS
    private static void menuProdutos(Scanner sc, ProdutoDAO dao) {
        System.out.println("\n  GERÊNCIA DE PRODUTOS");
        System.out.println("1 - Cadastrar");
        System.out.println("2 - Listar");
        System.out.println("3 - Atualizar");
        System.out.println("4 - Deletar");
        System.out.print("Escolha: ");
        String opcao = sc.nextLine();

        switch (opcao) {
            case "1":
                System.out.print("Nome: ");
                String nome = sc.nextLine();
                System.out.print("Preço: ");
                BigDecimal preco = new BigDecimal(sc.nextLine());
                System.out.print("Quantidade: ");
                int qtd = Integer.parseInt(sc.nextLine());
                dao.inserirProduto(new Produto(0, nome, preco, qtd));
                break;

            case "2":
                dao.listarProdutos().forEach(p ->
                        System.out.println(p.getId() + " - " + p.getNome() + " | R$" + p.getPreco() + " | Estoque:" + p.getQuantidade()));
                break;

            case "3":
                System.out.print("ID do produto: ");
                int id = Integer.parseInt(sc.nextLine());
                System.out.print("Novo nome: ");
                nome = sc.nextLine();
                System.out.print("Novo preço: ");
                preco = new BigDecimal(sc.nextLine());
                System.out.print("Nova quantidade: ");
                qtd = Integer.parseInt(sc.nextLine());
                dao.atualizarProduto(new Produto(id, nome, preco, qtd));
                break;

            case "4":
                System.out.print("ID do produto a deletar: ");
                dao.deletarProduto(Integer.parseInt(sc.nextLine()));
                break;

            default:
                System.out.println("Opção inválida!");
        }
    }

    //GERÊNCIA DE PEDIDOS
    private static void menuPedidos(Scanner sc, PedidoDAO pedidoDAO, ProdutoDAO produtoDAO) {
        System.out.println("\n  GERÊNCIA DE PEDIDOS");
        System.out.println("1 - Cadastrar");
        System.out.println("2 - Listar");
        System.out.println("3 - Atualizar");
        System.out.println("4 - Deletar");
        System.out.println("5 - Listar Informações Completas (View)");
        System.out.println("6 - Atualizar Status (Procedure)");
        System.out.print("Escolha: ");
        String opcao = sc.nextLine();

        switch (opcao) {
            case "1":
                cadastrarPedido(sc, pedidoDAO, produtoDAO);
                break;

            case "2":
                pedidoDAO.listarPedidos().forEach(p ->
                        System.out.println(p.getId() + " | Cliente:" + p.getClienteId() + " | Total:" + p.getValorTotal() + " | " + p.getStatus()));
                break;

            case "3":
                System.out.print("Código do pedido: ");
                String codigo = sc.nextLine();
                System.out.print("Novo status: ");
                String status = sc.nextLine();
                System.out.print("Novo valor total: ");
                BigDecimal total = new BigDecimal(sc.nextLine());
                Pedido p = new Pedido(0, 0, total, status, new Timestamp(System.currentTimeMillis()), codigo);
                pedidoDAO.atualizarPedido(p);
                break;

            case "4":
                System.out.print("Código do pedido a deletar: ");
                pedidoDAO.deletarPedido(sc.nextLine());
                break;

            case "5":
                pedidoDAO.listarInformacoesCompletasPedidos();
                break;

            case "6":
                System.out.print("Código do pedido: ");
                codigo = sc.nextLine();
                System.out.print("Novo status: ");
                status = sc.nextLine();
                pedidoDAO.atualizarStatusPedido(codigo, status);
                break;

            default:
                System.out.println("Opção inválida!");
        }
    }

    private static void cadastrarPedido(Scanner sc, PedidoDAO pedidoDAO, ProdutoDAO produtoDAO) {
        System.out.print("ID do cliente: ");
        int clienteId = Integer.parseInt(sc.nextLine());

        List<Produto> produtos = produtoDAO.listarProdutos();
        if (produtos.isEmpty()) {
            System.out.println("Nenhum produto cadastrado!");
            return;
        }

        List<Produto> comprados = new ArrayList<>();
        List<Integer> quantidades = new ArrayList<>();
        BigDecimal total = BigDecimal.ZERO;

        while (true) {
            produtos.forEach(prod -> System.out.println(prod.getId() + " - " + prod.getNome() +
                    " (R$" + prod.getPreco() + ") Estoque:" + prod.getQuantidade()));
            System.out.print("ID do produto (0 para finalizar): ");
            int idProd = Integer.parseInt(sc.nextLine());
            if (idProd == 0) break;

            Produto escolhido = produtos.stream().filter(pr -> pr.getId() == idProd).findFirst().orElse(null);
            if (escolhido == null) {
                System.out.println("Produto inválido!");
                continue;
            }

            System.out.print("Quantidade: ");
            int qtd = Integer.parseInt(sc.nextLine());
            comprados.add(escolhido);
            quantidades.add(qtd);
            total = total.add(escolhido.getPreco().multiply(BigDecimal.valueOf(qtd)));
        }

        System.out.print("Código do pedido: ");
        String codigo = sc.nextLine();
        Pedido pedido = new Pedido(0, clienteId, total, "PENDENTE", new Timestamp(System.currentTimeMillis()), codigo);
        pedidoDAO.inserirPedido(pedido, comprados, quantidades);
    }
}

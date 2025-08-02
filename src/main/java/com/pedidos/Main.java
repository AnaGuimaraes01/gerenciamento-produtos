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
import java.text.Normalizer;

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
                    menuPedidos(sc, pedidoDAO, produtoDAO, clienteDAO);
                    break;
                default:
                    System.out.println("Opção inválida!");
            }
        }
    }

    // GERÊNCIA CLIENTES
    private static void menuClientes(Scanner sc, ClienteDAO dao) {
        System.out.println("\n  GERÊNCIA DE CLIENTES");
        System.out.println("1 - Cadastrar");
        System.out.println("2 - Listar");
        System.out.println("3 - Atualizar (apenas e-mail)");
        System.out.println("4 - Deletar");
        System.out.println("5 - Consultar Total Gasto");
        System.out.println("6 - Consultar Total de Pedidos");
        System.out.print("Escolha: ");
        String opcao = sc.nextLine();

        try {
            switch (opcao) {
                case "1":
                    String nome = lerNome(sc, "Nome");
                    String email = lerEmail(sc, "Email");
                    String cpf = lerCpf(sc, "CPF");

                    boolean cpfJaExiste = dao.listarClientes().stream().anyMatch(c -> c.getCpf().equals(cpf));
                    if (cpfJaExiste) {
                        System.out.println("CPF já cadastrado! Operação cancelada.");
                        return;
                    }
                    dao.inserirCliente(new Cliente(0, nome, email, cpf));
                    break;

                case "2":
                    System.out.println("\nClientes cadastrados:");
                    dao.listarClientes().forEach(c -> System.out
                            .println(c.getId() + " - " + c.getNome() + " | " + c.getEmail() + " | CPF:" + c.getCpf()));
                    break;

                case "3":
                    listarClientes(dao);
                    System.out.print("\nCPF do cliente: ");
                    String cpfUpdate = sc.nextLine();
                    Cliente clienteExistente = dao.listarClientes().stream()
                            .filter(c -> c.getCpf().equals(cpfUpdate))
                            .findFirst().orElse(null);
                    if (clienteExistente == null) {
                        System.out.println("Cliente não encontrado!");
                        return;
                    }
                    String novoEmail;
                    while (true) {
                        System.out.print("Digite o novo email (ou 0 para cancelar): ");
                        novoEmail = sc.nextLine();
                        if (novoEmail.equals("0") || novoEmail.isBlank()) {
                            novoEmail = clienteExistente.getEmail();
                            break;
                        } else if (novoEmail.matches("^[\\w-.]+@[\\w-]+\\.[A-Za-z]{2,}$")) {
                            break;
                        } else {
                            System.out.println("E-mail inválido! Digite novamente ou 0 para manter o anterior.");
                        }
                    }
                    dao.atualizarCliente(new Cliente(0, clienteExistente.getNome(), novoEmail, cpfUpdate));
                    break;

                case "4":
                    listarClientes(dao);
                    System.out.print("\nCPF do cliente a deletar: ");
                    dao.deletarCliente(sc.nextLine());
                    break;

                case "5":
                    listarClientes(dao);
                    System.out.print("CPF do cliente: ");
                    dao.consultarTotalGastoPorCliente(sc.nextLine());
                    break;

                case "6":
                    listarClientes(dao);
                    System.out.print("CPF do cliente: ");
                    int total = dao.consultarTotalPedidosCliente(sc.nextLine());
                    System.out.println("Total de pedidos: " + total);
                    break;

                default:
                    System.out.println("Opção inválida!");
            }
        } catch (Exception e) {
            System.out.println("Erro na operação! Voltando ao menu principal.");
        }
    }

    private static void listarClientes(ClienteDAO dao) {
        System.out.println("\nClientes cadastrados:");
        dao.listarClientes().forEach(c -> System.out.println(c.getNome() + " | CPF: " + c.getCpf()));
    }

    // GERÊNCIA PRODUTOS
    private static void menuProdutos(Scanner sc, ProdutoDAO dao) {
        System.out.println("\n  GERÊNCIA DE PRODUTOS");
        System.out.println("1 - Cadastrar");
        System.out.println("2 - Listar");
        System.out.println("3 - Atualizar (Preço/Estoque)");
        System.out.println("4 - Deletar");
        System.out.print("Escolha: ");
        String opcao = sc.nextLine();

        try {
            switch (opcao) {
                case "1":
                    String nome = lerNome(sc, "Nome");
                    BigDecimal preco = lerPreco(sc);
                    int qtd = lerInteiro(sc, "Quantidade");
                    dao.inserirProduto(new Produto(0, nome, preco, qtd));
                    break;

                case "2":
                    listarProdutos(dao);
                    break;

                case "3":
                    listarProdutos(dao);
                    System.out.print("ID do produto: ");
                    int idProd = Integer.parseInt(sc.nextLine());
                    Produto produtoExistente = dao.listarProdutos().stream().filter(p -> p.getId() == idProd)
                            .findFirst().orElse(null);
                    if (produtoExistente == null) {
                        System.out.println("Produto não encontrado!");
                        return;
                    }
                    while (true) {
                        System.out.print("Novo preço (S para manter): ");
                        String inputPreco = sc.nextLine();
                        if (inputPreco.equalsIgnoreCase("S") || inputPreco.isBlank()) {
                            break;
                        }
                        try {
                            produtoExistente.setPreco(new BigDecimal(inputPreco));
                            break;
                        } catch (Exception e) {
                            System.out.println("Preço inválido! Digite novamente ou S para manter.");
                        }
                    }

                    while (true) {
                        System.out.print("Nova quantidade (S para manter): ");
                        String inputQtd = sc.nextLine();
                        if (inputQtd.equalsIgnoreCase("S") || inputQtd.isBlank()) {
                            break;
                        }
                        try {
                            produtoExistente.setQuantidade(Integer.parseInt(inputQtd));
                            break;
                        } catch (Exception e) {
                            System.out.println("Quantidade inválida! Digite novamente ou S para manter.");
                        }
                    }

                    dao.atualizarProduto(produtoExistente);
                    break;

                case "4":
                    listarProdutos(dao);
                    System.out.print("ID do produto a deletar: ");
                    dao.deletarProduto(Integer.parseInt(sc.nextLine()));
                    break;

                default:
                    System.out.println("Opção inválida!");
            }
        } catch (Exception e) {
            System.out.println("Erro na operação! Voltando ao menu principal.");
        }
    }

    private static void listarProdutos(ProdutoDAO dao) {
        System.out.println("\nProdutos cadastrados:");
        dao.listarProdutos().forEach(p -> System.out.println(
                p.getId() + " - " + p.getNome() + " | R$" + p.getPreco() + " | Estoque: " + p.getQuantidade()));
    }

    // GERÊNCIA PEDIDOS
    private static void menuPedidos(Scanner sc, PedidoDAO pedidoDAO, ProdutoDAO produtoDAO, ClienteDAO clienteDAO) {
        System.out.println("\n GERÊNCIA DE PEDIDOS");
        System.out.println("1 - Cadastrar");
        System.out.println("2 - Listar");
        System.out.println("3 - Deletar");
        System.out.println("4 - Listar Informações Completas (View)");
        System.out.println("5 - Atualizar Status (Procedure)");
        System.out.print("Escolha: ");
        String opcao = sc.nextLine();

        try {
            switch (opcao) {
                case "1": {
                    try {
                        listarClientes(clienteDAO);
                        System.out.print("CPF do cliente (0 para cancelar): ");
                        String cpf = sc.nextLine();
                        if (cpf.equals("0"))
                            return;

                        Cliente cliente = clienteDAO.listarClientes().stream().filter(c -> c.getCpf().equals(cpf))
                                .findFirst().orElse(null);

                        if (cliente == null) {
                            System.out.println("Cliente não encontrado! Operação cancelada.");
                            return;
                        }

                        List<Produto> produtos = produtoDAO.listarProdutos();
                        if (produtos.isEmpty()) {
                            System.out.println("Nenhum produto cadastrado!");
                            return;
                        }

                        List<Produto> comprados = new ArrayList<>();
                        List<Integer> quantidades = new ArrayList<>();
                        BigDecimal total = BigDecimal.ZERO;

                        while (true) {
                            listarProdutos(produtoDAO);
                            int idProduto = lerInteiro(sc, "ID do produto (0 para finalizar)");
                            if (idProduto == 0)
                                break;

                            Produto escolhido = produtos.stream().filter(pr -> pr.getId() == idProduto).findFirst()
                                    .orElse(null);

                            if (escolhido == null) {
                                System.out.println("Produto inválido!");
                                continue;
                            }

                            int qtd = lerInteiro(sc, "Quantidade (0 para cancelar este produto)");
                            if (qtd == 0)
                                continue;

                            if (qtd > escolhido.getQuantidade()) {
                                System.out
                                        .println("Quantidade solicitada maior que o estoque disponível! Estoque atual: "
                                                + escolhido.getQuantidade());
                                continue;
                            }

                            comprados.add(escolhido);
                            quantidades.add(qtd);
                            total = total.add(escolhido.getPreco().multiply(BigDecimal.valueOf(qtd)));

                            System.out.print("Digite 1 para adicionar mais produtos ou 0 para finalizar: ");
                            if (sc.nextLine().equals("0"))
                                break;
                        }

                        if (comprados.isEmpty()) {
                            System.out.println("Nenhum produto adicionado! Pedido cancelado.");
                            return;
                        }

                        String codigoPedido = gerarCodigoPedido();
                        System.out.println("Código do pedido: " + codigoPedido);

                        java.time.LocalDateTime agora = java.time.LocalDateTime.now().withSecond(0).withNano(0);
                        Timestamp dataCriacao = Timestamp.valueOf(agora);
                        Pedido pedido = new Pedido(0, cliente.getId(), total, "PENDENTE", dataCriacao, codigoPedido);

                        pedidoDAO.inserirPedido(pedido, comprados, quantidades);
                        for (int i = 0; i < comprados.size(); i++) {
                            Produto produto = comprados.get(i);
                            int quantidadeComprada = quantidades.get(i);
                            int novoEstoque = produto.getQuantidade() - quantidadeComprada;
                            produto.setQuantidade(novoEstoque);
                            produtoDAO.atualizarProduto(produto);
                        }
                    } catch (Exception e) {
                        System.out.println("Erro ao cadastrar pedido! Voltando ao menu.");
                    }
                    break;
                }

                case "2": {
                    listarPedidos(pedidoDAO);
                    break;
                }

                case "3": { // DELETAR
                    listarPedidos(pedidoDAO);
                    System.out.print("Código do pedido a deletar (0 para cancelar): ");
                    String codigoDel = sc.nextLine();
                    if (codigoDel.equals("0"))
                        return;
                    pedidoDAO.deletarPedido(codigoDel);
                    break;
                }

                case "4": { // VIEW COMPLETA
                    pedidoDAO.listarInformacoesCompletasPedidos();
                    break;
                }

                case "5": { // PROCEDURE STATUS
                    listarPedidos(pedidoDAO);
                    System.out.print("Código do pedido (0 para cancelar): ");
                    String codigo = sc.nextLine();
                    if (codigo.equals("0"))
                        return;

                    String novoStatus;
                    while (true) {
                        System.out.print("Novo status (concluido/pendente): ");
                        novoStatus = sc.nextLine().trim();

                        // Remove acentos e converte para minúsculo
                        String statusNormalizado = Normalizer.normalize(novoStatus, Normalizer.Form.NFD)
                                .replaceAll("[^\\p{ASCII}]", "")
                                .toUpperCase();

                        if (statusNormalizado.equals("CONCLUIDO") || statusNormalizado.equals("PENDENTE"))
                            break;
                        System.out.println("Status inválido! Use apenas 'concluido' ou 'pendente'.");
                    }
                    // Normaliza antes de enviar para o banco
                    String statusFinal = Normalizer.normalize(novoStatus, Normalizer.Form.NFD)
                            .replaceAll("[^\\p{ASCII}]", "")
                            .toUpperCase();
                    pedidoDAO.atualizarStatusPedido(codigo, statusFinal);
                    break;
                }

                default:
                    System.out.println("Opção inválida!");
            }
        } catch (Exception e) {
            System.out.println("Erro na operação! Voltando ao menu principal.");
        }
    }

    private static void listarPedidos(PedidoDAO dao) {
        System.out.println("\nPedidos cadastrados:");

        dao.listarPedidos().forEach(p -> System.out.println("Código: " + p.getCodigo() + " | Cliente ID:"
                + p.getClienteId() + " | Total: R$" + p.getValorTotal() + " | Status: " + p.getStatus()));
    }

    private static String gerarCodigoPedido() {
        int codigo = 1000 + (int) (Math.random() * 9000); // gera entre 1000 e 9999
        return String.valueOf(codigo);
    }

    // VALIDAÇÕES DOS CAMPOS
    private static String lerNome(Scanner sc, String campo) {
        while (true) {
            System.out.print(campo + ": ");
            String valor = sc.nextLine().trim();
            if (!valor.isEmpty() && valor.matches("[A-Za-zÀ-ÿ ]+"))
                return valor;
            System.out.println("Todos os campos são obrigatórios e devem ser preenchidos corretamente.");
        }
    }

    private static String lerEmail(Scanner sc, String campo) {
        while (true) {
            System.out.print(campo + ": ");
            String valor = sc.nextLine().trim();
            if (!valor.isEmpty() && valor.matches("^[\\w-.]+@[\\w-]+\\.[A-Za-z]{2,}$"))
                return valor;
            System.out.println("Todos os campos são obrigatórios e devem ser preenchidos corretamente.");
        }
    }

    private static String lerCpf(Scanner sc, String campo) {
        while (true) {
            System.out.print(campo + ": ");
            String valor = sc.nextLine().trim();
            if (valor.matches("\\d{11}"))
                return valor;
            System.out.println("Todos os campos são obrigatórios e devem ser preenchidos corretamente.");
        }
    }

    private static BigDecimal lerPreco(Scanner sc) {
        while (true) {
            try {
                System.out.print("Preço: ");
                return new BigDecimal(sc.nextLine());
            } catch (Exception e) {
                System.out.println("Todos os campos são obrigatórios e devem ser preenchidos corretamente.");
            }
        }
    }

    private static int lerInteiro(Scanner sc, String msg) {
        while (true) {
            try {
                System.out.print(msg + ": ");
                return Integer.parseInt(sc.nextLine());
            } catch (Exception e) {
                System.out.println("Todos os campos são obrigatórios e devem ser preenchidos corretamente.");
            }
        }
    }
}

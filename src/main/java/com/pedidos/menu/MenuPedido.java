package com.pedidos.menu;

import com.pedidos.dao.*;
import com.pedidos.model.*;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class MenuPedido {
    public static void exibirMenu(Scanner sc, PedidoDAO pedidoDAO, ProdutoDAO produtoDAO, ClienteDAO clienteDAO) {
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
                    cadastrarPedido(sc, pedidoDAO, produtoDAO, clienteDAO);
                    break;
                }
                case "2": {
                    listarPedidos(pedidoDAO);
                    break;
                }
                case "3": {
                    deletarPedido(sc, pedidoDAO);
                    break;
                }
                case "4": {
                    pedidoDAO.listarInformacoesCompletasPedidos();
                    break;
                }
                case "5": {
                    atualizarStatusPedido(sc, pedidoDAO);
                    break;
                }
                default:
                    System.out.println("Opção inválida!");
            }
        } catch (Exception e) {
            System.out.println("Erro na operação! Voltando ao menu principal.");
        }
    }

    private static void cadastrarPedido(Scanner sc, PedidoDAO pedidoDAO, ProdutoDAO produtoDAO, ClienteDAO clienteDAO) {
        try {
            listarClientes(clienteDAO);

            Cliente cliente = null;
            while (true) {
                System.out.print("CPF do cliente (0 para cancelar): ");
                String cpf = sc.nextLine();
                if (cpf.equals("0"))
                    return;

                cliente = clienteDAO.listarClientes().stream()
                        .filter(c -> c.getCpf().equals(cpf))
                        .findFirst().orElse(null);

                if (cliente == null) {
                    System.out.println("Todos os campos são obrigatórios e devem ser preenchidos corretamente.");
                } else {
                    break;
                }
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

                if (escolhido.getQuantidade() == 0) {
                    System.out.println("Produto indisponível! Estoque atual: 0");
                    continue; // pula para o próximo produto sem permitir adicionar
                }

                int qtd;
                while (true) {
                    qtd = lerInteiro(sc, "Quantidade (0 para cancelar este produto)");
                    if (qtd == 0) {
                        System.out.println("Produto não adicionado.");
                        break;
                    }
                    if (qtd > escolhido.getQuantidade()) {
                        System.out.println("Quantidade solicitada maior que o estoque disponível! Estoque atual: "
                                + escolhido.getQuantidade());
                    } else {
                        break; 
                    }
                }
                if (qtd == 0)
                    continue; 

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
                int estoqueAtual = produto.getQuantidade();
                if (quantidadeComprada > estoqueAtual) {
                    System.out.println("Erro: tentativa de subtrair mais produtos do que o disponível! Pedido cancelado.");
                    return; 
                }
                produto.setQuantidade(estoqueAtual - quantidadeComprada);
                produtoDAO.atualizarProduto(produto);
            }
        } catch (Exception e) {
            System.out.println("Erro ao cadastrar pedido! Voltando ao menu.");
        }
    }

    private static void listarPedidos(PedidoDAO dao) {
        System.out.println("\nPedidos cadastrados:");

        dao.listarPedidos().forEach(p -> System.out.println("Código: " + p.getCodigo() + " | Cliente ID:"
                + p.getClienteId() + " | Total: R$" + p.getValorTotal() + " | Status: " + p.getStatus()));
    }

    private static void deletarPedido(Scanner sc, PedidoDAO pedidoDAO) {
        listarPedidos(pedidoDAO);

        while (true) {
            System.out.print("Código do pedido a deletar (0 para cancelar): ");
            String codigoDel = sc.nextLine();
            if (codigoDel.equals("0"))
                return;

            boolean existe = pedidoDAO.listarPedidos().stream()
                    .anyMatch(p -> p.getCodigo().equals(codigoDel));

            if (existe) {
                pedidoDAO.deletarPedido(codigoDel);
                break;
            } else {
                System.out.println("Pedido não encontrado! Todos os campos devem ser preenchidos corretamente.");
            }
        }
    }

    private static void atualizarStatusPedido(Scanner sc, PedidoDAO pedidoDAO) {
        listarPedidos(pedidoDAO);

        String codigoPedido;
        while (true) {
            System.out.print("Código do pedido (0 para cancelar): ");
            codigoPedido = sc.nextLine();

            if (codigoPedido.equals("0")) {
                System.out.println("Operação cancelada.");
                return;
            }

            final String codigoCheck = codigoPedido; // variável efetivamente final
            boolean existe = pedidoDAO.listarPedidos().stream()
                    .anyMatch(p -> p.getCodigo().equals(codigoCheck));

            if (existe) {
                break;
            } else {
                System.out.println("Pedido não encontrado! Todos os campos devem ser preenchidos corretamente.");
            }
        }

        String novoStatus;
        while (true) {
            System.out.print("Novo status (concluido/pendente): ");
            novoStatus = sc.nextLine().trim();

            String statusNormalizado = Normalizer.normalize(novoStatus, Normalizer.Form.NFD)
                    .replaceAll("[^\\p{ASCII}]", "")
                    .toUpperCase();

            if (statusNormalizado.equals("CONCLUIDO") || statusNormalizado.equals("PENDENTE")) {
                String statusFinal = statusNormalizado;
                pedidoDAO.atualizarStatusPedido(codigoPedido, statusFinal);
                System.out.println("Status atualizado com sucesso.");
                break;
            } else {
                System.out.println("Status inválido! Use apenas 'concluido' ou 'pendente'.");
            }
        }
    }

    // Métodos auxiliares para listar clientes e produtos, reutilizados do menu
    private static void listarClientes(ClienteDAO dao) {
        System.out.println("\nClientes cadastrados:");
        dao.listarClientes().forEach(c -> System.out.println(c.getNome() + " | CPF: " + c.getCpf()));
    }

    private static void listarProdutos(ProdutoDAO dao) {
        System.out.println("\nProdutos cadastrados:");
        dao.listarProdutos().forEach(p -> System.out.println(
                p.getId() + " - " + p.getNome() + " | R$" + p.getPreco() + " | Estoque: " + p.getQuantidade()));
    }

    private static String gerarCodigoPedido() {
        int codigo = 1000 + (int) (Math.random() * 9000); // gera entre 1000 e 9999
        return String.valueOf(codigo);
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

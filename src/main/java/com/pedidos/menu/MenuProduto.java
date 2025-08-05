package com.pedidos.menu;

import com.pedidos.dao.ProdutoDAO;
import com.pedidos.model.Produto;

import java.math.BigDecimal;
import java.util.Scanner;

public class MenuProduto {
    public static void exibirMenu(Scanner sc, ProdutoDAO dao) {
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

                    Produto produtoExistente = null;
                    while (produtoExistente == null) {
                        int idProd = lerInteiro(sc, "ID do produto (ou 0 para cancelar)");
                        if (idProd == 0)
                            return;

                        produtoExistente = dao.listarProdutos().stream()
                                .filter(p -> p.getId() == idProd)
                                .findFirst().orElse(null);

                        if (produtoExistente == null) {
                            System.out.println(
                                    "Produto não encontrado! Todos os campos devem ser preenchidos corretamente.");
                        }
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

                    while (true) {
                        int idProdDel = lerInteiro(sc, "ID do produto a deletar (ou 0 para cancelar)");
                        if (idProdDel == 0)
                            return;

                        Produto existe = dao.listarProdutos().stream()
                                .filter(p -> p.getId() == idProdDel)
                                .findFirst().orElse(null);

                        if (existe != null) {
                            dao.deletarProduto(idProdDel);
                            break;
                        } else {
                            System.out.println(
                                    "Produto não encontrado! Todos os campos devem ser preenchidos corretamente.");
                        }
                    }
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

    // Validações reutilizáveis
    private static String lerNome(Scanner sc, String campo) {
        while (true) {
            System.out.print(campo + ": ");
            String valor = sc.nextLine().trim();
            if (!valor.isEmpty() && valor.matches("[A-Za-zÀ-ÿ ]+"))
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

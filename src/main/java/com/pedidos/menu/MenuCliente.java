
package com.pedidos.menu;

import com.pedidos.dao.ClienteDAO;
import com.pedidos.model.Cliente;

import java.util.Scanner;

public class MenuCliente {

    public static void exibirMenu(Scanner sc, ClienteDAO dao) {
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

                    // Verifica se CPF já existe
                    boolean cpfJaExiste = dao.listarClientes().stream().anyMatch(c -> c.getCpf().equals(cpf));
                    if (cpfJaExiste) {
                        System.out.println("CPF já cadastrado! Operação cancelada.");
                        return;
                    }

                    // Verifica se email já existe
                    boolean emailJaExiste = dao.listarClientes().stream().anyMatch(c -> c.getEmail().equalsIgnoreCase(email));
                    if (emailJaExiste) {
                        System.out.println("Email já cadastrado! Operação cancelada.");
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
                        if (novoEmail.equals("0")) {
                            System.out.println("Operação cancelada.");
                            return;
                        } else if (!novoEmail.isBlank() && novoEmail.matches("^[\\w-.]+@[\\w-]+\\.[A-Za-z]{2,}$")) {
                            break;
                        } else {
                            System.out.println("E-mail inválido! Digite novamente ou 0 para cancelar.");
                        }
                    }
                    dao.atualizarCliente(new Cliente(0, clienteExistente.getNome(), novoEmail, cpfUpdate));
                    System.out.println("Cliente atualizado com sucesso.");
                    break;

                case "4":
                    listarClientes(dao);

                    while (true) {
                        String cpfDelete = lerCpf(sc, "CPF do cliente a deletar (ou 0 para cancelar)");
                        if (cpfDelete.equals("0")) {
                            System.out.println("Operação cancelada.");
                            return;
                        }

                        Cliente existe = dao.listarClientes().stream()
                                .filter(c -> c.getCpf().equals(cpfDelete))
                                .findFirst().orElse(null);

                        if (existe != null) {
                            dao.deletarCliente(cpfDelete);
                            System.out.println("Cliente removido com sucesso.");
                            break;
                        } else {
                            System.out.println(
                                    "Cliente não encontrado! Todos os campos devem ser preenchidos corretamente.");
                        }
                    }

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

    // Validações dos campos
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

    // Aqui está a modificação que permite "0" para cancelar sem mensagem de erro
    private static String lerCpf(Scanner sc, String campo) {
        while (true) {
            System.out.print(campo + ": ");
            String valor = sc.nextLine().trim();
            if (valor.equals("0")) {
                return valor;  // Aceita 0 para cancelar
            }
            if (valor.matches("\\d{11}")) {
                return valor;  // Aceita CPF válido
            }
            System.out.println("Todos os campos são obrigatórios e devem ser preenchidos corretamente.");
        }
    }
}

// package com.pedidos.menu;

// import com.pedidos.dao.ClienteDAO;
// import com.pedidos.model.Cliente;

// import java.util.Scanner;

// public class MenuCliente {

//     public static void exibirMenu(Scanner sc, ClienteDAO dao) {
//         System.out.println("\n  GERÊNCIA DE CLIENTES");
//         System.out.println("1 - Cadastrar");
//         System.out.println("2 - Listar");
//         System.out.println("3 - Atualizar (apenas e-mail)");
//         System.out.println("4 - Deletar");
//         System.out.println("5 - Consultar Total Gasto");
//         System.out.println("6 - Consultar Total de Pedidos");
//         System.out.print("Escolha: ");
//         String opcao = sc.nextLine();

//         try {
//             switch (opcao) {
//                 case "1":
//                     String nome = lerNome(sc, "Nome");
//                     String email = lerEmail(sc, "Email");
//                     String cpf = lerCpf(sc, "CPF");

//                     // Verifica se CPF já existe
//                     boolean cpfJaExiste = dao.listarClientes().stream().anyMatch(c -> c.getCpf().equals(cpf));
//                     if (cpfJaExiste) {
//                         System.out.println("CPF já cadastrado! Operação cancelada.");
//                         return;
//                     }

//                     // Verifica se email já existe
//                     boolean emailJaExiste = dao.listarClientes().stream().anyMatch(c -> c.getEmail().equalsIgnoreCase(email));
//                     if (emailJaExiste) {
//                         System.out.println("Email já cadastrado! Operação cancelada.");
//                         return;
//                     }

//                     dao.inserirCliente(new Cliente(0, nome, email, cpf));
//                     break;

//                 case "2":
//                     System.out.println("\nClientes cadastrados:");
//                     dao.listarClientes().forEach(c -> System.out
//                             .println(c.getId() + " - " + c.getNome() + " | " + c.getEmail() + " | CPF:" + c.getCpf()));
//                     break;

//                     // Modifiquei a mensagem de cancelamento

//                 case "3":
//                     listarClientes(dao);
//                     System.out.print("\nCPF do cliente: ");
//                     String cpfUpdate = sc.nextLine();
//                     Cliente clienteExistente = dao.listarClientes().stream()
//                             .filter(c -> c.getCpf().equals(cpfUpdate))
//                             .findFirst().orElse(null);
//                     if (clienteExistente == null) {
//                         System.out.println("Cliente não encontrado!");
//                         return;
//                     }
//                     String novoEmail;
//                     while (true) {
//                         System.out.print("Digite o novo email (ou 0 para cancelar): ");
//                         novoEmail = sc.nextLine();
//                         if (novoEmail.equals("0")) {
//                             System.out.println("Operação cancelada.");
//                             return;
//                         } else if (!novoEmail.isBlank() && novoEmail.matches("^[\\w-.]+@[\\w-]+\\.[A-Za-z]{2,}$")) {
//                             break;
//                         } else {
//                             System.out.println("E-mail inválido! Digite novamente ou 0 para cancelar.");
//                         }
//                     }
//                     dao.atualizarCliente(new Cliente(0, clienteExistente.getNome(), novoEmail, cpfUpdate));
//                     System.out.println("Cliente atualizado com sucesso.");

//                     // String novoEmail;
//                     // while (true) {
//                     //     System.out.print("Digite o novo email (ou 0 para cancelar): ");
//                     //     novoEmail = sc.nextLine();
//                     //     if (novoEmail.equals("0") || novoEmail.isBlank()) {
//                     //         novoEmail = clienteExistente.getEmail();
//                     //         break;
//                     //     } else if (novoEmail.matches("^[\\w-.]+@[\\w-]+\\.[A-Za-z]{2,}$")) {
//                     //         break;
//                     //     } else {
//                     //         System.out.println("E-mail inválido! Digite novamente ou 0 para manter o anterior.");
//                     //     }
//                     // }
//                     // dao.atualizarCliente(new Cliente(0, clienteExistente.getNome(), novoEmail, cpfUpdate));
//                     // break;


//                 case "4":
//                     listarClientes(dao); // Modifiquei a mensagem de cancelamento 

//                     while (true) {
//                         String cpfDelete = lerCpf(sc, "CPF do cliente a deletar (ou 0 para cancelar)");
//                         if (cpfDelete.equals("0")) {
//     System.out.println("Operação cancelada.");
//     return;
// }

//                         // if (cpfDelete.equals("0"))
//                         //     return;

//                         Cliente existe = dao.listarClientes().stream()
//                                 .filter(c -> c.getCpf().equals(cpfDelete))
//                                 .findFirst().orElse(null);

//                         if (existe != null) {
//                             dao.deletarCliente(cpfDelete);
//                             break;
//                         } else {
//                             System.out.println(
//                                     "Cliente não encontrado! Todos os campos devem ser preenchidos corretamente.");
//                         }
//                     }

//                     break;

//                 case "5":
//                     listarClientes(dao);
//                     System.out.print("CPF do cliente: ");
//                     dao.consultarTotalGastoPorCliente(sc.nextLine());
//                     break;

//                 case "6":
//                     listarClientes(dao);
//                     System.out.print("CPF do cliente: ");
//                     int total = dao.consultarTotalPedidosCliente(sc.nextLine());
//                     System.out.println("Total de pedidos: " + total);
//                     break;

//                 default:
//                     System.out.println("Opção inválida!");
//             }
//         } catch (Exception e) {
//             System.out.println("Erro na operação! Voltando ao menu principal.");
//         }
//     }

//     private static void listarClientes(ClienteDAO dao) {
//         System.out.println("\nClientes cadastrados:");
//         dao.listarClientes().forEach(c -> System.out.println(c.getNome() + " | CPF: " + c.getCpf()));
//     }

//     // Validações dos campos
//     private static String lerNome(Scanner sc, String campo) {
//         while (true) {
//             System.out.print(campo + ": ");
//             String valor = sc.nextLine().trim();
//             if (!valor.isEmpty() && valor.matches("[A-Za-zÀ-ÿ ]+"))
//                 return valor;
//             System.out.println("Todos os campos são obrigatórios e devem ser preenchidos corretamente.");
//         }
//     }

//     private static String lerEmail(Scanner sc, String campo) {
//         while (true) {
//             System.out.print(campo + ": ");
//             String valor = sc.nextLine().trim();
//             if (!valor.isEmpty() && valor.matches("^[\\w-.]+@[\\w-]+\\.[A-Za-z]{2,}$"))
//                 return valor;
//             System.out.println("Todos os campos são obrigatórios e devem ser preenchidos corretamente.");
//         }
//     }

//     private static String lerCpf(Scanner sc, String campo) {
//         while (true) {
//             System.out.print(campo + ": ");
//             String valor = sc.nextLine().trim();
//             if (valor.matches("\\d{11}"))
//                 return valor;
//             System.out.println("Todos os campos são obrigatórios e devem ser preenchidos corretamente.");
//         }
//     }
// }

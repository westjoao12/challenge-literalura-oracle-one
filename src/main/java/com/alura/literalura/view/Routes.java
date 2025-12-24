package com.alura.literalura.view;

import com.alura.literalura.model.*;
import com.alura.literalura.repository.AutorRepository;
import com.alura.literalura.repository.LivroRepository;
import com.alura.literalura.service.ConsumoApi;
import com.alura.literalura.service.ConverteDados;
import org.springframework.stereotype.Component;

import java.util.Scanner;
import java.util.List;

@Component
public class Routes {
    private Scanner leitura = new Scanner(System.in);
    private ConsumoApi consumo = new ConsumoApi();
    private ConverteDados conversor = new ConverteDados();
    private final String ENDERECO = "https://gutendex.com/books/?search=";

    private final LivroRepository livroRepository;
    private final AutorRepository autorRepository;

    public Routes(LivroRepository livroRepository, AutorRepository autorRepository) {
        this.livroRepository = livroRepository;
        this.autorRepository = autorRepository;
    }

    public void exibeMenu() {
        var opcao = -1;
        while (opcao != 0) {
            var menu = """
                    1 - Buscar livro pelo título
                    2 - Listar livros registrados
                    3 - Listar autores registrados
                    4 - Listar autores vivos em um determinado ano
                    5 - Listar livros em um determinado idioma
                    
                    0 - Sair
                    """;

            System.out.println(menu);

            try {
                opcao = leitura.nextInt();
                leitura.nextLine();
            } catch (java.util.InputMismatchException e) {
                System.out.println("Opção inválida! Por favor, digite um número inteiro.");
                leitura.nextLine();
                opcao = -1;
                continue;
            }

            switch (opcao) {
                case 1 -> buscarLivroWeb();
                case 2 -> listarLivrosRegistrados();
                case 3 -> listarAutoresRegistrados();
                case 4 -> listarAutoresVivos();
                case 5 -> listarLivrosPorIdioma();
                case 0 -> System.out.println("Saindo...");
                default -> System.out.println("Opção inválida");
            }
        }
    }

    private void buscarLivroWeb() {
        System.out.println("Digite o nome do livro para busca:");
        var nomeLivro = leitura.nextLine();
        var json = consumo.obterDados(ENDERECO + nomeLivro.replace(" ", "%20"));
        var dados = conversor.obterDados(json, DadosGutendex.class);

        if (dados.resultados() != null && !dados.resultados().isEmpty()) {

            // Pega o primeiro resultado
            DadosLivro dadosLivro = dados.resultados().get(0);

            // Verifica se o autor já existe no banco
            DadosAutor dadosAutor = dadosLivro.autores().get(0);
            Autor autor = autorRepository.findByNome(dadosAutor.nome())
                    .orElseGet(() -> autorRepository.save(new Autor(dadosAutor)));

            // Salva o livro
            Livro livro = new Livro(dadosLivro, autor);

            // Verifica duplicidade de livro antes de salvar
            try {
                livroRepository.save(livro);
                System.out.println("Livro salvo: " + livro);
            } catch (Exception e) {
                System.out.println("Erro ao salvar (possível duplicidade): " + e.getMessage());
            }

        } else {
            System.out.println("Livro não encontrado.");
        }
    }

    private void listarLivrosRegistrados() {
        List<Livro> livros = livroRepository.findAll();
        livros.forEach(System.out::println);
    }

    private void listarAutoresRegistrados() {
        List<Autor> autores = autorRepository.findAll();
        autores.forEach(System.out::println);
    }

    private void listarAutoresVivos() {
        System.out.println("Insira o ano que deseja pesquisar:");
        var ano = leitura.nextInt();
        leitura.nextLine();
        List<Autor> autores = autorRepository.autoresVivosNoAno(ano);
        autores.forEach(System.out::println);
    }

    private void listarLivrosPorIdioma() {
        System.out.println("Insira o idioma para busca (es, en, fr, pt):");
        var idioma = leitura.nextLine();
        List<Livro> livros = livroRepository.findByIdioma(idioma);
        livros.forEach(System.out::println);
    }
}

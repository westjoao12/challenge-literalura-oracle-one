package com.alura.literalura.model;
import jakarta.persistence.*;

@Entity
@Table(name = "livros")
public class Livro {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String titulo;

    private String idioma;
    private Double numeroDownloads;

    @ManyToOne
    private Autor autor;

    public Livro() {}

    public Livro(DadosLivro dadosLivro, Autor autor) {
        this.titulo = dadosLivro.titulo();
        this.idioma = !dadosLivro.idiomas().isEmpty() ? dadosLivro.idiomas().get(0) : null;
        this.numeroDownloads = dadosLivro.numeroDownloads();
        this.autor = autor;
    }

    // Getters, Setters e toString
    public Long getId() { return id; }
    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
    public String getIdioma() { return idioma; }
    public void setIdioma(String idioma) { this.idioma = idioma; }
    public Double getNumeroDownloads() { return numeroDownloads; }
    public void setNumeroDownloads(Double numeroDownloads) { this.numeroDownloads = numeroDownloads; }
    public Autor getAutor() { return autor; }
    public void setAutor(Autor autor) { this.autor = autor; }

    @Override
    public String toString() {
        return "Livro: " + titulo + " | Autor: " + (autor != null ? autor.getNome() : "N/A") + " | Idioma: " + idioma;
    }
}

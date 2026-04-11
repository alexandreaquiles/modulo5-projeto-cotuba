package br.com.unipds;

import java.nio.file.Path;

public class Capitulo {

    private String titulo;

    private Path arquivoMardown;
    private String markdown;

    private String html;

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public Path getArquivoMardown() {
        return arquivoMardown;
    }

    public void setArquivoMardown(Path arquivoMardown) {
        this.arquivoMardown = arquivoMardown;
    }

    public String getMarkdown() {
        return markdown;
    }

    public void setMarkdown(String markdown) {
        this.markdown = markdown;
    }

    public String getHtml() {
        return html;
    }

    public void setHtml(String html) {
        this.html = html;
    }
}

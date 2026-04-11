package br.com.unipds;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

public class LeitorPropriedadesEbook {

    public void ler(Path diretorioMD, Ebook ebook) {

        Path arquivoProperties = diretorioMD.resolve("ebook.properties");

        if (!Files.exists(arquivoProperties)) {
            throw new IllegalStateException("Arquivo ebook.properties não encontrado em: " + diretorioMD);
        }

        Properties properties = new Properties();
        try (InputStream in = Files.newInputStream(arquivoProperties)) {
            properties.load(in);
        } catch (IOException ex) {
            throw new IllegalStateException("Erro ao ler arquivo: " + arquivoProperties, ex);
        }

        String propriedadeTitulo = "cotuba.ebook.titulo";
        String titulo = properties.getProperty(propriedadeTitulo);
        validarPropriedade(titulo, propriedadeTitulo);

        String propriedadeAutor = "cotuba.ebook.autor";
        String autor = properties.getProperty(propriedadeAutor);
        validarPropriedade(autor, propriedadeAutor);

        ebook.setTitulo(titulo);
        ebook.setAutor(autor);
    }

    private static void validarPropriedade(String valor, String propriedade) {
        if (valor == null || valor.isBlank()) {
            throw new IllegalArgumentException("Propriedade inválida: " + propriedade);
        }
    }

}

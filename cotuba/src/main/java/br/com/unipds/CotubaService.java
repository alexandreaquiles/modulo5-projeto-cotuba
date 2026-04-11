package br.com.unipds;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.nio.file.Path;
import java.util.List;

@ApplicationScoped
public class CotubaService {

    private final RenderizadorMarkdown renderizadorMarkdown;
    private final LeitorPropriedadesEbook leitorPropriedadesEbook;
    private final RepositorioMarkdowns repositorioMarkdowns;
    private final GeradorEbook geradorPDF;
    private final GeradorEbook geradorEPUB;


    @Inject
    public CotubaService(RenderizadorMarkdown renderizadorMarkdown, LeitorPropriedadesEbook leitorPropriedadesEbook, RepositorioMarkdowns repositorioMarkdowns, @Named("geradorPDF") GeradorEbook geradorPDF,  @Named("geradorEPUB")  GeradorEbook geradorEPUB) {
        this.renderizadorMarkdown = renderizadorMarkdown;
        this.leitorPropriedadesEbook = leitorPropriedadesEbook;
        this.repositorioMarkdowns = repositorioMarkdowns;
        this.geradorPDF = geradorPDF;
        this.geradorEPUB = geradorEPUB;
    }

    public void executar(ParametrosCotuba parametrosCotuba) {

        Path diretorioDosMD = parametrosCotuba.getDiretorioDosMD();

        List<Capitulo> capitulos = repositorioMarkdowns.buscar(diretorioDosMD);

        renderizadorMarkdown.renderizar(capitulos);

        var ebook = new Ebook();

        leitorPropriedadesEbook.ler(diretorioDosMD, ebook);

        ebook.setCapitulos(capitulos);
        ebook.setFormato(parametrosCotuba.getFormato());
        ebook.setArquivoSaida(parametrosCotuba.getArquivoDeSaida());

        GeradorEbook geradorEbook;
        if (FormatoEbook.PDF.equals(ebook.getFormato())) {
            geradorEbook = geradorPDF;
        } else if (FormatoEbook.EPUB.equals(ebook.getFormato())) {
            geradorEbook = geradorEPUB;
        } else {
            throw new IllegalArgumentException("Formato do ebook inválido: " + parametrosCotuba.getFormato());
        }

        geradorEbook.gerar(ebook);


    }

}

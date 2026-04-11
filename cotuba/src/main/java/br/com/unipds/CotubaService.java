package br.com.unipds;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Any;
import jakarta.enterprise.inject.Instance;
import jakarta.inject.Inject;

import java.nio.file.Path;
import java.util.List;

@ApplicationScoped
public class CotubaService {

    private final RenderizadorMarkdown renderizadorMarkdown;
    private final LeitorPropriedadesEbook leitorPropriedadesEbook;
    private final RepositorioMarkdowns repositorioMarkdowns;
    private final Instance<GeradorEbook> geradoresEbook;


    @Inject
    public CotubaService(RenderizadorMarkdown renderizadorMarkdown, LeitorPropriedadesEbook leitorPropriedadesEbook, RepositorioMarkdowns repositorioMarkdowns, @Any Instance<GeradorEbook> geradoresEbook) {
        this.renderizadorMarkdown = renderizadorMarkdown;
        this.leitorPropriedadesEbook = leitorPropriedadesEbook;
        this.repositorioMarkdowns = repositorioMarkdowns;
        this.geradoresEbook = geradoresEbook;
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

        FormatoEbook formato = ebook.getFormato();
        GeradorEbook geradorEbook = geradoresEbook.select(FormatoEbookFilter.of(formato)).get();

        geradorEbook.gerar(ebook);

    }

}

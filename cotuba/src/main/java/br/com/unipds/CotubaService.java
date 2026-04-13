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

        Path diretorioDosMD = parametrosCotuba.diretorioDosMD();

        List<Markdown> markdowns = repositorioMarkdowns.buscar(diretorioDosMD);

        List<Capitulo> capitulos = renderizadorMarkdown.renderizar(markdowns);

        var propriedadesEbook = leitorPropriedadesEbook.ler(diretorioDosMD);

        var ebook = EbookBuilder.builder()
                .capitulos(capitulos)
                .formato(parametrosCotuba.formato())
                .arquivoSaida(parametrosCotuba.arquivoDeSaida())
                .titulo(propriedadesEbook.titulo())
                .autor(propriedadesEbook.autor())
                .build();

        GeradorEbook geradorEbook = geradoresEbook.select(FormatoEbookFilter.of(ebook.formato())).get();

        geradorEbook.gerar(ebook);

    }

}

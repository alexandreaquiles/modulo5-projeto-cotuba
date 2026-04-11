package br.com.unipds;

import java.util.List;

public class CotubaService {

    public void executar(ParametrosCotuba parametrosCotuba) {

        var renderizadorMarkdown = new RenderizadorMarkdown();
        List<Capitulo> capitulos = renderizadorMarkdown.renderizar(parametrosCotuba.getDiretorioDosMD());

        var ebook = new Ebook();

        var leitorPropriedadesEbook = new LeitorPropriedadesEbook();
        leitorPropriedadesEbook.ler(parametrosCotuba.getDiretorioDosMD(), ebook);

        ebook.setCapitulos(capitulos);
        ebook.setFormato(parametrosCotuba.getFormato());
        ebook.setArquivoSaida(parametrosCotuba.getArquivoDeSaida());

        if (FormatoEbook.PDF.equals(ebook.getFormato())) {

            var geradorPDF = new GeradorPDF();
            geradorPDF.gerarPDF(ebook);

        } else if (FormatoEbook.EPUB.equals(ebook.getFormato())) {

            var geradorEPUB = new GeradorEPUB();
            geradorEPUB.gerarEPUB(ebook);

        } else {
            throw new IllegalArgumentException("Formato do ebook inválido: " + parametrosCotuba.getFormato());
        }

    }

}

package br.com.unipds.estatisticas;

import br.com.unipds.cotuba.domain.Capitulo;
import br.com.unipds.cotuba.domain.Ebook;
import br.com.unipds.cotuba.plugin.CotubaPlugin;
import br.com.unipds.estatisticas.ContadorPalavras.ContagemPalavra;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class PluginEstatisticas implements CotubaPlugin  {

    @Override
    public String aposRenderizacao(String s) {
        return "";
    }

    @Override
    public void aposGeracao(Ebook ebook) {

        var contadorPalavras = new ContadorPalavras();

        for (Capitulo capitulo : ebook.capitulos()) {
            String html = capitulo.html();
            Document doc = Jsoup.parseBodyFragment(html);
            String textoCapitulo = doc.text().toLowerCase();
            textoCapitulo = textoCapitulo.replaceAll("\\p{Punct}", "");
            String[] palavras = textoCapitulo.split("\\s+");
            for (String palavra : palavras) {
                contadorPalavras.adicionarPalavra(palavra);
            }
        }

        for (ContagemPalavra contagem : contadorPalavras) {
            System.out.printf("'%s': %d\n", contagem.palavra(), contagem.ocorrencias());
        }

    }
}

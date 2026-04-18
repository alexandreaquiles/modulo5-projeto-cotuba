package br.com.unipds.estatisticas;

import br.com.unipds.cotuba.domain.Capitulo;
import br.com.unipds.cotuba.domain.Ebook;
import br.com.unipds.cotuba.plugin.CotubaPlugin;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.util.Map;

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

        for (Map.Entry<String, Integer> contagem : contadorPalavras.entrySet()) {
            String palavra = contagem.getKey();
            Integer ocorrencias = contagem.getValue();
            System.out.printf("'%s': %d\n", palavra, ocorrencias);
        }


    }
}

package br.com.unipds.tema;

import br.com.unipds.cotuba.plugin.CotubaPluginAposRenderizacao;

public class PluginTemaCSS implements CotubaPluginAposRenderizacao {

    @Override
    public String aposRenderizacao(String html) {

        return """
                <style>
                    h1 { /* título do capítulo */
                      border-bottom: 1px dashed black;
                      font-size: 3em;
                      font-weight: bolder;
                      font-variant-caps: small-caps;
                  }
                  h2 { /* título das seções */
                      border-left: 1px solid black;
                      padding-left: 5px;
                      border-bottom: 1px solid black;
                  }
                </style>
                %s
                """.formatted(html);
    }

}

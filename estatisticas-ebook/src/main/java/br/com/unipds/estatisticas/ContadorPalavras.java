package br.com.unipds.estatisticas;

import java.util.TreeMap;

class ContadorPalavras extends TreeMap<String, Integer> {

    public void adicionarPalavra(String palavra) {
        super.merge(palavra, 1, Integer::sum);
    }

}

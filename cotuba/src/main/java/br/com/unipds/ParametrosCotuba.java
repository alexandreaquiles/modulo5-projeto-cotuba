package br.com.unipds;

import java.nio.file.Path;

public record ParametrosCotuba (Path diretorioDosMD, FormatoEbook formato, Path arquivoDeSaida, boolean modoVerboso) {

    ParametrosCotuba (Path diretorioDosMD, FormatoEbook formato, Path arquivoDeSaida) {
        this(diretorioDosMD, formato, arquivoDeSaida, false);
    }



}

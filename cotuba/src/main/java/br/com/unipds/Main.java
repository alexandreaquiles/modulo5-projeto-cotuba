package br.com.unipds;

import java.nio.file.Path;
import java.util.List;

public class Main {

    void main(String[] args) {
        int exitCode = executar(args);
        if (exitCode != 0) {
            System.exit(exitCode);
        }
    }

    int executar(String[] args) {

        boolean modoVerboso = true;

        try {
            var leitorOpcoesCLI = new LeitorOpcoesCLI();
            ParametrosCotuba parametrosCotuba = leitorOpcoesCLI.ler(args);

            modoVerboso = parametrosCotuba.isModoVerboso();

            var cotubaService = new CotubaService();
            cotubaService.executar(parametrosCotuba);

            System.out.println("Arquivo gerado com sucesso: " + parametrosCotuba.getArquivoDeSaida());
            return 0;

        } catch (Exception ex) {
            System.err.println(ex.getMessage());
            if (modoVerboso) {
                System.err.println();
                ex.printStackTrace();
            }
            return 1;
        }
    }

}
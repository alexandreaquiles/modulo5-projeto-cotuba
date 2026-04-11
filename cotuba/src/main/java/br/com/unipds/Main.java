package br.com.unipds;

import jakarta.enterprise.inject.se.SeContainer;
import jakarta.enterprise.inject.se.SeContainerInitializer;

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

        try (SeContainer container = SeContainerInitializer.newInstance().initialize()) {
            var leitorOpcoesCLI = container.select(LeitorOpcoesCLI.class).get();
            ParametrosCotuba parametrosCotuba = leitorOpcoesCLI.ler(args);

            modoVerboso = parametrosCotuba.isModoVerboso();

            var cotubaService = container.select(CotubaService.class).get();
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
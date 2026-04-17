package br.com.unipds.cotuba.adapters.in.cli;

import br.com.unipds.cotuba.dto.ParametrosCotuba;
import br.com.unipds.cotuba.ports.in.CotubaUseCase;
import jakarta.enterprise.inject.se.SeContainer;
import jakarta.enterprise.inject.se.SeContainerInitializer;

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

            modoVerboso = parametrosCotuba.modoVerboso();

            var cotubaUseCase = container.select(CotubaUseCase.class).get();
            cotubaUseCase.executar(parametrosCotuba);

            System.out.println("Arquivo gerado com sucesso: " + parametrosCotuba.arquivoDeSaida());
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
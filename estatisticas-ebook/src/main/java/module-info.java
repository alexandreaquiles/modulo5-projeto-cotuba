module estatisticas.ebook {
    requires cotuba.domain;
    requires cotuba.plugin;

    requires org.jsoup;

    provides br.com.unipds.cotuba.plugin.CotubaPluginAposGeracao
            with br.com.unipds.estatisticas.PluginEstatisticas;
}
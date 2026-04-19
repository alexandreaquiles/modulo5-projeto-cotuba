module cotuba.plugin {
    requires cotuba.domain;

    exports br.com.unipds.cotuba.plugin;

    uses br.com.unipds.cotuba.plugin.CotubaPluginAposRenderizacao;
    uses br.com.unipds.cotuba.plugin.CotubaPluginAposGeracao;
}
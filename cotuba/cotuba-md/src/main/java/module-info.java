module cotuba.md {
    requires cotuba.application;
    requires cotuba.domain;
    requires cotuba.plugin;

    requires org.commonmark;

    requires weld.se.shaded;

    uses br.com.unipds.cotuba.plugin.CotubaPluginAposRenderizacao;
}
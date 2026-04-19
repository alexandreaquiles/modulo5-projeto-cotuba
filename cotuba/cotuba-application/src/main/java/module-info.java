open module cotuba.application {
    requires cotuba.domain;
    requires cotuba.plugin;

    requires org.jmolecules.architecture.hexagonal;
    requires org.jmolecules.ddd;

    requires weld.se.shaded;

    exports br.com.unipds.cotuba.ports.in;
    exports br.com.unipds.cotuba.ports.out;
    exports br.com.unipds.cotuba.dto;
    exports br.com.unipds.cotuba.support;

    uses br.com.unipds.cotuba.plugin.CotubaPluginAposGeracao;
}
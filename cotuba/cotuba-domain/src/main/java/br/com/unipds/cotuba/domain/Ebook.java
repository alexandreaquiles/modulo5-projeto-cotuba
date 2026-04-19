package br.com.unipds.cotuba.domain;

import io.soabase.recordbuilder.core.RecordBuilder;
import org.jmolecules.ddd.annotation.AggregateRoot;
import org.jmolecules.ddd.annotation.Identity;

import java.util.List;

@AggregateRoot
@RecordBuilder
public record Ebook(
     @Identity String titulo,
     String autor,
     FormatoEbook formato,
     List<Capitulo> capitulos)  {

}

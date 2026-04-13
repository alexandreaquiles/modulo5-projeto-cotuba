package br.com.unipds;

import io.soabase.recordbuilder.core.RecordBuilder;

import java.nio.file.Path;
import java.util.List;

@RecordBuilder
public record Ebook(
     String titulo,
     String autor,
     FormatoEbook formato,
     List<Capitulo> capitulos,
     Path arquivoSaida)  {

}

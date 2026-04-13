package br.com.unipds;

import jakarta.enterprise.context.ApplicationScoped;
import nl.siegmann.epublib.domain.Author;
import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.domain.GuideReference;
import nl.siegmann.epublib.domain.Resource;
import nl.siegmann.epublib.epub.EpubWriter;
import nl.siegmann.epublib.service.MediatypeService;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@FormatoEbookQualifier(FormatoEbook.EPUB)
@ApplicationScoped
public class GeradorEPUB implements GeradorEbook {

    @Override
    public void gerar(Ebook ebook) {
        List<Capitulo> capitulos = ebook.capitulos();
        Path arquivoSaida = ebook.arquivoSaida();

        try {
            var epub = new Book();

            epub.getMetadata().addTitle(ebook.titulo());
            epub.getMetadata().addAuthor(new Author(ebook.autor()));

            boolean[] ehPrimeiroCapitulo = {true};

            capitulos.forEach(capitulo -> {
                String html = capitulo.html();
                String tituloCapitulo = capitulo.titulo();

                try {
                    StringWriter sw = new StringWriter();
                    XMLStreamWriter writer = XMLOutputFactory.newInstance()
                            .createXMLStreamWriter(sw);

                    writer.writeStartElement("html");
                    writer.writeDefaultNamespace("http://www.w3.org/1999/xhtml");

                    writer.writeStartElement("head");
                    writer.writeStartElement("title");
                    writer.writeCharacters(ebook.titulo());
                    writer.writeEndElement();
                    writer.writeEndElement();

                    writer.writeStartElement("body");
                    writer.writeCharacters("");
                    writer.flush();
                    sw.write(html);

                    writer.writeEndElement(); // body
                    writer.writeEndElement(); // html

                    writer.close();

                    var chapter = new Resource(sw.toString().getBytes(), MediatypeService.XHTML);
                    epub.addSection(tituloCapitulo, chapter);

                    if (ehPrimeiroCapitulo[0]) {
                        epub.getGuide().addReference(new GuideReference(chapter, "text", "Start Reading"));
                        ehPrimeiroCapitulo[0] = false;
                    }

                } catch (XMLStreamException ex) {
                    throw new IllegalStateException("Erro ao criar capitulo do epub: " + capitulo.titulo(), ex);
                }
            });

            var epubWriter = new EpubWriter();

            try {
                epubWriter.write(epub, Files.newOutputStream(arquivoSaida));
            } catch (IOException ex) {
                throw new IllegalStateException("Erro ao criar arquivo EPUB: " + arquivoSaida.toAbsolutePath(), ex);
            }

        } catch (Exception ex) {
            throw new IllegalStateException("Erro ao gerar EPUB: " + arquivoSaida.toAbsolutePath(), ex);
        }
    }
}

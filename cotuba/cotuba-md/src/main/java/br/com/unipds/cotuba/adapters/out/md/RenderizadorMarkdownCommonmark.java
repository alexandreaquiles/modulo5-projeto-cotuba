package br.com.unipds.cotuba.adapters.out.md;

import br.com.unipds.cotuba.domain.CapituloBuilder;
import br.com.unipds.cotuba.domain.Capitulo;
import br.com.unipds.cotuba.domain.Markdown;
import br.com.unipds.cotuba.plugin.CotubaPluginAposRenderizacao;
import br.com.unipds.cotuba.ports.out.RenderizadorMarkdown;
import jakarta.enterprise.context.ApplicationScoped;
import org.commonmark.node.AbstractVisitor;
import org.commonmark.node.Heading;
import org.commonmark.node.Node;
import org.commonmark.node.Text;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;

import java.util.List;
import java.util.ServiceLoader;

@ApplicationScoped
public class RenderizadorMarkdownCommonmark implements RenderizadorMarkdown {


    @Override
    public List<Capitulo> renderizar(List<Markdown> markdowns) {

        return markdowns.stream().map(markdown -> {

            var capituloBuilder = CapituloBuilder.builder();
            capituloBuilder.markdown(markdown);

            Parser parser = Parser.builder().build();
            Node document = null;
            try {

                document = parser.parse(markdown.conteudo());
                document.accept(new AbstractVisitor() {
                    @Override
                    public void visit(Heading heading) {
                        if (heading.getLevel() == 1) {
                            // capítulo
                            String tituloDoCapitulo = ((Text) heading.getFirstChild()).getLiteral();
                            capituloBuilder.titulo(tituloDoCapitulo);
                        } else if (heading.getLevel() == 2) {
                            // seção
                        } else if (heading.getLevel() == 3) {
                            // título
                        }
                    }

                });
            } catch (Exception ex) {
                throw new IllegalStateException("Erro ao fazer parse do arquivo " + markdown.nome(), ex);
            }

            try {
                HtmlRenderer renderer = HtmlRenderer.builder().build();
                String html = renderer.render(document);

                System.out.println("Renderizou HTML: ");

                for (CotubaPluginAposRenderizacao plugin : ServiceLoader.load(CotubaPluginAposRenderizacao.class)) {

                    System.out.println("Vai chamar plugin \n\n");

                    String htmlProcessado = plugin.aposRenderizacao(html);

                    System.out.println("Chamou plugin com : " + htmlProcessado);


                    if (htmlProcessado != null && !htmlProcessado.isBlank()) {
                        html = htmlProcessado;
                    }
                }

                capituloBuilder.html(html);

            } catch (Exception ex) {
                throw new IllegalStateException("Erro ao renderizar para HTML o arquivo " + markdown.nome(), ex);
            }

            return capituloBuilder.build();
        }).toList();

    }
}

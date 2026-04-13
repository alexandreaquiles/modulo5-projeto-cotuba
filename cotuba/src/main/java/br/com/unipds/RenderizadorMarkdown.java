package br.com.unipds;

import java.util.List;

public interface RenderizadorMarkdown {
    List<Capitulo> renderizar(List<Markdown> markdowns);
}

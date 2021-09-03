package markup;

import java.util.List;

public class Emphasis extends MarkupBlock implements NotAParagraph {

    public Emphasis(List<NotAParagraph> input) {
        super(input);
    }

    @Override
    public void toMarkdown(StringBuilder sb) {
        toMarkdownList(sb, "*", "*");
    }

    @Override
    public void toTex(StringBuilder sb) {
        toTeXList(sb, "\\emph{", "}");
    }
}

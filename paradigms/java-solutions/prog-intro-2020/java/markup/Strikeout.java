package markup;

import java.util.List;

public class Strikeout extends MarkupBlock implements NotAParagraph {

    Strikeout(List<NotAParagraph> input) {
        super(input);
    }

    @Override
    public void toMarkdown(StringBuilder sb) {
        toMarkdownList(sb, "~", "~");
    }

    @Override
    public void toTex(StringBuilder sb) {
        toTeXList(sb, "\\textst{", "}");
    }
}

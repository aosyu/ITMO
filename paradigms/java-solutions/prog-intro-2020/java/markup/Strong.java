package markup;

import java.util.List;

public class Strong extends MarkupBlock implements NotAParagraph {

    Strong(List<NotAParagraph> input) {
        super(input);
    }

    @Override
    public void toMarkdown(StringBuilder sb) {
        toMarkdownList(sb, "__", "__");
    }

    @Override
    public void toTex(StringBuilder sb) {
        toTeXList(sb, "\\textbf{", "}");
    }
}

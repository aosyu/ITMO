package markup;

import java.util.Collections;
import java.util.List;

public class Paragraph extends MarkupBlock implements ToMarkdown, ToParagraph {

    public Paragraph(List<NotAParagraph> content) {
        super(content);
    }

    @Override
    public void toMarkdown(StringBuilder sb) {
        toMarkdownList(sb, "", "");
    }

    @Override
    public void toTex(StringBuilder sb) {
        toTeXList(sb, "", "");
    }

}

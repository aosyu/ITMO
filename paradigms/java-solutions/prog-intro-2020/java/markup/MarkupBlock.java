package markup;

import java.util.List;

public class MarkupBlock {
    List<NotAParagraph> list;

    MarkupBlock(List<NotAParagraph> content) {
        this.list = content;
    }

    private void toList(boolean isMarkdown, StringBuilder sb, String begin, String end) {
        sb.append(begin);
        for (ToMarkdown t : list) {
            if (isMarkdown) {
                t.toMarkdown(sb);
            } else {
                t.toTex(sb);
            }
        }
        sb.append(end);
    }

    public void toMarkdownList(StringBuilder sb, String begin, String end) {
        toList(true, sb, begin, end);
    }

    public void toTeXList(StringBuilder sb, String begin, String end) {
        toList(false, sb, begin, end);
    }
}

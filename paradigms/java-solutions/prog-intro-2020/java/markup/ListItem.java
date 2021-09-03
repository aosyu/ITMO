package markup;

import java.util.List;

public class ListItem implements ToTex {
    private final List<ToParagraph> list;

    public ListItem(List<ToParagraph> content) {
        this.list = content;
    }

    @Override
    public void toTex(StringBuilder sb) {
        sb.append("\\item ");
        for (ToTex item : list) {
            item.toTex(sb);
        }
    }
}

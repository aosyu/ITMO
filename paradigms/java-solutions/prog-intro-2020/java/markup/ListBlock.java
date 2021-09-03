package markup;

import java.util.List;

public abstract class ListBlock {
    List<ListItem> list;

    ListBlock(List<ListItem> input) {
        this.list = input;
    }

    public void toTeXList(StringBuilder sb, String begin, String end) {
        sb.append(begin);
        for (ListItem t : list) {
            t.toTex(sb);
        }
        sb.append(end);
    }
}
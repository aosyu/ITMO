package markup;
import java.util.List;

public class OrderedList extends ListBlock implements ToParagraph {
    public OrderedList(List<ListItem> input) {
        super(input);
    }

    @Override
    public void toTex(StringBuilder sb) {
        toTeXList(sb, "\\begin{enumerate}", "\\end{enumerate}");
    }
}
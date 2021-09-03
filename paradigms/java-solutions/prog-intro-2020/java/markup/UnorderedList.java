package markup;
import java.util.List;

public class UnorderedList extends ListBlock implements ToParagraph {
    public UnorderedList(List<ListItem> input) {
        super(input);
    }

    @Override
    public void toTex(StringBuilder sb) {
        toTeXList(sb, "\\begin{itemize}", "\\end{itemize}");
    }
}
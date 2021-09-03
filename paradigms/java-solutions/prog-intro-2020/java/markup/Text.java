package markup;

public class Text implements NotAParagraph {
    String text;

    Text(String input) {
        this.text = input;
    }

    @Override
    public void toMarkdown(StringBuilder sb) {
        sb.append(text);
    }

    @Override
    public void toTex(StringBuilder sb) {
        sb.append(text);
    }
}

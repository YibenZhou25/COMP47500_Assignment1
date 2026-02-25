public class TextDocument {
    private StringBuilder content;

    public TextDocument() {
        this.content = new StringBuilder();
    }

    public void insert(int offset, String text) {
        content.insert(offset, text);
    }

    public void delete(int offset, int length) {
        content.delete(offset, offset + length);
    }

    public String getText() {
        return content.toString();
    }

    public int length() {
        return content.length();
    }
}
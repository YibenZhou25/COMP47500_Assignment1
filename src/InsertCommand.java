public class InsertCommand implements Command {
    private TextDocument document;
    private int offset;
    private String textToInsert;

    public InsertCommand(TextDocument document, int offset, String textToInsert) {
        this.document = document;
        this.offset = offset;
        this.textToInsert = textToInsert;
    }

    @Override
    public void execute() {
        document.insert(offset, textToInsert);
    }

    @Override
    public void undo() {
        document.delete(offset, textToInsert.length());
    }
}
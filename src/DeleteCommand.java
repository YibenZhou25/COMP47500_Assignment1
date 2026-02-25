public class DeleteCommand implements Command {
    private TextDocument document;
    private int offset;
    private int length;
    private String deletedText; 

    public DeleteCommand(TextDocument document, int offset, int length) {
        this.document = document;
        this.offset = offset;
        this.length = length;
    }

    @Override
    public void execute() {
        deletedText = document.getText().substring(offset, offset + length);
        document.delete(offset, length);
    }

    @Override
    public void undo() {
        document.insert(offset, deletedText);
    }
}
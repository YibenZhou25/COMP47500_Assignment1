public class EditorHistoryManager {
    private MyStack<Command> undoStack;
    private MyStack<Command> redoStack;

    public EditorHistoryManager(MyStack<Command> undoStack, MyStack<Command> redoStack) {
        this.undoStack = undoStack;
        this.redoStack = redoStack;
    }

    public void executeCommand(Command cmd) {
        cmd.execute();
        undoStack.push(cmd);
        redoStack.clear();
    }

    public void undo() {
        if (!undoStack.isEmpty()) {
            Command cmd = undoStack.pop();
            cmd.undo();
            redoStack.push(cmd);
        } else {
            System.out.println("Nothing to undo.");
        }
    }

    public void redo() {
        if (!redoStack.isEmpty()) {
            Command cmd = redoStack.pop();
            cmd.execute();
            undoStack.push(cmd);
        } else {
            System.out.println("Nothing to redo.");
        }
    }
}
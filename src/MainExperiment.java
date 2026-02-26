public class MainExperiment {

    // Benchmark configuration
    private static final int[] SIZES = {100_000, 500_000, 1_000_000, 2_000_000};
    private static final int WARMUP_RUNS = 2;
    private static final int MEASURED_RUNS = 5;

    public static void main(String[] args) {                
            runFunctionalTest();
            runPerformanceExperiments();
       
    }

    

    private static void runFunctionalTest() {
        System.out.println("=== Functional Test (Undo/Redo) ===");

        TextDocument doc = new TextDocument();
        EditorHistoryManager manager =
                new EditorHistoryManager(new LinkedListStack<>(), new LinkedListStack<>());

        // 1) Insert "Hello "
        Command cmd1 = new InsertCommand(doc, 0, "Hello ");
        manager.executeCommand(cmd1);

        // 2) Insert "World"
        Command cmd2 = new InsertCommand(doc, doc.length(), "World");
        manager.executeCommand(cmd2);

        System.out.println("Current     : " + doc.getText()); // Expected: Hello World

        // 3) Undo
        manager.undo();
        System.out.println("After Undo  : " + doc.getText()); // Expected: Hello

        // 4) Undo again
        manager.undo();
        System.out.println("After Undo  : " + doc.getText()); // Expected: (empty)

        // 5) Redo
        manager.redo();
        System.out.println("After Redo  : " + doc.getText()); // Expected: Hello

        System.out.println();
    }

    private static void runPerformanceExperiments() {
        System.out.println("=== Performance Experiment (Stack Push/Pop) ===");
        System.out.println("Warm-up runs: " + WARMUP_RUNS + " | Measured runs: " + MEASURED_RUNS);
        System.out.println();

        // A single command object reused as the stack element to reduce object-allocation noise.
        // We do NOT call execute() to avoid StringBuilder costs from TextDocument.
        TextDocument dummyDoc = new TextDocument();
        Command dummyCommand = new InsertCommand(dummyDoc, 0, "a");

        // Header
        System.out.printf("%-12s | %-18s | %-18s | %-18s%n",
                "Ops", "ArrayStack (untuned)", "ArrayStack (tuned)", "LinkedListStack");
        System.out.println("--------------------------------------------------------------------------------------");

        for (int ops : SIZES) {
            // Warm up JVM/JIT (not recorded)
            for (int i = 0; i < WARMUP_RUNS; i++) {
            	runSingleExperiment(() -> new ArrayStack<Command>(), ops, dummyCommand);
            	runSingleExperiment(() -> new ArrayStack<Command>(ops), ops, dummyCommand);
            	runSingleExperiment(() -> new LinkedListStack<Command>(), ops, dummyCommand);
            }

            Stats untuned = measure(() -> runSingleExperiment(() -> new ArrayStack<Command>(), ops, dummyCommand));
            Stats tuned   = measure(() -> runSingleExperiment(() -> new ArrayStack<Command>(ops), ops, dummyCommand));
            Stats list    = measure(() -> runSingleExperiment(() -> new LinkedListStack<Command>(), ops, dummyCommand));

            System.out.printf("%-12s | %-18s | %-18s | %-18s%n",
                    formatOps(ops),
                    untuned.format(),
                    tuned.format(),
                    list.format());
        }

        System.out.println();
        System.out.println("Notes:");
        System.out.println("- Times are in milliseconds (ms). Format: avg [min..max] over " + MEASURED_RUNS + " runs.");
        System.out.println("- Untuned ArrayStack uses default capacity; Tuned ArrayStack pre-allocates capacity = ops.");
        System.out.println("- Each run creates a fresh stack instance to avoid cross-run contamination.");
    }

  
    private static long runSingleExperiment(StackFactory factory, int operations, Command element) {
        MyStack<Command> stack = factory.create();

        long start = System.nanoTime();

        // Push
        for (int i = 0; i < operations; i++) {
            stack.push(element);
        }

        // Pop
        for (int i = 0; i < operations; i++) {
            stack.pop();
        }

        long end = System.nanoTime();
        return (end - start) / 1_000_000; // convert ns -> ms
    }

    /**
     * Measures MEASURED_RUNS times and returns average/min/max in ms.
     */
    private static Stats measure(LongSupplierMs trial) {
        long sum = 0;
        long min = Long.MAX_VALUE;
        long max = Long.MIN_VALUE;

        for (int i = 0; i < MEASURED_RUNS; i++) {
            long t = trial.getAsLong();
            sum += t;
            if (t < min) min = t;
            if (t > max) max = t;
        }

        long avg = sum / MEASURED_RUNS;
        return new Stats(avg, min, max);
    }

    private static String formatOps(int ops) {
        if (ops >= 1_000_000) return (ops / 1_000_000) + "M";
        if (ops >= 1_000) return (ops / 1_000) + "K";
        return String.valueOf(ops);
    }

    
    @FunctionalInterface
    private interface StackFactory {
        MyStack<Command> create();
    }

    @FunctionalInterface
    private interface LongSupplierMs {
        long getAsLong();
    }

    private static final class Stats {
        private final long avgMs;
        private final long minMs;
        private final long maxMs;

        private Stats(long avgMs, long minMs, long maxMs) {
            this.avgMs = avgMs;
            this.minMs = minMs;
            this.maxMs = maxMs;
        }

        private String format() {
            return avgMs + " [" + minMs + ".." + maxMs + "]";
        }
    }
}
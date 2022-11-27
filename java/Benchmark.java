import java.io.FileNotFoundException;

class Benchmark {
    private long testSequential() throws FileNotFoundException {
        long startTime = System.nanoTime();
        Chatbot cb = new Chatbot("../lotr.en");
        long stopTime = System.nanoTime();
        return stopTime - startTime;

    }

    private long testConcurrent() throws FileNotFoundException {
        long startTime = System.nanoTime();
        ThreadBot tb = new ThreadBot("../lotr.en", new DataMonitor());
        long stopTime = System.nanoTime();
        return stopTime - startTime;
    }

    public static void main(String[] args) throws FileNotFoundException {
        System.out.println("Will benchmark java solutions");
        Benchmark ben = new Benchmark();
        final long seqTime = ben.testSequential();
        System.out.println("Time for sequential solution: " + seqTime);
        final long conTime = ben.testConcurrent();
        System.out.println("Time for concurrent solution: " + conTime);
        final long diff = seqTime - conTime;
        final double percentage = (conTime / seqTime) * 100;
        System.out.println("Diff: " + diff + ", " + percentage + "%");
    }
}
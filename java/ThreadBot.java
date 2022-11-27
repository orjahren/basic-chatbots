import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.HashSet;

class ThreadBot extends Chatbot {
    class LineThread implements Runnable {
        String line;
        DataMonitor mon;

        LineThread(String line, DataMonitor mon) {
            this.line = line;
            this.mon = mon;
        }

        public void run() {

            ArrayList<String> ll = Chatbot.tokenise(line);
            this.mon.write(ll);
        }
    }

    DataMonitor mon;

    public ThreadBot(String fileName, DataMonitor mon) throws FileNotFoundException {
        super(fileName);
        this.mon = mon;
        System.out.println("Commencing reading the file in thread.");
        readFile(fileName);
        System.out.println("File is read.");
        System.out.println("Calculating base tf-idf scores");
        // TODO: This section should be parallellized
        this.docFrecs = regnDocFrecs();
        List<ArrayList<String>> utterances = this.mon.read();
        this.utterances = utterances;

        for (ArrayList<String> utterance : utterances) {
            HashMap<String, Double> tfidf = getTfidf(utterance, utterances.size());
            tfidfs.add(tfidf);
        }
        System.out.println("done.");
    }

    @Override
    protected void init(String fileName) throws FileNotFoundException {
        return;
    }

    public static void main(String[] args) {
        ThreadBot cb = null;
        DataMonitor mon = new DataMonitor();
        try {
            cb = new ThreadBot("../lotr.en", mon);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.exit(1);
        }

        final ArrayList<ArrayList<String>> utterances = mon.read();

        // Start a "conversation" with our bot
        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNextLine()) {
            System.out.println("Si noe til boten: ");
            String input = scanner.nextLine();
            String response = cb.getResponse(utterances, input);

            System.out.printf("Bot: %s%n", response);
        }

        scanner.close();
    }

    // reads an input file and calculates tfidfs for the vectorized input
    @Override
    protected void readFile(String fileName) throws FileNotFoundException {
        File file = new File(fileName);
        Scanner scanner = new Scanner(file);

        List<Thread> threads = new ArrayList<>();

        while (scanner.hasNextLine()) {
            String l = scanner.nextLine();
            Runnable rbl = new LineThread(l, mon);
            Thread t = new Thread(rbl);
            t.start();
            threads.add(t);

        }

        scanner.close();
        System.out.println("Scanning er ferdig");
        for (Thread t : threads) {
            try {
                t.join();
            } catch (InterruptedException e) {

            }
        }
    }

    // method for computing document frequency
    @Override
    protected HashMap<String, Double> regnDocFrecs() {
        HashMap<String, Double> df = new HashMap<>();
        ArrayList<ArrayList<String>> utterances = this.mon.read();
        for (ArrayList<String> ut : utterances) {
            HashSet<String> hs = new HashSet<>();

            for (String s : ut) {
                if (!hs.contains(s)) {
                    df.put(s, df.getOrDefault(s, Double.valueOf(-1)) + 1);
                    hs.add(s);
                }
            }
        }
        return df;
    }
}

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

    // list of tokenised utterances
    // list of term-frequency-inverted-document-frequencies
    protected List<HashMap<String, Double>> tfidfs = new ArrayList<>();
    // map of global token frequencies
    protected HashMap<String, Double> docFrecs;

    public ThreadBot(String fileName, DataMonitor mon) throws FileNotFoundException {
        super(fileName);
        System.out.println("Commencing reading the file.");
        this.readFile(fileName, mon);
        System.out.println("File is read.");
        System.out.println("Calculating base tf-idf scores");
        // TODO: This section should be parallellized
        docFrecs = regnDocFrecs(mon);

        List<ArrayList<String>> utterances = mon.read();

        for (ArrayList<String> utterance : utterances) {
            HashMap<String, Double> tfidf = super.getTfidf(utterance, utterances.size());
            tfidfs.add(tfidf);
        }
        System.out.println("done.");
    }

    public static void main(String[] args) {
        ThreadBot cb = null;
        DataMonitor mon = new DataMonitor();
        try {
            cb = new ThreadBot("lotr.en", mon);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.exit(1);
        }

        // Start a "conversation" with our bot
        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNextLine()) {
            System.out.println("Si noe til boten: ");
            String input = scanner.nextLine();
            String response = cb.getResponse(input, mon);

            System.out.printf("Bot: %s%n", response);
        }

        scanner.close();
    }

    // method for demonstrating some sentences
    public void test(DataMonitor mon) {
        String[] ts = {
                "Are you Bilbo Baggins ?",
                "Hello good sir, what a good day to live in the Shire t'is!",
                "I have three hens in my pen.",
                "Are you aware of these facts?",
                "One does not simply walk into mordor",
                "Fly , you fools .",
                "Fly you fools!",
                "I guide other to a treasure i cannot posess",
                "A god walks.",
                "Imagine Dragons fly above us.",
                "HAVE YOU GOT THE BIG BRAIN?",
                "Oh say, can you see?",
                "Les francais doivent mourir pour leur empereur"
        };
        for (String s : ts) {
            System.out.println(s + " : " + this.getResponse(s, mon) + "\n");
        }
    }

    // reads an input file and calculates tfidfs for the vectorized input
    private void readFile(String fileName, DataMonitor mon) throws FileNotFoundException {
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

    // method taking an input query and returning a string response
    public String getResponse(String query, DataMonitor mon) {
        ArrayList<ArrayList<String>> utterances = mon.read();
        final int n_utterances = utterances.size();
        HashMap<String, Double> q = super.getTfidf(super.tokenise(query), n_utterances);
        double mesteCs = -9999999;
        int idx = 0;
        for (int i = 0; i < tfidfs.size(); i++) {
            double cs = super.computeCosine(q, tfidfs.get(i));
            if (cs > mesteCs) {
                mesteCs = cs;
                idx = i + 1;
            }
        }
        // retrive response
        ArrayList<String> responseTokenList = utterances.get(idx);
        // remove first token
        String response = responseTokenList.remove(0);
        // make first letter capital
        response = String.valueOf(response.charAt(0)).toUpperCase() + response.substring(1);
        // append other tokens
        for (String token : responseTokenList) {
            // make space iff the token is alphanumeric
            if (token.matches("\\w+")) {
                response += " ";
            }
            response += token;
        }
        return response;
    }

    // method for computing document frequency
    private HashMap<String, Double> regnDocFrecs(DataMonitor mon) {
        HashMap<String, Double> df = new HashMap<>();
        ArrayList<ArrayList<String>> utterances = mon.read();
        for (ArrayList<String> ut : utterances) {
            HashSet<String> hs = new HashSet<>();

            for (String s : ut) {
                if (!hs.contains(s)) {
                    df.put(s, df.getOrDefault(s, Double.valueOf(-1)));
                    hs.add(s);
                }
            }
        }
        return df;
    }
}

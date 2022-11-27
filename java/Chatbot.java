import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Collections;

class Chatbot {
    // list of tokenised utterances
    protected List<ArrayList<String>> utterances;
    // list of term-frequency-inverted-document-frequencies
    protected List<HashMap<String, Double>> tfidfs;
    // map of global token frequencies
    protected HashMap<String, Double> docFrecs;

    public Chatbot(String fileName) throws FileNotFoundException {
        this.utterances = new ArrayList<ArrayList<String>>();
        this.tfidfs = new ArrayList<>();
        init(fileName);
    }

    protected void init(String fileName) throws FileNotFoundException {

        System.out.println("Commencing reading the file.");
        readFile(fileName);
        System.out.println("File is read.");
        System.out.println("Calculating base tf-idf scores");
        this.docFrecs = regnDocFrecs();
        for (ArrayList<String> utterance : this.utterances) {
            HashMap<String, Double> tfidf = getTfidf(utterance, utterances.size());
            tfidfs.add(tfidf);
        }
    }

    public static void main(String[] args) {
        Chatbot cb = null;
        try {
            cb = new Chatbot("../lotr.en");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.exit(1);
        }

        // Start a "conversation" with our bot
        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNextLine()) {
            String input = scanner.nextLine();
            if (input.equals("!")) {
                break;
            }
            String response = cb.getResponse(cb.utterances, input);

            System.out.printf("Bot: %s%n", response);
        }
        scanner.close();
    }

    // method for demonstrating some sentences
    public void test() {
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
            System.out.println(s + " : " + this.getResponse(this.utterances, s) + "\n");
        }
    }

    // reads an input file and calculates tfidfs for the vectorized input
    protected void readFile(String fileName) throws FileNotFoundException {
        File file = new File(fileName);
        Scanner scanner = new Scanner(file);

        while (scanner.hasNextLine()) {
            String l = scanner.nextLine();
            ArrayList<String> ll = tokenise(l);
            utterances.add(ll);
        }
        scanner.close();
    }

    // method taking an input query and returning a string response
    public String getResponse(List<ArrayList<String>> utterances, String query) {
        HashMap<String, Double> q = this.getTfidf(tokenise(query), utterances.size());
        double mesteCs = -9999999;
        int idx = 0;
        for (int i = 0; i < tfidfs.size(); i++) {
            double cs = this.computeCosine(q, tfidfs.get(i));
            if (cs > mesteCs) {
                mesteCs = cs;
                idx = i + 1;
            }
        }
        // retrive response
        ArrayList<String> responseTokenList = new ArrayList<>(utterances.get(idx));
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
    protected HashMap<String, Double> regnDocFrecs() {
        HashMap<String, Double> df = new HashMap<>();
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

    // method for computing the tfidf of a tokenised input
    protected HashMap<String, Double> getTfidf(ArrayList<String> utterance,
            final int n_utterances) {
        HashMap<String, Double> tfidf_vals = new HashMap<>();
        HashMap<String, Integer> counts = new HashMap<>();
        // find frequency of all tokens in the utterance
        for (String s : utterance) {
            int x = Collections.frequency(utterance, s);
            counts.put(s, x);
        }

        // calculate tfidf for every type
        for (String s : counts.keySet()) {
            double idf = Math.log(n_utterances / (docFrecs.getOrDefault(s, Double.valueOf(0)) + 1));
            tfidf_vals.put(s, idf * counts.get(s));
        }
        return tfidf_vals;
    }

    // method for basic simple tokenization
    public static ArrayList<String> tokenise(String s) {
        ArrayList<String> ll = new ArrayList<>();
        for (String x : s.split(" ")) {
            ll.add(x.toLowerCase().replaceAll("[\n\r]", ""));
        }
        return ll;
    }

    // method for calculating vector norm
    protected double getNorm(HashMap<String, Double> tfidf) {
        double sum = 0;
        for (Double f : tfidf.values()) {
            sum += Math.pow(f, 2);
        }
        return Math.sqrt(sum);
    }

    // method for computing the cosine similarity of two vectors. Vecs here
    // represented as hashmaps
    protected double computeCosine(HashMap<String, Double> tf1, HashMap<String, Double> tf2) {
        double dp = 0;
        for (String s : tf1.keySet()) {
            if (tf2.containsKey(s)) {
                dp += tf1.get(s) * tf2.get(s);
            }
        }
        return dp / (getNorm(tf1) * getNorm(tf2));
    }
}

import java.io.FileNotFoundException;

class TestBot {
    public static void main(String[] args) throws FileNotFoundException {
        Chatbot cb = new Chatbot("../lotr.en");
        cb.test();
    }
}
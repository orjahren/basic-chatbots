import java.io.FileNotFoundException;

class TestThread {
    public static void main(String[] args) throws FileNotFoundException {
        System.out.println("Will test threadbot.");
        DataMonitor mon = new DataMonitor();
        System.out.println("Initting bot class.");
        ThreadBot cb = new ThreadBot("../lotr.en", mon);
        System.out.println("Running test method.");
        cb.test();
    }
}
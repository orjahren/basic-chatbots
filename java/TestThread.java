import java.io.FileNotFoundException;

class TestThread {
    public static void main(String[] args) throws FileNotFoundException {
        DataMonitor mon = new DataMonitor();
        ThreadBot cb = new ThreadBot("../lotr.en", mon);
        cb.test(mon);
    }
}
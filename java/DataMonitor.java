import java.util.ArrayList;
import java.util.concurrent.Semaphore;

class DataMonitor {
    private ArrayList<ArrayList<String>> utterances;

    private Semaphore sem;

    DataMonitor() {
        this.utterances = new ArrayList<ArrayList<String>>();
        this.sem = new Semaphore(0);

    }

    public void write(ArrayList<String> sl) {
        try {

            sem.acquire();
        } catch (InterruptedException e) {

            // Thread.currentThread().si
        }
        utterances.add(sl);
        sem.release();
    }

    public ArrayList<ArrayList<String>> read() {
        return this.utterances;
    }

}
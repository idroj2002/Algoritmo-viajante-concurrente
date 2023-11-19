import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinWorkerThread;

public class PriorityWorkerThread extends ForkJoinWorkerThread {
    protected PriorityWorkerThread(ForkJoinPool pool) {
        super(pool);
    }
}

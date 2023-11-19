import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinWorkerThread;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.TimeUnit;

public class PriorityForkJoinPool extends ForkJoinPool {
    public PriorityForkJoinPool(int parallelism, ForkJoinPool.ForkJoinWorkerThreadFactory factory,
                                Thread.UncaughtExceptionHandler handler, boolean asyncMode,
                                int corePoolSize, int maximumPoolSize, long keepAliveTime,
                                TimeUnit unit, PriorityBlockingQueue<Runnable> workQueue) {
        super();
    }

    /*
    public ForkJoinWorkerThread newThread(ForkJoinPool pool) {
        return new PriorityWorkerThread(pool);
    }*/

    // Puedes personalizar otras configuraciones según tus necesidades
}

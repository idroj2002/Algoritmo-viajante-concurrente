import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.*;

import static java.lang.System.exit;
import static java.lang.Thread.sleep;


public class TSP
{
    public static final Boolean DEBUG = false;
    //public static final int INF = Integer.MAX_VALUE;
    public static final int INF = -1;
    public static final int C_MATRIX_PADDING = 3;
    private static final int DEFAULT_THREADS = 8;

    private enum ConcurrentMethod { FixedThreadPool, CachedThreadPool, ForkJoinPool }
    public int[][] distanceMatrix;
    private int NCities = 0;

    // Concurrent variables
    private ExecutorService pool;
    private final int threads;
    private ConcurrentMethod concurrentMethod;
    private final PriorityBlockingQueue<Node> solution = new PriorityBlockingQueue<>();
    private ForkJoinPool forkJoinPool = null;

    // Statistics of purged and processed nodes.
    private ConcurrentHashMap<String, Long> metrics = new ConcurrentHashMap();
    private long purgedNodes = 0;
    private long processedNodes = 0;


    // Getters & Setters
    public int getNCities() {
        return NCities;
    }
    public void setNCities(int NCities) {
        this.NCities = NCities;
    }
    public void incrementProcessedNodes() {
        metrics.merge("processedNodes", 1L, Long::sum);
    }
    public void incrementPurgedNodes() {
        metrics.merge("purgedNodes", 1L, Long::sum);
    }
    public Node getSolution() {
        return solution.peek();
    }
    public void setSolution(Node sol) {
        this.solution.add(sol);
        // ELIMINAR NODOS INNECESARIOS
        /*solution.forEach(node -> {
            if (node.getCost() > sol.getCost()) {
                solution.remove(node);
            }
        });*/
        /*((ThreadPoolExecutor) pool).getQueue().forEach(runnable -> {
            FindTSPTask task = (FindTSPTask) runnable;
            if (task != null) {
                if (task.getNode().getCost() > 364) System.out.print(task.getNode().getCost() + ", ");
            }
        });
        System.out.print("]\n");*/
    }
    public int getDistanceMatrix(int i, int j) { return distanceMatrix[i][j]; }
    public int[][] getDistanceMatrix() {
        return distanceMatrix;
    }
    public long getPurgedNodes() { return purgedNodes; }
    public long getProcessedNodes() { return processedNodes; }
    public void prugedNodesIncrement() { purgedNodes++; }

    // Constructors.
    public TSP()
    {
        initDefaultCitiesDistances();
        this.threads = DEFAULT_THREADS;
        this.concurrentMethod = ConcurrentMethod.FixedThreadPool;
        initPool();
        initMetrics();
    }
    public TSP(String citiesPath)
    {
        radCitiesFile(citiesPath);
        this.threads = DEFAULT_THREADS;
        this.concurrentMethod = ConcurrentMethod.FixedThreadPool;
        initPool();
        initMetrics();
    }
    public TSP(String citiesPath, int threadsNum)
    {
        radCitiesFile(citiesPath);
        this.threads = threadsNum;
        this.concurrentMethod = ConcurrentMethod.FixedThreadPool;
        initPool();
        initMetrics();
    }
    public TSP(String citiesPath, int threadsNum, String concurrentMethod)
    {
        radCitiesFile(citiesPath);
        this.threads = threadsNum;
        switch (concurrentMethod) {
            case "FixedThreadPool":
                this.concurrentMethod = ConcurrentMethod.FixedThreadPool;
                break;
            case "CachedThreadPool":
                this.concurrentMethod = ConcurrentMethod.CachedThreadPool;
                break;
            case "ForkJoinPool":
                this.concurrentMethod = ConcurrentMethod.ForkJoinPool;
                break;
            default:
                System.err.println("Invalid concurrent method. Concurrent methods accepted are:\n" +
                        "\t- FixedThreadPool\n" +
                        "\t- CachedThreadPool\n" +
                        "\t- ForkJoinPool\n");
                exit(-1);
        }
        initPool();
        initMetrics();
    }

    public void initMetrics()
    {
        metrics.put("processedNodes", 0L);
        metrics.put("purgedNodes", 0L);
    }

    public void initDefaultCitiesDistances()
    {
        distanceMatrix = new int[][]{{INF, 10, 15, 20},
                                    {10, INF, 35, 25},
                                    {15, 35, INF, 30},
                                    {20, 25, 30, INF}};
        NCities = 4;
        System.out.println("Default matrix:\n" + Arrays.deepToString(distanceMatrix));
        System.out.println("Num. of cities:" + NCities);
    }

    private void initPool() {
        if (concurrentMethod == ConcurrentMethod.FixedThreadPool) {
            //pool = Executors.newFixedThreadPool(threads);
            BlockingQueue<Runnable> queue = new PriorityBlockingQueue<>(11, new FindTSPPoolTaskComparator());

            pool = new ThreadPoolExecutor(
                    threads, // Tamaño del pool
                    threads, // Tamaño máximo del pool
                    0L, TimeUnit.MILLISECONDS, // Tiempo de espera antes de que se eliminen los hilos inactivos
                    queue // Cola de trabajos
            );
        } else if (concurrentMethod == ConcurrentMethod.CachedThreadPool) {
            BlockingQueue<Runnable> queue = new PriorityBlockingQueue<>(11, new FindTSPPoolTaskComparator());

            pool = new ThreadPoolExecutor(
                    0, // Tamaño del pool
                    threads, // Tamaño máximo del pool
                    60L, TimeUnit.SECONDS, // Tiempo de espera antes de que se eliminen los hilos inactivos
                    queue // Cola de trabajos
            );
        } else {
            pool = null;
            PriorityBlockingQueue<Runnable> queue = new PriorityBlockingQueue<>(11, new FindTSPForkTaskComparator());
            forkJoinPool = new PriorityForkJoinPool(
                    Runtime.getRuntime().availableProcessors(),
                    ForkJoinPool.defaultForkJoinWorkerThreadFactory,
                    null, false, 1, 1, 0, TimeUnit.MILLISECONDS, queue
            );
            /*forkJoinPool = new ForkJoinPool();*/
        }
    }

    public void radCitiesFile(String citiesPath)
    {
        Scanner input = null;
        try {
            input = new Scanner(new File(citiesPath));

            // Read the number of cities
            NCities = 0;
            if (input.hasNextInt())
                NCities = input.nextInt();
            else
                System.err.printf("[TSP::ReadCitiesFile] Error reading cities number on %s.\n", citiesPath);

            // Init cities' distances matrix
            distanceMatrix = new int[NCities][NCities];

            // Read cities distances
            for (int i = 0; i < NCities; ++i) {
                for (int j = 0; j < NCities; ++j) {
                    distanceMatrix[i][j] = 0;
                    if (input.hasNextInt())
                        distanceMatrix[i][j] = input.nextInt();
                    else
                        System.err.printf("[TSP::ReadCitiesFile] Error reading distance beetwen cities %d-%d.\n", i, j);
                }
            }

        } catch (FileNotFoundException e) {
            System.err.printf("[TSP::ReadCitiesFile] File %s not found.\n",citiesPath);
            e.printStackTrace();
        } catch (IOException e) {
            System.err.printf("[TSP::ReadCitiesFile] Error file reading %s.\n",citiesPath);
            e.printStackTrace();
        }
    }

    public Node solve()
    {
        Instant start = Instant.now();

        System.out.println("\n___________________________________________________________________________________________________________________________________________________");
        System.out.printf("Test with %d cities.\n",getNCities());

        Node solution = concurrentMethod == ConcurrentMethod.ForkJoinPool ? forkJoinSolve(distanceMatrix) : poolSolve(distanceMatrix);
        printSolution("\nOptimal Solution: ", solution);

        Instant finish = Instant.now();
        long timeElapsed = Duration.between(start, finish).toMillis();  //in millis
        System.out.printf("Total execution time: %.3f secs with %d cities.\n", timeElapsed/1000.0,getNCities());

        return solution;
    }

    // Function to solve the traveling salesman problem using Branch and Bound
    public Node poolSolve(int CostMatrix[][])
    {
        // Create a root node and calculate its cost. The TSP starts from the first city, i.e., node 0
        Node root = new Node(this, CostMatrix);
        //System.out.println(root);

        // Calculate the lower bound of the path starting at node 0
        root.calculateSetCost();

        addNodeToPool(root);

        long ProcessedNodes = metrics.get("processedNodes");
        long PurgedNodes = metrics.get("purgedNodes");
        long totalNodes = ProcessedNodes + PurgedNodes;
        int a = 0;

        if (DEBUG) System.out.printf("\nTotal nodes: %d \tProcessed nodes: %d \tPurged nodes: %d \tPending nodes: %d \tBest Solution: %d.\r",totalNodes, ProcessedNodes, PurgedNodes, a/*NodesQueue.size()*/,getSolution()==null?0:getSolution().getCost());

        while (true) {
            // Dormir por un breve período de tiempo para evitar una espera activa intensiva
            try {
                sleep(100);
                ProcessedNodes = metrics.get("processedNodes");
                PurgedNodes = metrics.get("purgedNodes");
                totalNodes = ProcessedNodes + PurgedNodes;
                if (true) System.out.printf("Total nodes: %d \tProcessed nodes: %d \tPurged nodes: %d \tPending nodes: %d \tBest Solution: %d\r",totalNodes, ProcessedNodes, PurgedNodes, ((ThreadPoolExecutor) pool).getQueue().size(),getSolution()==null?0:getSolution().getCost());
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            // Obtener la cantidad de tareas en cola
            int queueSize = ((ThreadPoolExecutor) pool).getQueue().size();

            // Si no hay tareas en cola, salir del bucle
            if (queueSize == 0 && ((ThreadPoolExecutor) pool).getActiveCount() == 0) {
                pool.shutdown();
                break;
            }
        }
        ProcessedNodes = metrics.get("processedNodes");
        PurgedNodes = metrics.get("purgedNodes");
        totalNodes = ProcessedNodes + PurgedNodes;
        if (true) System.out.printf("Final total nodes: %d \tProcessed nodes: %d \tPurged nodes: %d \tPending nodes: %d \tBest Solution: %d",totalNodes, ProcessedNodes, PurgedNodes, ((ThreadPoolExecutor) pool).getQueue().size(),getSolution()==null?0:getSolution().getCost());
        return getSolution();  // Return solution
    }

    public Node forkJoinSolve(int CostMatrix[][])
    {
        // Create a root node and calculate its cost. The TSP starts from the first city, i.e., node 0
        Node root = new Node(this, CostMatrix);
        //System.out.println(root);

        // Calculate the lower bound of the path starting at node 0
        root.calculateSetCost();

        addToForkJoinPool(root);

        return getSolution();

        /*FindTSPForkTask principalTask = new FindTSPForkTask(this, root);
        try {
            return forkJoinPool.invoke(principalTask);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
        return null;*/
    }

    public void addNodeToPool(Node node) {
         pool.execute(new FindTSPPoolTask(this, node));
    }

    public void addToForkJoinPool(Node node) {
        FindTSPForkTask findTSPForkTask = new FindTSPForkTask(this, node);
        forkJoinPool.invoke(findTSPForkTask);
    }

    // Purge nodes from the queue whose cost is bigger than the minCost.
    public void purgeWorseNodes(int minCost)
    {
        if (concurrentMethod != ConcurrentMethod.ForkJoinPool) {
            Iterator<Runnable> iterator = ((ThreadPoolExecutor) pool).getQueue().iterator();
            while (iterator.hasNext()) {
                Runnable runnable = iterator.next();
                if (runnable instanceof FindTSPPoolTask) {
                    FindTSPPoolTask task = (FindTSPPoolTask) runnable;
                    if (task.getNode().getCost() > minCost) {
                        iterator.remove(); // Utiliza el iterador para eliminar de forma segura
                        incrementPurgedNodes();
                    }
                }
            }
        }
    }

    // Print the solution to console
    public void printSolution(String msg, Node sol) {
        printSolution(System.out, msg, sol);
    }

    // Print the solution to PrintStream
    public void printSolution(PrintStream out, String msg, Node sol) {
        out.print(msg);
        sol.printPath(out, true);
    }
}

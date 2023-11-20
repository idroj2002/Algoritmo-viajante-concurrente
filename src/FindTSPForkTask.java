import java.util.ArrayList;
import java.util.concurrent.RecursiveAction;
import java.util.concurrent.RecursiveTask;

public class FindTSPForkTask extends RecursiveTask<Node> {
    private final int LIMIT_CONCUR;
    TSP tsp;
    Node node;

    public FindTSPForkTask(TSP tsp, Node node) {
        this.tsp = tsp;
        this.node = node;
        this.LIMIT_CONCUR = tsp.getNCities() - 5;
    }

    @Override
    protected Node compute() {
        /*if (node.getLevel() == tsp.getNCities() - 1) {
            // Si todas las ciudades han sido visitadas, volver a la ciudad de inicio
            node.addPathStep(node.getVertex(), 0);
            return node;
        } else if (node.getLevel() >= LIMIT_CONCUR) {
            // Caso base: resolver de manera secuencial para tareas pequeñas
            return solveSec();
        } else {
            // Dividir la tarea en sub-tareas y ejecutarlas en paralelo
            return divideAndConquer();
        }*/
        boolean finish = false;
        Node nextNode;

        while (!finish){
            nextNode = null;
            int i = node.getVertex();

            // If all cities are visited
            if (node.getLevel() == tsp.getNCities()-1)
            {
                // Return to starting city
                node.addPathStep(i, 0);

                if (tsp.getSolution()==null || node.getCost()<tsp.getSolution().getCost())
                {   // Found sub-optimal solution
                    tsp.setSolution(node);
                }
            }

            // Do for each child of min (i, j) forms an edge in a space tree
            for (int j = 0; j < tsp.getNCities(); j++)
            {
                // if city is not visited create child node
                if (node.cityVisited(j)==false && node.getCostMatrix(i,j) != tsp.INF)
                {

                    // Create a child node and calculate its cost
                    Node child = new Node(tsp, node, node.getLevel() + 1, i, j);
                    int child_cost = node.getCost() + node.getCostMatrix(i,j) +
                            child.calculateCost();

                    // Add node to pending nodes queue if its costs is lower than better solution
                    if (tsp.getSolution()==null || child_cost<tsp.getSolution().getCost())
                    {
                        // Add a child to the list of live nodes
                        child.setCost (child_cost);
                        if (nextNode == null || child_cost < nextNode.getCost()) {
                            if (nextNode != null) tsp.addToForkJoinPool(nextNode);
                            nextNode = child;
                        } else {
                            tsp.addToForkJoinPool(child);
                        }
                    }
                }
            }
            if (nextNode == null) finish = true; // No tiene hijos
            else node = nextNode;
        }
        return null;
    }

    /*private Node solveSec() {
        Node bestSol = null;
        for (int i = 0; i < tsp.getNCities(); i++) {
            if (!node.cityVisited(i) && node.getCostMatrix(node.getVertex(), i) != tsp.INF && (bestSol == null || node.compareTo(bestSol) < 0)) {
                Node child = new Node(tsp, node, node.getLevel() + 1, node.getVertex(), i);
                int child_cost = node.getCost() + node.getCostMatrix(node.getVertex(), i) +
                        child.calculateCost();
                child.setCost(child_cost);
                Node newSol = new FindTSPForkTask(tsp, child).compute();
                if (bestSol == null) {
                    bestSol = newSol;
                } else {
                    bestSol = newSol.compareTo(bestSol) < 0 ? newSol : bestSol;
                }
            }
        }
        return bestSol == null ? node : bestSol;
    }

    private Node divideAndConquer() {
        // Dividir la tarea en sub-tareas
        Node bestSol = null;
        for (int i = 0; i < tsp.getNCities(); i++) {
            if (!node.cityVisited(i) && node.getCostMatrix(node.getVertex(), i) != tsp.INF && (bestSol == null || node.compareTo(bestSol) < 0)) {
                Node child = new Node(tsp, node, node.getLevel() + 1, node.getVertex(), i);
                int child_cost = node.getCost() + node.getCostMatrix(node.getVertex(), i) +
                        child.calculateCost();
                child.setCost(child_cost);
                FindTSPForkTask subTask = new FindTSPForkTask(tsp, child);
                subTask.fork();
                Node newSol = subTask.join();
                bestSol = (bestSol == null || newSol.compareTo(bestSol) < 0) ? newSol : bestSol;
            }
        }
        return bestSol == null ? node : bestSol;
    }*/
}

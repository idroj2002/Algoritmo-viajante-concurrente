public class FindTSPPoolTask implements Runnable {
    
    TSP tsp;
    Node node;
    
    public FindTSPPoolTask(TSP tsp, Node node) {
        this.tsp = tsp;
        this.node = node;
    }

    public Node getNode() { return node; }

    @Override
    public void run() {
        boolean finish = false;
        Node nextNode;

        while (!finish){
            tsp.incrementProcessedNodes();
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
                    if (TSP.DEBUG) System.out.println("Set cost to " + node.getCost());

                    // Remove nodes from Nodes queue that can not improved last found solution
                    tsp.purgeWorseNodes(node.getCost());
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
                            if (nextNode != null) tsp.addNodeToPool(nextNode);
                            nextNode = child;
                        } else {
                            tsp.addNodeToPool(child);
                        }
                    } else {
                        tsp.incrementPurgedNodes();
                    }
                }
            }
            if (nextNode == null) finish = true; // No tiene hijos
            else node = nextNode;
        }
    }
}

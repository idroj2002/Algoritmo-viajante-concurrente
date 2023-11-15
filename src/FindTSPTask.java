public class FindTSPTask implements Runnable {
    
    TSP tsp;
    Node node;
    
    public FindTSPTask(TSP tsp, Node node) {
        this.tsp = tsp;
        this.node = node;
    }
    
    @Override
    public void run() {
        int i = node.getVertex();

        // If all cities are visited
        if (node.getLevel() == tsp.getNCities()-1)
        {
            // Return to starting city
            node.addPathStep(i, 0);

            if (tsp.getSolution()==null || node.getCost()<tsp.getSolution().getCost())
            {   // Found sub-optimal solution
                tsp.setSolution(node);

                // Remove nodes from Nodes queue that can not improved last found solution
                tsp.PurgeWorseNodes(node.getCost());
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
                    tsp.pushNode(child);
                }
                else if (tsp.getSolution()!=null && child_cost>tsp.getSolution().getCost())
                    tsp.prugedNodesIncrement();

                tsp.addNodeToPool(child);
            }
        }
    }
}


import org.javatuples.Pair;

import java.io.PrintStream;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Vector;
import java.util.stream.Collectors;

// Node that implements the Branch&Bound search three elements in the solution space
public class Node
{
    // Helps in tracing the path when the answer is found
    // Stores the edges of the path completed till current visited node
    Vector<Pair<Integer, Integer> > path;

    // Stores the reduced matrix
    int reducedMatrix[][];
    // Stores the cities already visited.
    Boolean visitedCities[];
    // Stores the lower bound
    int cost;
    // Stores the current city number
    int vertex;
    // Stores the total number of cities visited
    int level;

    TSP tsp;
    long id = 0;

    private static long TotalNodes=0;

    // Setters & Getters
    public long getId() {
        return id;
    }
    public static long getTotalNodes() { return TotalNodes; }
    public Boolean[] getVisitedCities() { return visitedCities; }
    public Boolean cityVisited(int city){ return visitedCities[city]; }
    public Vector<Pair<Integer, Integer>> getPath() {
        return path;
    }
    public void setPath(Vector<Pair<Integer, Integer>> path) {
        this.path = path;
    }
    public int[][] getReducedMatrix() {
        return reducedMatrix;
    }
    public void setReducedMatrix(int[][] reducedMatrix) {
        this.reducedMatrix = reducedMatrix;
    }
    public int getCostMatrix(int i, int j) { return reducedMatrix[i][j]; }
    public int getCost() {
        return cost;
    }
    public void setCost(int cost) {
        this.cost = cost;
    }
    public void calculateSetCost() { setCost(calculateCost()); }
    public int getVertex() {
        return vertex;
    }
    public void setVertex(int vertex) {
        this.vertex = vertex;
    }
    public int getLevel() {
        return level;
    }
    public void setLevel(int level) {
        this.level = level;
    }
    public void addPathStep(int startCity, int destCity)
    {
        path.addElement(new Pair(startCity, destCity));
    }

    // Constructors
    public Node(TSP tsp, int[][] reducedMatrix)
    {
        this.tsp = tsp;

        // Reserve and clone reduce matrix.
        this.reducedMatrix = new int[tsp.getNCities()][tsp.getNCities()];
        for(int x = 0; x < reducedMatrix.length; x++)
            this.reducedMatrix[x] = reducedMatrix[x].clone();

        // Reserve and initialize to false the visited cities array.
        this.visitedCities = new Boolean[tsp.getNCities()];
        Arrays.fill(this.visitedCities, false);
        this.visitedCities[0] = true;

        this.path = new Vector<>();
        this.cost = 0;
        this.vertex = 0;
        this.level = 0;
        this.id = ++TotalNodes;
    }

    public Node(TSP tsp, Node parentNode, int level, int i, int j)
    {
        this.tsp = tsp;
        this.cost = 0;
        this.vertex = j;
        this.level = level;
        this.id = ++TotalNodes;

        // Copy path data from the parent node to the current node
        if (parentNode.getPath()!=null)
            this.path = ( Vector<Pair<Integer, Integer> >) parentNode.getPath().clone();
        else
            this.path = new Vector<>();
        // Skip for the root node
        if (level != 0)
            // Add a current edge to the path
            addPathStep(i,j);

        // Copy reduce matrix data from the parent node to the current node
        this.reducedMatrix = new int[tsp.getNCities()][tsp.getNCities()];
        for(int x = 0; x < parentNode.getReducedMatrix().length; x++)
            this.reducedMatrix[x] = parentNode.getReducedMatrix()[x].clone();

        // Reserve and clone the visited cities array.
        this.visitedCities = new Boolean[tsp.getNCities()];
        this.visitedCities = parentNode.getVisitedCities().clone();
        this.visitedCities[j] = true;

        // Change all entries of row i and column j to INF skip for the root node
        for (int k = 0; level != 0 && k < tsp.getNCities(); k++)
        {
            // Set outgoing edges for the city i to INF
            reducedMatrix[i][k] = tsp.INF;
            // Set incoming edges to city j to INF
            reducedMatrix[k][j] = tsp.INF;
        }

        // Set (j, 0) to INF here start node is 0
        reducedMatrix[j][0] = tsp.INF;
    }

    // Function to get the lower bound on the path  starting at the current minimum node
    public int calculateCost()
    {
        // Initialize cost to 0
        int cost = 0;

        // Row Reduction
        int row[] = new int[tsp.getNCities()];
        rowReduction(reducedMatrix, row);

        // Column Reduction
        int col[] = new int[tsp.getNCities()];
        columnReduction(reducedMatrix, col);

        // The total expected cost is the sum of all reductions
        for (int i = 0; i < tsp.getNCities(); i++) {
            cost += (row[i] != tsp.INF) ? row[i] : 0;
            cost += (col[i] != tsp.INF) ? col[i] : 0;
        }

        return cost;
    }

    // Function to reduce each row so that there must be at least one zero in each row
    public void rowReduction(int reducedMatrix[][], int row[])
    {
        // Initialize row array to INF
        java.util.Arrays.fill(row, tsp.INF);

        // row[i] contains minimum in row i
        for (int i = 0; i < tsp.getNCities(); i++) {
            for (int j = 0; j < tsp.getNCities(); j++) {
                if (reducedMatrix[i][j] != tsp.INF && (row[i]==tsp.INF || reducedMatrix[i][j] < row[i])) {
                    row[i] = reducedMatrix[i][j];
                }
            }
        }

        // Reduce the minimum value from each element in each row
        for (int i = 0; i < tsp.getNCities(); i++) {
            for (int j = 0; j < tsp.getNCities(); j++) {
                if (reducedMatrix[i][j] != tsp.INF && row[i] != tsp.INF) {
                    reducedMatrix[i][j] -= row[i];
                }
            }
        }

    }

    // Function to reduce each column so that there must be at least one zero in each column
    public void columnReduction(int reducedMatrix[][], int col[])
    {
        // Initialize all elements of array col with INF
        java.util.Arrays.fill(col, tsp.INF);

        // col[j] contains minimum in col j
        for (int i = 0; i < tsp.getNCities(); i++) {
            for (int j = 0; j < tsp.getNCities(); j++) {
                if (reducedMatrix[i][j] != tsp.INF && (col[j]==tsp.INF || reducedMatrix[i][j] < col[j])) {
                    col[j] = reducedMatrix[i][j];
                }
            }
        }
        // Reduce the minimum value from each element
        // in each column
        for (int i = 0; i < tsp.getNCities(); i++) {
            for (int j = 0; j < tsp.getNCities(); j++) {
                if (reducedMatrix[i][j] != tsp.INF  &&  col[j] != tsp.INF) {
                    reducedMatrix[i][j] -= col[j];
                }
            }
        }
    }

    // Function to print list of cities visited following least cost
    void printPath(PrintStream out,Boolean withCosts)
    {
        out.print(pathToString(withCosts));
    }
    public String pathToString(Boolean withCosts)
    {
        //String out ="[Node %d] "+getId()+"\n";
        String out = path.stream()
                .map((step) ->  {
                        String out_step;
                        if (withCosts)
                            out_step = (step.getValue0()+1)+"->"+(step.getValue1()+1)+" ("+tsp.getDistanceMatrix(step.getValue0(), step.getValue1())+")";
                        else
                            out_step = (step.getValue0()+1)+"->"+(step.getValue1()+1);
                        return out_step;
                    })
                .collect(Collectors.joining(", ", "{", "}"));

        if (withCosts)
            out +=" ==> Totat Cost "+getCost()+".\n";
        else
            out +=".\n";

        return out;
    }

    public String ReducedMatrixToString()
    {
        String str = "";
        for (int i = 0; i < reducedMatrix.length; i++) {
            str += ((str.length() > 0) ? "\n\t\t" : "") + "[";
            for (int j = 0; j < reducedMatrix[0].length; j++)
                str += String.format("%1$"+tsp.CMatrixPading+ "s",reducedMatrix[i][j]) + ((j < reducedMatrix[0].length - 1) ? " " : " ");
            str += "]";
        }
        return str+"\n";
    }

    @Override
    public String toString()
    {
        //return "["+getId()+":"+getLevel()+","+getCost()+","+getVertex()+"]";
        return  "___________________________________________________________________________________________\n"+
                "Node:  \t"+ getId()+"\n"+
               "Path:  \t" + pathToString(true)+
               "Matrix:\t" + ReducedMatrixToString()+
               "Cost:  \t" + getCost()+"\n"+
               "Vertex:\t"+ getVertex()+"\n"+
               "Level: \t" + getLevel()+"\n";
    }

}

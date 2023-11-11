import org.junit.jupiter.api.Test;

/* Este código esta basado en el algoritmo y el código de C++ descrito en la
   siguiente página web: https://www.geeksforgeeks.org/travelling-salesman-problem-tsp-using-reduced-matrix-method/
 */


public class TSP_Sec
{

    public static void main(String[] args)
    {
        TSP tsp;

        if (args.length>1)
            System.err.println("Error in Parameters. Usage: TSP_Sec [<Cities_File>]");
        if (args.length < 1)
            tsp = new TSP();
        else
            tsp = new TSP(args[0]);

        Node solution = tsp.Solve();
    }

}
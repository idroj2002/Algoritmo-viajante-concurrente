/* Este código esta basado en el algoritmo y el código de C++ descrito en la
   siguiente página web: https://www.geeksforgeeks.org/travelling-salesman-problem-tsp-using-reduced-matrix-method/
 */


public class TSP_Conc
{

    public static void main(String[] args)
    {
        TSP tsp;

        if (args.length>3)
            System.err.println("Error in Parameters. Usage: TSP_Conc [<Cities_File>] [<Num_Threads>] [<Concurrent_Method>]");
        if (args.length < 1)
            tsp = new TSP();
        else if (args.length < 2)
            tsp = new TSP(args[0]);
        else if (args.length < 3)
        {
            int threadsNum = Integer.parseInt(args[1]);
            tsp = new TSP(args[0], threadsNum);
        }
        else
        {
            int threadsNum = Integer.parseInt(args[1]);
            tsp = new TSP(args[0], threadsNum, args[2]);
        }

        Node solution = tsp.Solve();
    }

}
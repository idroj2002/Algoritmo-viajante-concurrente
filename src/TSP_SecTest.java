import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TSP_SecTest
{
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_RESET = "\u001B[0m";

    @Test
    public static void main(String[] args)
    {
        TSP tsp;
        Node solution;


        tsp = new TSP("./Ejemplos_Ciudades/tsp4");
        solution = tsp.Solve();
        try {
            assertEquals(solution.getCost(),8);
        }catch (AssertionError e){
            System.out.println(ANSI_RED+ e.getMessage() + " "+ ANSI_RESET);
        }

        tsp = new TSP("./Ejemplos_Ciudades/tsp10");
        solution = tsp.Solve();
        try {
            assertEquals(solution.getCost(),96);
        }catch (AssertionError e){
            System.out.println(ANSI_RED+ e.getMessage() + " "+ ANSI_RESET);
        }

        tsp = new TSP("./Ejemplos_Ciudades/tsp15");
        solution = tsp.Solve();
        try {
            assertEquals(solution.getCost(),140);
        }catch (AssertionError e){
            System.out.println(ANSI_RED+ e.getMessage() + " "+ ANSI_RESET);
        }

        tsp = new TSP("./Ejemplos_Ciudades/tsp20");
        solution = tsp.Solve();
        try {
            assertEquals(solution.getCost(),172);
        }catch (AssertionError e){
            System.out.println(ANSI_RED+ e.getMessage() + " "+ ANSI_RESET);
        }

        tsp = new TSP("./Ejemplos_Ciudades/tsp30");
        solution = tsp.Solve();
        try {
            assertEquals(solution.getCost(),353);
        }catch (AssertionError e){
            System.out.println(ANSI_RED+ e.getMessage() + " "+ ANSI_RESET);
        }

        tsp = new TSP("./Ejemplos_Ciudades/tsp40");
        solution = tsp.Solve();
        try {
            assertEquals(solution.getCost(),8);
        }catch (AssertionError e){
            System.out.println(ANSI_RED+ e.getMessage() + " "+ ANSI_RESET);
        }
    }

}
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TSP_ConTest
{
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_RESET = "\u001B[0m";

    @Test
    public static void main(String[] args)
    {
        TSP tsp;
        Node solution;
        String[] concurrentTypes = {"ForkJoinPool"};//{"FixedThreadPool", "CachedThreadPool", "ForkJoinPool"};

        for (String concurrentType:concurrentTypes){
            System.out.println("########################");
            System.out.println("Initializing tests of " + concurrentType);
            System.out.println("########################");
            tsp = new TSP("./Ejemplos_Ciudades/tsp4", 8, concurrentType);
            solution = tsp.solve();
            try {
                assertEquals(solution.getCost(),8);
            }catch (AssertionError e){
                System.out.println(ANSI_RED+ e.getMessage() + " "+ ANSI_RESET);
            }

            tsp = new TSP("./Ejemplos_Ciudades/tsp10", 8, concurrentType);
            solution = tsp.solve();
            try {
                assertEquals(solution.getCost(),96);
            }catch (AssertionError e){
                System.out.println(ANSI_RED+ e.getMessage() + " "+ ANSI_RESET);
            }

            tsp = new TSP("./Ejemplos_Ciudades/tsp15", 8, concurrentType);
            solution = tsp.solve();
            try {
                assertEquals(solution.getCost(),140);
            }catch (AssertionError e){
                System.out.println(ANSI_RED+ e.getMessage() + " "+ ANSI_RESET);
            }

            tsp = new TSP("./Ejemplos_Ciudades/tsp20", 8, concurrentType);
            solution = tsp.solve();
            try {
                assertEquals(solution.getCost(),172);
            }catch (AssertionError e){
                System.out.println(ANSI_RED+ e.getMessage() + " "+ ANSI_RESET);
            }

            tsp = new TSP("./Ejemplos_Ciudades/tsp30", 8, concurrentType);
            solution = tsp.solve();
            try {
                assertEquals(solution.getCost(),353);
            }catch (AssertionError e){
                System.out.println(ANSI_RED+ e.getMessage() + " "+ ANSI_RESET);
            }

            tsp = new TSP("./Ejemplos_Ciudades/tsp40", 8, concurrentType);
            solution = tsp.solve();
            try {
                assertEquals(solution.getCost(),297);
            }catch (AssertionError e){
                System.out.println(ANSI_RED+ e.getMessage() + " "+ ANSI_RESET);
            }
        }
    }

}
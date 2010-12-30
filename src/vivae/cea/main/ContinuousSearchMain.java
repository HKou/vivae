package vivae.cea.main;

import com.google.common.collect.ImmutableMap;
import java.util.concurrent.ExecutionException;
import org.uncommons.maths.random.SeedException;
import vivae.cea.ContinuousEvolutionSearch;
import vivae.cea.Solution;

/**
 *
 * @author Bc. Ramunas Belkauskas (ramunas.belkauskas@gmail.com)
 */
public class ContinuousSearchMain {

    private static ImmutableMap<Options, String> properties;
    private static final int ARG_COUNT = Options.values().length;

    public static void main(String[] args) throws SeedException, InterruptedException, ExecutionException {
        parseOptions(args);
        System.out.println("Options:");
        System.out.println(properties);
        ContinuousEvolutionSearch search = new ContinuousEvolutionSearch(properties);
        Solution solution = search.runSearch();
        System.out.println(solution.network);
    }

    public static void printUsage() {
        System.out.println(
                "Parameters needed:\n" +
                "filename sensors neurons populationSize" +
                "mutationRate crossoverRate maxAge iterationsLimit");
    }

    private static void parseOptions(String[] args) {
        if (args.length < ARG_COUNT) {
            printUsage();
            System.exit(1);
        } else {
            properties = new ImmutableMap.Builder<Options, String>()
                    .put(Options.FILENAME, args[0])
                    .put(Options.SENSORS, args[1])
                    .put(Options.NEURONS, args[2])
                    .put(Options.POPULATION_SIZE, args[3])
                    .put(Options.MUTATION_RATE, args[4])
                    .put(Options.CROSSOVER_RATE, args[5])
                    .put(Options.MAX_AGE, args[6])
                    .put(Options.ITERATIONS_LIMIT, args[7])
                    .build();
        }
    }

    /**
     * Enum for all command line parameters (options).
     */
    public static enum Options {
        /** svg file with scenario */
        FILENAME,
        /** sensors count */
        SENSORS,
        /** neurons count */
        NEURONS,
        /** max size of the population */
        POPULATION_SIZE,
        /** mutation probability */
        MUTATION_RATE,
        /** crossover probability */
        CROSSOVER_RATE,
        /** limit for age of the individual */
        MAX_AGE,
        /** limit for evolution steps (iterations) */
        ITERATIONS_LIMIT
    }
}

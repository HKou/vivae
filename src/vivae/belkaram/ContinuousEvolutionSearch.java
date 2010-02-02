package vivae.belkaram;

import java.util.Random;
import org.uncommons.maths.random.DefaultSeedGenerator;
import org.uncommons.maths.random.MersenneTwisterRNG;
import org.uncommons.maths.random.SeedException;

/**
 *
 * @author Bc. Ramunas Belkauskas
 */
public class ContinuousEvolutionSearch {

    /** Source of randomness suited for randomized simulations. */
    private Random rand;

    private String scenarioFilename;



    public ContinuousEvolutionSearch() throws SeedException {
        rand = new MersenneTwisterRNG(DefaultSeedGenerator.getInstance());

    }
}

package vivae.cea;

import com.google.common.collect.ImmutableMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import nn.FRNN;
import org.uncommons.maths.random.SeedException;
import vivae.cea.main.ContinuousSearchMain.Options;
import vivae.example.FRNNExperiment;
import vivae.fitness.AverageSpeed;
import vivae.fitness.FitnessFunction;
import vivae.util.Util;
import static vivae.cea.MathUtil.clip;

/**
 * From the paper CONTINUAL EVOLUTION ALGORITHM FOR BUILDING OF ANN-BASED MODELS,
 * authors: Zdenek Buk, Miroslav Snorek.
 * 
 *      Algorithm1 (The General CE Algorithm):
 *      1.Initialization
 *      2.Repeat until stop condition
 *          2.1.Evaluate all individuals
 *          2.2.Reproduction of the individuals with respect to the RP(¯xi) value
 *          2.3.Elimination of the individuals with respect to the DP(¯xi) value
 *          2.4.Adaptation of the parametrical vector of each individual
 *          2.5.Update all working parameters and age parameter of individuals
 *      3.The result processing
 *
 * @author Bc. Ramunas Belkauskas
 */
public class ContinuousEvolutionSearch {

    private String scenarioFilename;
    private final ImmutableMap<Options, String> properties;

    public ContinuousEvolutionSearch(ImmutableMap<Options, String> properties) throws SeedException {
        this.properties = properties;
        scenarioFilename = properties.get(Options.FILENAME);
    }

    /**
     *
     * @return
     */
    public Solution runSearch() throws InterruptedException, ExecutionException {
        FRNNExperiment experiment = new FRNNExperiment();
        experiment.createArena(scenarioFilename, false);
        Individual bestOverall = null;

        //1.Init
        final int maxPopulationSize = Integer.parseInt(properties.get(Options.POPULATION_SIZE));
        final int maxIndividualAge = Integer.parseInt(properties.get(Options.MAX_AGE));
        final int neurons = Integer.parseInt(properties.get(Options.NEURONS));
        final int sensors = Integer.parseInt(properties.get(Options.SENSORS));
        final double mutationRate = Double.parseDouble(properties.get(Options.MUTATION_RATE));
        final double crossoverRate = Double.parseDouble(properties.get(Options.CROSSOVER_RATE));

        final SelectionStrategy selectionStrategy = new RouleteSelectionStrategy();
        final ReproductionStrategy reproductionStrategy =
                new ReproductionStrategyImpl(mutationRate, crossoverRate);

        //FIXME: tento pristup pres funkce jako objekty neni moc prehledny pri volani, vymyslet nejakou alternativu.
        /**
         * Raw death probability function
         * Function of fitness amd age ratio.
         */
        final Function2Params<Double, Double, Double> rawDPFunction =
                new Function2Params<Double, Double, Double>() {

                    @Override
                    public Double apply(Double arg1, Double arg2) {
                        double fitness = clip(arg1, 0.0, 1.0); //must be between 0 and 1
                        double age = clip(arg2, 0.0, 1.0);//relative to maxAge
                        //magicke konstanty asi nejsou to prave orechove..ale jde tu o ad hoc tuneni..
                        double value = Math.pow(age, 3.) * (1. + 0.5 * (1 - fitness));
                        if (value > 1) {
                            return 1.;
                        }
                        return value;
                    }
                };
        /**
         * Raw reproduction probability.
         */
        final Function2Params<Double, Double, Double> rawRPFunction =
                new Function2Params<Double, Double, Double>() {

                    @Override
                    public Double apply(Double arg1, Double arg2) {
                        double fitness = clip(arg1, 0.0, 1.0);
                        double age = clip(arg2, 0.0, 1.0);
                        //fce znevyhodnuje starsi jedince .. nejstarsi maji cca 2x mensi
                        //pravdepodobnost nez nejmladsi pri stejne fitness.
                        return (2. - age) * 0.5 * fitness;
                    }
                };

        final double _balanceTresh = 0.5;
        final double _balanceTreshInv = 1.0 / _balanceTresh;

        /**Parametry: rawDeathProbability, populationRatio*/
        final Function2Params<Double, Double, Double> balanceDPFunction =
                new Function2Params<Double, Double, Double>() {

                    /**
                     * Balance death probability to population size.
                     * Bigger population -> bigger chance of death.
                     */
                    @Override
                    public Double apply(final Double arg1, final Double arg2) {
                        double deathProb = clip(arg1, 0.0, 1.0);
                        double ratio = clip(arg2, 0.0, 1.0);
                        if (ratio < _balanceTresh) {
                            return deathProb * ratio * _balanceTreshInv;
                        }
                        return deathProb;
                    }
                };
        /**Parametry: rawReproductionProbability, populationRatio*/
        final Function2Params<Double, Double, Double> balanceRPFunction =
                new Function2Params<Double, Double, Double>() {

                    /**
                     * Balance reproduction probability to population size.
                     * Bigger population -> smaller chance of reproduction (low resources).
                     */
                    @Override
                    public Double apply(final Double arg1, final Double arg2) {
                        double reprProb = clip(arg1, 0.0, 1.0);
                        double ratio = clip(arg2, 0.0, 1.0);
                        if (ratio > _balanceTresh) {
                            return reprProb * (1 - ratio) * _balanceTreshInv;
                        }
                        return reprProb;
                    }
                };

        ArrayList<Individual> population = createRandomPopulation(maxPopulationSize, neurons, sensors);

        int iteration = 0;
        final int iterationLimit = Integer.parseInt(properties.get(Options.ITERATIONS_LIMIT));
        final int dT = 1;

        ExecutorService pool = Executors.newFixedThreadPool(
                Runtime.getRuntime().availableProcessors());

        final IndividualEvaluator evaluator = new IndividualEvaluator(experiment, scenarioFilename);

        //2.Repeat until stop condition
        while (iteration++ < iterationLimit) {
            //2.1.Evaluate all individuals
            double popSizeRatio = (double) population.size() / maxPopulationSize;
//TODO: Udelat vicevlaknove reseni, problem je zatim v FRNNExperiment, v tomhle kodu mam 1 instanci a pritom potrebuju tolik, kolik mam vlaken.
//            Collection<Future> futures = new ArrayList<Future>(population.size());
            for (Individual individual : population) { //for each in population ....
//                class FitRunnable implements Runnable {
//
//                    Individual individual;
//                    double popSizeRatio;
//
//                    public FitRunnable(Individual individual, double popSizeRatio) {
//                        this.individual = individual;
//                        this.popSizeRatio = popSizeRatio;
//                    }
//
//                    @Override
//                    public void run() {
//                        //fitness
//                        double fitness = evaluator.evaluate(this.individual);
//                        double ageRatio = (double) individual.age / maxIndividualAge;
//                        double rawDP = rawDPFunction.apply(fitness, ageRatio);
//                        double rawRP = rawRPFunction.apply(fitness, ageRatio);
//                        //balanced DP & RP:
//                        double balDeathProb = balanceDPFunction.apply(rawDP, popSizeRatio);
//                        double balReprProb = balanceRPFunction.apply(rawRP, popSizeRatio);
//
//                        individual.fitness = fitness;
//                        individual.deathProb = balDeathProb;
//                        individual.reproProb = balReprProb;
//                    }
//                }
//                Runnable fitRunnable = new FitRunnable(individual, popSizeRatio);
//                Future futureResult = pool.submit(fitRunnable);
//                futures.add(futureResult);
//TODO: nejprve spocitat fitness, zjistit max a min fitness a podle toho preskalovat na rozsah 0..1, 0 pro nejhorsi, 1 pro nejlepsiho.
                //fitness:
                double fitness = evaluator.evaluate(individual);
                double ageRatio = (double) individual.age / maxIndividualAge;
                double rawDP = rawDPFunction.apply(fitness, ageRatio);
                double rawRP = rawRPFunction.apply(fitness, ageRatio);
                //balanced DP & RP:
                double balDeathProb = balanceDPFunction.apply(rawDP, popSizeRatio);
                double balReprProb = balanceRPFunction.apply(rawRP, popSizeRatio);

                individual.fitness = fitness;
                individual.deathProb = balDeathProb;
                individual.reproProb = balReprProb;
            }
            //wait for fitness eval to finish.
//            for (Future f : futures) {
//                f.get();
//            }

            //2.2.Reproduction of the individuals with respect to the RP(¯xi) value
            ArrayList<Individual> newIndividuals = new ArrayList<Individual>();
            for (Individual individual : population) {
                double p = Util.rand.nextDouble();
                if (p < individual.reproProb) {
                    //select other mate:
                    Individual other = selectionStrategy.select(population);
                    //make new individual:
                    Individual newIndividual = reproductionStrategy.reproduce(individual, other);
                    newIndividuals.add(newIndividual);
                }
            }
            //add new individuals after iteration is done:
            population.addAll(newIndividuals);

            //2.3.Mark for elimination - new individuals have DP = 0, so they will survive:
            for (Individual individual : population) {
                if (Util.rand.nextDouble() < individual.deathProb) {
                    individual.selectedForElimination = true;
                }
            }

            //TODO: 2.4 Time dependent evolution ...

            //2.5.Update all working parameters and age parameter of individuals
            population = copyLivePopulation(population);
            for (Individual individual : population) {
                individual.age += dT;
            }


            //some print outs ...
            System.out.println("Iteration: " + iteration);
            Collections.sort(population, Individual.byFitnessDescComparator);
            System.out.println("Top 3:");
            if (population.size() >= 3) {
                System.out.println(population.subList(0, 3).toString());
            } else {
                System.out.println(population.toString());
            }
            if (bestOverall == null || bestOverall.fitness < population.get(0).fitness) {
                bestOverall = population.get(0);
            }
        }
        System.out.println("Best overall:");
        System.out.println(bestOverall);
        return getSolution(population);

    }

    /**
     * Utility method which copies only live individuals.
     * @param population
     * @return Shallow copy of population containing only live individuals.
     */
    private static ArrayList<Individual> copyLivePopulation(final Collection<Individual> population) {
        ArrayList<Individual> newPopulation = new ArrayList<Individual>(population.size());
        for (Individual individual : population) {
            if (!individual.selectedForElimination) {
                newPopulation.add(individual);
            }
        }
        return newPopulation;
    }

    /**
     * Generates random population of given size.
     * 
     * @param populationSize
     * @return
     */
    private static ArrayList<Individual> createRandomPopulation(final int populationSize,
            final int neurons, final int sensors) {
        ArrayList<Individual> population = new ArrayList<Individual>(populationSize);

        for (int i = 0; i < populationSize; i++) {
            Individual individual = Individual.createRandomIndividual(neurons, sensors);
            population.add(individual);
        }

        return population;
    }

    private static Solution getSolution(ArrayList<Individual> population) {
        Collections.sort(population, Individual.byFitnessDescComparator);
        return new Solution(population.get(0).getNetwork());
    }
}

/**
 * Evaluator class runs experiment and evaluates fitness of individuals.
 * 
 * @author Bc. Ramunas Belkauskas (ramunas.belkauskas@gmail.com)
 */
class IndividualEvaluator {
    //TODO: experiment by mel uz v sobe zahrnovat arenu, misto aby se vytvarela znovu ze souboru.

    private FRNNExperiment experiment;
    private String filename;
    private final boolean visible = false;

    public IndividualEvaluator(final FRNNExperiment experiment, final String filename) {
        this.experiment = experiment;
        this.filename = filename;
    }

    /**
     * @param individual
     * @return Returns fitness value of given individual.
     */
    public double evaluate(final Individual individual) {
        experiment.createArena(filename, visible);
        FRNN frnn = individual.getNetwork();
        //parametry cidel k robotum ...
        //TODO: nacist z command lajny
        double maxDistance = 50, frictionDistance = 25;
        experiment.setupExperiment(new FRNN[]{frnn}, maxDistance, frictionDistance);
        FitnessFunction avg = new AverageSpeed(experiment.getArena());
        experiment.startExperiment();
        double fitness = avg.getFitness();
        return fitness;
    }
}
//TODO: Nahradit populaci jako ArrayList za vlastni tridu..Bude to lepsi do budoucna.
//    class Population {
//        public ArrayList individuals;
//    }


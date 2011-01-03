package vivae.cea;

import java.util.Arrays;
import vivae.util.Util;

/**
 * Simple implementation of reproduction strategy, uses 1-point crossover for structural array
 * and for parametric array.
 * Mutation is implementet by eather flipping boolean value in structural array
 * or by adding or subtracting (random decision) parametric array value by <code>mutationRate</code>.
 * 
 * @author Bc. Ramunas Belkauskas (ramunas.belkauskas@gmail.com)
 */
class ReproductionStrategyImpl implements ReproductionStrategy {

    public final double mutationProb;
    public final double crossoverProb;
    public final double mutationRate = 0.1;

    public ReproductionStrategyImpl(final double mutationRate, final double crossoverRate) {
        this.mutationProb = mutationRate;
        this.crossoverProb = crossoverRate;
    }

    @Override
    public Individual reproduce(Individual parent1, Individual parent2) {
        assert (parent1.enabledConnections.length == parent2.enabledConnections.length);
        assert (parent1.originalWeights.length == parent2.originalWeights.length);
        
        double[] param;
        boolean[] enaCon;

        if (Util.rand.nextDouble() < crossoverProb) {
            //crossover points to both arrays
            double p = Util.rand.nextDouble();
            param = new double[parent1.originalWeights.length];
            enaCon = new boolean[parent1.enabledConnections.length];
            //TODO: Overit, jestli si odpovidaji prvky pole vah a struktury, pripadne jestli vadi pokud ne..
            int pointParam = (int) (p * param.length);
            int pointStruct = (int) (p * enaCon.length);
            System.arraycopy(parent1.originalWeights, 0, param, 0, pointParam);
            System.arraycopy(parent2.originalWeights, pointParam, param, pointParam, param.length - pointParam);
            System.arraycopy(parent1.enabledConnections, 0, enaCon, 0, pointStruct);
            System.arraycopy(parent2.enabledConnections, pointStruct, enaCon, pointStruct, enaCon.length - pointStruct);
        } else {
            //just copy the one with better fitness
            Individual toCopy = (parent1.fitness > parent2.fitness) ? parent1 : parent2;
            param = Arrays.copyOf(toCopy.originalWeights, toCopy.originalWeights.length);
            enaCon = Arrays.copyOf(toCopy.enabledConnections, toCopy.enabledConnections.length);
        }
        int neurons = parent1.getNeuronsCount();
        int sensors = parent1.getSensorsCount();
        mutate(param, enaCon);
        Individual offspring = new Individual(neurons, sensors, param, enaCon);
        return offspring;
    }

    /**
     * Mutates arrays according to mutation probability.
     *
     * @param weights Array with weights - parametric array.
     * @param enabledConnections Structural array.
     */
    private void mutate(double[] weights, boolean[] enabledConnections) {
        for (int i = 0; i < enabledConnections.length; i++) {
            if (Util.rand.nextDouble() < this.mutationProb) {
                boolean b = enabledConnections[i];
                enabledConnections[i] = !b; //inverts the original value
            }
        }
        for (int i = 0; i < weights.length; i++) {
            if (Util.rand.nextDouble() < this.mutationProb) {
                double d = weights[i];
                weights[i] = Util.rand.nextBoolean() ? d + mutationRate : d - mutationRate;
            }
        }
    }
}

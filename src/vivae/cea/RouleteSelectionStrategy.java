package vivae.cea;

import java.util.ArrayList;
import vivae.util.Util;

/**
 *
 * @author Bc. Ramunas Belkauskas (ramunas.belkauskas@gmail.com)
 */
class RouleteSelectionStrategy implements SelectionStrategy {

    @Override
    public Individual select(ArrayList<Individual> population) {
//        double fitnessSum = fitnessSum(population);
        double fitnessSum = reproProbSum(population);
        double rouletePoint = fitnessSum * Util.rand.nextDouble();
        for (Individual individual : population) {
//            rouletePoint -= individual.fitness;
            rouletePoint -= individual.reproProb;
            if (rouletePoint <= 0) {
                return individual;
            }
        }
        //should not happen...
        return null;
    }

    private static double fitnessSum(ArrayList<Individual> population) {
        double fitnessSum = 0;
        for (Individual individual : population) {
            fitnessSum += individual.fitness;
        }
        assert (fitnessSum > 0);
        return fitnessSum;
    }
    
    private static double reproProbSum(ArrayList<Individual> population) {
        double probSum = 0;
        for (Individual individual : population) {
            probSum += individual.reproProb;
        }
        assert (probSum > 0);
        return probSum;
    }
}

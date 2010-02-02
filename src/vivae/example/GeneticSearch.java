/**
 * This is VIVAE (Visual Vector Agent Environment)
 * a library allowing for simulations of agents in co-evolution
 * written as a bachelor project
 * by Petr Smejkal
 * at Czech Technical University in Prague
 * in 2008
 */
package vivae.example;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import vivae.fitness.AverageSpeed;
import vivae.fitness.CombinedFitness;
import vivae.fitness.Damage;
import vivae.fitness.FitnessFunction;
import vivae.fitness.MovablesOnTop;
import vivae.util.Util;

public class GeneticSearch {

    private final String scenario;
    private final double mutationRate;
    private final double crossoverRate;
    private final int generationCount;
    private final int populationSize;

    public GeneticSearch(
            String scenario,
            double mutationRate,
            double crossoverRate,
            int generationCount,
            int populationSize) {
        this.scenario = scenario;
        this.mutationRate = mutationRate;
        this.crossoverRate = crossoverRate;
        this.generationCount = generationCount;
        this.populationSize = populationSize;
    }

    public static void main(String[] args) {
        final String scenario = "data/scenarios/arena2.svg";
        final double mutationRate = 0.01;
        final double crossoverRate = 0.7;
        final int generationCount = 25;
        final int populationSize = 10;

        final GeneticSearch exp = new GeneticSearch(scenario, mutationRate, crossoverRate, generationCount, populationSize);
        final int sensors = 5;
        final int neurons = 5;

        final double[][] wm = exp.runExperiment(neurons, sensors, 15, -15);
        try {
            System.in.read();
        } catch (IOException e) {
            e.printStackTrace();
        }
        exp.playExperiment(scenario, wm);
    }

    private FitnessFunction experiment(double[][] wm, boolean visible) {
        final FRNNExperiment exp = new FRNNExperiment();
        exp.createArena(scenario, visible);
        final double[][][] wm2 = new double[][][]{wm};
        exp.setupExperiment(wm2, 50, 25);

        FitnessFunction mot = new MovablesOnTop(exp.arena);// initialize fitness
        FitnessFunction avg = new AverageSpeed(exp.arena);
        FitnessFunction dmg = new Damage(exp.arena);
        FitnessFunction comb = new CombinedFitness(new double[]{0.3, 0.7}, new FitnessFunction[]{dmg, avg});
        exp.startExperiment();
        System.out.println("average speed fitness = " + avg.getFitness());
        System.out.println("average ontop fitness = " + mot.getFitness());
        System.out.println("average damage fitness = " + dmg.getFitness());
        System.out.println("combined fitness = " + comb.getFitness());

        return comb;
    }

    private void playExperiment(String scenario, double[][] wm) {
        experiment(wm, true);
    }

    private double[][] runExperiment(final int neurons, final int sensors,
            final double max, final double min) {

        final List<WeightedNetwork> population = new ArrayList<WeightedNetwork>();
        randomPopulation(neurons, sensors, max, min, population);

        WeightedNetwork result = null;
        double bestFitness = 0;
        for (int i = 0; i < generationCount; i++) {
            System.out.println("new generation");
            double totalFitness = 0;

            result = null;
            bestFitness = 0;

            int index = 0;
            for (WeightedNetwork net : population) {
                System.out.println("test " + i + "/" + index);

                System.out.println(net.getGenotype());
                final FitnessFunction fitness = experiment(net.getNet(), false);
                net.setFitness(fitness.getFitness());
                totalFitness += net.getFitness();
                index++;

                if (fitness.getFitness() > bestFitness) {
                    bestFitness = fitness.getFitness();
                    result = net;
                }
            }

            final List<WeightedNetwork> temp = new ArrayList<WeightedNetwork>();

            temp.add(result);

            for (int j = 0; j < populationSize / 2; j++) {
                WeightedNetwork offspring1 = roulette(totalFitness, population);
                WeightedNetwork offspring2 = roulette(totalFitness, population);

                crossover(offspring1, offspring2);

                mutate(offspring1, max, min);
                mutate(offspring2, max, min);

                temp.add(offspring1);
                temp.add(offspring2);

            }
            population.clear();
            population.addAll(temp);

        }

        return result.getNet();
    }

    private void randomPopulation(final int neurons, final int sensors,
            final double max, final double min,
            final List<WeightedNetwork> population) {

        for (int i = 0; i < populationSize; i++) {
            population.add(new WeightedNetwork(0, Util.randomArray2D(neurons, 2
                    * sensors + neurons + 1, min, max), 0, ""));
        }

    }

    private void mutate(WeightedNetwork net, double max, double min) {
        for (int i = 0; i < net.getNet().length; i++) {
            double[] item = net.getNet()[i];
            for (int j = 0; j < item.length; j++) {
                if (Math.random() < mutationRate) {
                    item[j] = Math.random() * (max - min) + min;
                }
            }
        }

    }

    private void crossover(WeightedNetwork net1, WeightedNetwork net2) {
        if (Math.random() < crossoverRate) {
            net1.addParent(net2.getParent());
            net2.addParent(net1.getParent());
            double[][] d1 = net1.getNet();
            double[][] d2 = net2.getNet();

            for (int i = 0; i < d1.length; i++) {

                for (int j = 0; j < d1[i].length; j++) {
                    double crossover = Math.random();
                    double temp = d1[i][j];
                    d1[i][j] = crossover * temp + (1 - crossover) * d2[i][j];
                    d2[i][j] = crossover * d2[i][j] + (1 - crossover) * temp;

                }
            }
        }

    }

    private WeightedNetwork roulette(double totalFitness,
            List<WeightedNetwork> nets) {
        double slice = Math.random() * totalFitness;
        double fitnessSoFar = 0;

        int i = 0;
        for (WeightedNetwork net : nets) {
            fitnessSoFar += net.getFitness();

            if (fitnessSoFar >= slice) {
                return new WeightedNetwork(0, copy(net.getNet()), i, net.getGenotype());
            }
            i++;
        }

        return null;
    }

    private static double[][] copy(double[][] source) {
        final double[][] dest = new double[source.length][];

        for (int j = 0; j < source.length; j++) {
            dest[j] = source[j].clone();
        }

        return dest;
    }

    private static class WeightedNetwork implements Comparable<WeightedNetwork> {

        private double fitness;
        private double[][] net;
        private String genotype = "";
        private int parent;

        public WeightedNetwork(double fitness, double[][] net, int parent,
                String genotype) {
            super();
            this.fitness = fitness;
            this.net = net;
            this.genotype += genotype + "," + parent;
            this.parent = parent;
        }

        public double[][] getNet() {
            return net;
        }

        public double getFitness() {
            return fitness;
        }

        public void setFitness(double fitness) {
            this.fitness = fitness;
        }

        public String getGenotype() {
            return genotype;
        }

        public void addParent(int parent) {
            genotype += "-" + parent;
        }

        public int getParent() {
            return parent;
        }

        @Override
        public int compareTo(WeightedNetwork other) {
            return Double.compare(other.fitness, fitness);
        }
    }
}

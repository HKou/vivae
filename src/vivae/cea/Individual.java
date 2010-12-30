
package vivae.cea;

import java.util.Arrays;
import java.util.Comparator;
import nn.FRNN;
import vivae.util.Util;


/**
 * Individual representing one solution.
 *
 * @author Bc. Ramunas Belkauskas (ramunas.belkauskas@gmail.com)
 */
class Individual {

    protected static final double MAX_WEIGTH = 1.;
    protected static final double MIN_WEIGTH = -1.;
    /** count of neurons */
    private int neurons;
    /** count of sensors */
    private int sensors;
    /**
     * Age of individual - a.
     */
    int age = 0;
    /**
     * Matice vah, prevedena po radcich do 1D pole.
     * Behavioral vector - b.
     */
    double[] weights = null;
    /**
     * Original value of weights matrix when the Individual began its evolution.
     * Instinct (parametric vector) - p.
     */
    double[] originalWeights = null;
    /**
     * Matrix which enables and disables particular edge between neurons.
     * Structural vector - s.
     */
    boolean[] enabledConnections = null;

    /**
     * Convenience factory method which takes 2d input arrays and creates individual,
     * which internally uses 1D arrays.
     *
     * @param neurons Number of neurons
     * @param sensors Number of sensors
     * @param param 2d array describing eaights and thresholds in net.
     *              Must be of size <code>neurons x (neurons + sensors + 1)</code>
     * @param structure 2d array describing which connections in neural net are active.
     *              Must be of size <code>neurons x (neurons + sensors)</code>
     * @return
     */
    public static Individual createIndividual(final int neurons, final int sensors,
            final double[][] param, final boolean[][] structure) {
        double[] paramFlat = Util.flatten(param);
        boolean[] structFlat = Util.flatten(structure);
        Individual instance = new Individual(neurons, sensors, paramFlat, structFlat);
        return instance;
    }
    double fitness;
    double deathProb;
    double reproProb;
    boolean selectedForElimination;

    /**
     * Ctor for the individual.
     *
     * @param neurons Numbers of neurons in the neural net for this individual.
     * @param sensors Numbers of sensors in the neural net for this individual.
     * @param param Parametric vector of individual - initial weights.
     * @param structure Structural vector - enabled and disabled edges in graph.
     */
    public Individual(final int neurons, final int sensors,
            final double[] param, final boolean[] structure) {
        int requiredLength = getRequired1DLength(neurons, sensors);
        if (requiredLength != param.length) {
            throw new IllegalArgumentException(
                    String.format(
                    "Illegal parametric array length (%d). Must be of neurons*(neurons+sensors+1) = %d%n",
                    param.length, requiredLength));
        }
        int structReqLen = neurons * (neurons + sensors);
        if (structReqLen != structure.length) {
            throw new IllegalArgumentException(
                    "Illegel structure array length. Must be of neurons*(neurons+sensors) = "
                    + structReqLen);
        }
        this.neurons = neurons;
        this.sensors = sensors;
        originalWeights = Arrays.copyOf(param, param.length);
        weights = Arrays.copyOf(param, param.length);
        enabledConnections = Arrays.copyOf(structure, structure.length);
    }

    /**
     * Transforms individual information to FRNN.
     * @return
     */
    public FRNN getNetwork() {
        final FRNN frnn = new FRNN();
        //matice vstupnich vah
        final double[][] wInput = new double[neurons][sensors];
        //matice vah rekurentnich vazeb
        final double[][] wRecurrent = new double[neurons][neurons];
        //matice prahu kazdeho neuronu
        final double[] wThresh = new double[neurons];

        //pomocna promenna - delka radku 2d matice vsech parametru (prahy, vahy)
        final int rowLen = getRequired1DLength(neurons, sensors) / neurons;

        for (int i = 0; i < neurons; i++) {
            //the beginning of the row
            int begin = i * rowLen;
            //copy thu subarrays
            wInput[i] = Arrays.copyOfRange(weights, begin, begin + sensors);
            wRecurrent[i] = Arrays.copyOfRange(weights, begin + sensors, begin + sensors + neurons);
            //disable connections according to the structure array
            for (int sensor = 0; sensor < sensors; sensor++) {
                if (!isSensorConnected(i, sensor)) {
                    wInput[i][sensor] = 0.0;
                }
            }
            for (int neuron = 0; neuron < neurons; neuron++) {
                if (!isNeuronsConnected(i, neuron)) {
                    wRecurrent[i][neuron] = 0.0;
                }
            }
            //copy the value
            wThresh[i] = weights[begin + sensors + neurons];
        }

        frnn.init(wInput, wRecurrent, wThresh);
        return frnn;
    }

    /**
     * Returns whether given input sensor is connected to given neuron.
     *
     * @param neuron Index of neuron, starts with 0.
     * @param sensor Index of sensor, starts with 0.
     * @return True if given sensor is connected to the neuron.
     */
    public boolean isSensorConnected(int neuron, int sensor) {
        //neuron - index do radku, sensor = index do sloupce
        //pripojeni neuronu na sensory je v prvni casti pomyslne 2d matice
        return enabledConnections[neuron * (sensors + neurons) + sensor];
    }

    /**
     * Returns whether are two given neurons connected.
     *
     * @param fromNeuron Start of the connection to test.
     * @param toNeuron End of connection to test.
     * @return True if there is a connection from the first neuron to the second.
     */
    public boolean isNeuronsConnected(int fromNeuron, int toNeuron) {
        //fromNeuron - index do radku, toNeuron - index do sloupce
        //cast matice pro spojeni mezi neurony zacina az za casti pro sensory (offset = sensors)
        return enabledConnections[fromNeuron * (sensors + neurons) + toNeuron + sensors];
    }

    /**
     * Returns required size of 1D array for weigths and tresholds,
     * given the number of neurons and inputs (sensors).
     *
     * @param neurons
     * @param sensors
     * @return
     */
    public static int getRequired1DLength(final int neurons, final int sensors) {
        if (neurons <= 0 || sensors <= 0) {
            return 0;
        }
        return neurons * (sensors + neurons + 1);
    }

    public static int getRequiredStructure1DLength(final int neurons, final int sensors) {
        if (neurons <= 0 || sensors <= 0) {
            return 0;
        }
        return neurons * (sensors + neurons);
    }

    public static Individual createRandomIndividual(final int neurons, final int sensors) {
        double[] parametric =
                Util.randomArray1D(Individual.getRequired1DLength(neurons, sensors), MIN_WEIGTH, MAX_WEIGTH);
        boolean[] structure =
                Util.randomBoolArray1D(Individual.getRequiredStructure1DLength(neurons, sensors));
        Individual individual = new Individual(neurons, sensors, parametric, structure);
        return individual;
    }

    public int getNeuronsCount() {
        return neurons;
    }

    public int getSensorsCount() {
        return sensors;
    }
    public static final Comparator<Individual> byFitnessDescComparator = new Comparator<Individual>() {

        @Override
        public int compare(Individual o1, Individual o2) {
            //descending sort..
            return Double.compare(o2.fitness, o1.fitness);
        }
    };

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(100);
        sb.append("[").append("Individual@").append(hashCode()).append(" Fitness = ").append(fitness)
                .append("]");
        return sb.toString();
    }


}


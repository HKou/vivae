package vivae.fitness;


public class CombinedFitness extends FitnessFunction {
	private final double[] weights;
	private final FitnessFunction[] functions;

	public CombinedFitness(double[] weights, FitnessFunction[] functions) {
		super();

		if (weights.length != functions.length) {
			throw new IllegalArgumentException(
					"Dimension of weights and functions must be the same.");
		}

		this.weights = weights;
		this.functions = functions;
	}

	@Override
	public double getFitness() {
		double fitness = 0.0;
		
		for (int i = 0; i < weights.length; i++) {
			fitness += weights[i] * functions[i].getFitness();
		}
		
		return fitness;
	}

}

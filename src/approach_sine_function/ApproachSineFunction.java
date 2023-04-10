package approach_sine_function;

import java.io.PrintWriter;
import java.util.Date;

import machine_learning.ActivationFunction;
import machine_learning.DoubleMatrix;
import machine_learning.NeuralNetwork;

public class ApproachSineFunction {

	public static void train() throws Exception {
		PrintWriter writer = new PrintWriter("/Users/Joseph/Desktop/" + new Date() + ".csv", "UTF-8");
		System.out.println("create file: \"/Users/Joseph/Desktop/" + new Date() + ".csv\"");

		int number_of_test_points = 100;

		writer.print("generation,");
		for (int testPoint = 0; testPoint < number_of_test_points; testPoint++) {
			double x = 2 * Math.PI * testPoint / number_of_test_points;

			writer.print(x + ",");
		}
		writer.print("fitness\n");

		NeuralNetwork model = new NeuralNetwork(new int[] { 1, 16, 16, 16, 1 },
				new ActivationFunction[] { ActivationFunction.SIGMOID, ActivationFunction.SIGMOID,
						ActivationFunction.SIGMOID, ActivationFunction.LINEAR });

		model.mutate(1);

		double maxFitness = 0;

		for (int generation = 0; generation <= 200; generation++) {

			writer.print(generation + ",");
			double sum_of_error_square = 0;
			for (int testPoint = 0; testPoint < number_of_test_points; testPoint++) {
				double x = 2 * Math.PI * testPoint / number_of_test_points;

				double predicted_value = model.getOutput(new DoubleMatrix(new double[][] { { x } }).transpose()).get(0,
						0);
				writer.print(predicted_value + ",");

				sum_of_error_square += Math.pow(predicted_value - Math.sin(x), 2);
			}
			double fitness = number_of_test_points / sum_of_error_square;
			writer.print(fitness + "\n");
			
			System.out.println("generation: " + generation + ", fitness: " + fitness);

			NeuralNetwork selectedVariant = model;

			for (int variant_index = 0; variant_index < 1000; variant_index++) {
				NeuralNetwork variant = model.clone();
				variant.mutate(0.01);

				sum_of_error_square = 0;
				for (int testPoint = 0; testPoint < number_of_test_points; testPoint++) {
					double x = 2 * Math.PI * testPoint / number_of_test_points;

					double predicted_value = variant.getOutput(new DoubleMatrix(new double[][] { { x } }).transpose())
							.get(0, 0);

					sum_of_error_square += Math.pow(predicted_value - Math.sin(x), 2);
				}

				fitness = number_of_test_points / sum_of_error_square;
				
				if (fitness > maxFitness) {
					maxFitness = fitness;
					selectedVariant = variant;
				}
			}

			model = selectedVariant;
		}

		writer.close();
	}

}

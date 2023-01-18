package approach_sine_function;

import org.json.JSONException;
import org.json.JSONObject;

import machine_learning.ActivationFunction;
import machine_learning.FloatMatrix;
import machine_learning.NeuralNetwork;

public class ApproachSineFunction {

	public static void train() throws Exception {
		NeuralNetwork model = new NeuralNetwork(new int[] { 1, 32, 32, 1 }, new ActivationFunction[] {
				ActivationFunction.SIGMOID, ActivationFunction.SIGMOID, ActivationFunction.LINEAR });

		model.mutate(1);

		System.out.println(model);

		float minSsd = 1000;

		for (int generation = 0; generation < 2000 && minSsd >= 0.01; generation++) {

			NeuralNetwork selectedVariant = model;

			for (int variant_index = 0; variant_index < 500; variant_index++) {
				NeuralNetwork variant = model.clone();
				variant.mutate(0.005f);

				float ssd = 0;
				for (int testPoint = 0; testPoint < 100; testPoint++) {
					float x = (float) (2 * Math.PI * testPoint / 100);

					ssd += Math.pow(variant.getOutput(new FloatMatrix(new float[][] { { x } }).transpose()).get(0, 0)
							- Math.sin(x), 2);
				}

				if (ssd < minSsd) {
					minSsd = ssd;
					selectedVariant = variant;
				}
			}

			model = selectedVariant;

			System.out.println("minSsd=" + minSsd);

		}

		System.out.println(model);
	}

	public static void test(String str) throws JSONException, Exception {
		NeuralNetwork model = new NeuralNetwork(new JSONObject(str));

		for (int testPoint = 0; testPoint < 100; testPoint++) {
			float x = (float) (2 * Math.PI * testPoint / 100);
			System.out.println(model.getOutput(new FloatMatrix(new float[][] { { x } }).transpose()).get(0, 0));
		}
	}

}

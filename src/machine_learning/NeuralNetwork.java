package machine_learning;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONString;

public class NeuralNetwork implements JSONString {

	private int[] LayerWidths;
	private ActivationFunction[] activationFunctions;
	private DoubleMatrix[] layerIntervals;
	private int numberOfParameters;

	/*
	 * LayerWidths[0] is the width of the input layer.
	 * layerWidths[layerWidths.lenght - 1] is the width of the output layer.
	 * 
	 * activationFunctions.length should be equal to layerWidths.length - 1.
	 */
	public NeuralNetwork(int[] layerWidths, ActivationFunction[] activationFunctions) throws Exception {
		if (layerWidths.length < 2)
			throw new Exception();
		this.LayerWidths = new int[LayerWidths.length];

		for (int i = 0; i < LayerWidths.length; i++) {
			if (LayerWidths[i] <= 0)
				throw new Exception();
			this.LayerWidths[i] = LayerWidths[i];
		}

		if (activationFunctions.length != LayerWidths.length - 1)
			throw new Exception();
		this.activationFunctions = new ActivationFunction[activationFunctions.length];
		for (int i = 0; i < activationFunctions.length; i++) {
			this.activationFunctions[i] = activationFunctions[i];
		}

		layerIntervals = new DoubleMatrix[LayerWidths.length - 1];
		for (int i = 0; i < layerIntervals.length; i++) {
			/*
			 * the first 1 to LayerWidths[i] columns are for weight parameters, and the last
			 * column, LayerWidths[i] + 1, is for bias parameters.
			 */
			layerIntervals[i] = new DoubleMatrix(LayerWidths[i + 1], LayerWidths[i] + 1);
		}

		setNumberOfParameters();
	}

	public NeuralNetwork(JSONObject JO) throws Exception {
		JSONArray jSONLayerWidthsArray = JO.getJSONArray("LayerWidths");
		if (jSONLayerWidthsArray.length() < 2)
			throw new Exception();
		LayerWidths = new int[jSONLayerWidthsArray.length()];

		for (int i = 0; i < LayerWidths.length; i++) {
			if (jSONLayerWidthsArray.getInt(i) <= 0)
				throw new Exception();
			LayerWidths[i] = jSONLayerWidthsArray.getInt(i);
		}

		JSONArray jSONActivationFunctionsArray = JO.getJSONArray("activationFunctions");
		if (jSONActivationFunctionsArray.length() != LayerWidths.length - 1)
			throw new Exception();
		activationFunctions = new ActivationFunction[jSONActivationFunctionsArray.length()];
		for (int i = 0; i < activationFunctions.length; i++) {
			activationFunctions[i] = jSONActivationFunctionsArray.getEnum(ActivationFunction.class, i);
		}

		JSONArray jSONParametersArray = JO.getJSONArray("layerIntervals");
		layerIntervals = new DoubleMatrix[LayerWidths.length - 1];
		for (int i = 0; i < layerIntervals.length; i++) {
			/*
			 * the first 1 to LayerWidths[i] columns are for weight parameters, and the last
			 * column, LayerWidths[i] + 1, is for bias parameters.
			 */
			layerIntervals[i] = new DoubleMatrix(jSONParametersArray.getJSONObject(i));
		}

		setNumberOfParameters();
	}

	public void mutate(double rate) throws Exception {
		if (rate < 0 || rate > 1)
			throw new Exception();

		for (int i = 0; i < numberOfParameters; i++) {
			if (Math.random() < rate) {
				setParameterByIndex(i, getParameterByIndex(i) + Math.random() * 2 - 1);
			}
		}
	}

	public int getNumberOfLayers() {
		return LayerWidths.length;
	}

	public int getLayerWidth(int index) throws Exception {
		if (index < 0 || index >= getNumberOfLayers())
			throw new Exception();

		return LayerWidths[index];
	}

	/*
	 * "index" points to the weights between layer[index] and layer[index + 1].
	 */
	public double getWeightParameter(int index, int inNode_index, int outNode_index) throws Exception {
		if (index < 0 || index >= layerIntervals.length)
			throw new Exception();
		if (inNode_index < 0 || inNode_index >= getLayerWidth(index) || outNode_index < 0
				|| outNode_index >= getLayerWidth(index + 1))
			throw new Exception();

		return layerIntervals[index].get(outNode_index, inNode_index);
	}

	/*
	 * "index" points to the Biases between layer[index] and layer[index + 1].
	 */
	public double getBiasParameter(int index, int outNode_index) throws Exception {
		if (index < 0 || index >= layerIntervals.length)
			throw new Exception();
		if (outNode_index < 0 || outNode_index >= getLayerWidth(index + 1))
			throw new Exception();

		return layerIntervals[index].get(outNode_index, getLayerWidth(index + 1) - 1);
	}

	public void setWeightParameter(int index, int inNode_index, int outNode_index, double value) throws Exception {
		if (index < 0 || index >= layerIntervals.length)
			throw new Exception();
		if (inNode_index < 0 || inNode_index >= getLayerWidth(index) || outNode_index < 0
				|| outNode_index >= getLayerWidth(index + 1))
			throw new Exception();

		layerIntervals[index].set(outNode_index, inNode_index, value);
	}

	public void setBiasParameter(int index, int outNode_index, double value) throws Exception {
		if (index < 0 || index >= layerIntervals.length)
			throw new Exception();
		if (outNode_index < 0 || outNode_index >= getLayerWidth(index + 1))
			throw new Exception();

		layerIntervals[index].set(outNode_index, getLayerWidth(index + 1) - 1, value);
	}

	private void setNumberOfParameters() {
		int sum = 0;

		for (int i = 0; i < layerIntervals.length; i++) {
			sum += layerIntervals[i].getNumberOfRows() * layerIntervals[i].getNumberOfColumns();
		}

		numberOfParameters = sum;
	}

	public int getNumberOfParameters() {
		return numberOfParameters;
	}

	/*
	 * "index" is from 0 to getNumberOfParameters() - 1. This method access parameters
	 * by index with a certain order.
	 */
	public double getParameterByIndex(int index) throws Exception {
		if (index < 0 || index >= numberOfParameters)
			throw new Exception();

		for (int i = 0; i < layerIntervals.length; i++) {
			if (layerIntervals[i].getNumberOfRows() * layerIntervals[i].getNumberOfColumns() - 1 >= index) {
				return layerIntervals[i].get(index / layerIntervals[i].getNumberOfColumns(),
						index % layerIntervals[i].getNumberOfColumns());
			} else {
				index -= layerIntervals[i].getNumberOfRows() * layerIntervals[i].getNumberOfColumns();
			}
		}

		throw new Exception();
	}

	/*
	 * "index" is from 0 to getNumberOfParameters() - 1. This method access parameters
	 * by index with a certain order.
	 */
	public void setParameterByIndex(int index, double value) throws Exception {
		if (index < 0 || index >= numberOfParameters)
			throw new Exception();

		for (int i = 0; i < layerIntervals.length; i++) {
			if (layerIntervals[i].getNumberOfRows() * layerIntervals[i].getNumberOfColumns() > index) {
				layerIntervals[i].set(index / layerIntervals[i].getNumberOfColumns(), index % layerIntervals[i].getNumberOfColumns(),
						value);
				return;
			} else {
				index -= layerIntervals[i].getNumberOfRows() * layerIntervals[i].getNumberOfColumns();
			}
		}
	}

	public DoubleMatrix getOutput(DoubleMatrix inputLayer) throws Exception {
		if (inputLayer.getNumberOfColumns() != 1 || inputLayer.getNumberOfRows() != LayerWidths[0])
			throw new Exception();

		DoubleMatrix propagatedValues = inputLayer;
		for (int i = 0; i < layerIntervals.length; i++) {
			/*
			 * "extendedLayer" is a copy of "propagatedValues" with an extra row on the
			 * bottom, which is for the bias parameter calculation. The value is set to be
			 * "1".
			 */
			DoubleMatrix extendedLayer = new DoubleMatrix(propagatedValues.getNumberOfRows() + 1, 1);
			for (int r = 0; r < extendedLayer.getNumberOfRows() - 1; r++) {
				extendedLayer.set(r, 0, propagatedValues.get(r, 0));
			}
			extendedLayer.set(extendedLayer.getNumberOfRows() - 1, 0, 1);

			propagatedValues = layerIntervals[i].multiply(extendedLayer);

			for (int r = 0; r < propagatedValues.getNumberOfRows(); r++) {
				double sumValue = propagatedValues.get(r, 0);
				double activatedValue = activateValue(activationFunctions[i], sumValue);
				propagatedValues.set(r, 0, activatedValue);
			}
		}

		return propagatedValues;
	}

	public double activateValue(ActivationFunction activationFunction, double sumValue) {
		switch (activationFunction) {
		case LINEAR:
			return sumValue;
		case SIGMOID:
			return 1 / (1 + Math.exp(-sumValue));
		case STEP:
			if (sumValue > 0)
				return 1;
			else
				return 0;
		default:
			return sumValue;
		}
	}

	@Override
	public String toString() {
		return toJSONString();
	}

	@Override
	public String toJSONString() {
		JSONObject JO = new JSONObject();

		JO.put("LayerWidths", LayerWidths);
		JO.put("activationFunctions", activationFunctions);
		JO.put("layerIntervals", layerIntervals);

		return JO.toString();
	}

	@Override
	public NeuralNetwork clone() {
		try {
			NeuralNetwork newNeuralNetwork = new NeuralNetwork(LayerWidths, activationFunctions);

			for (int i = 0; i < newNeuralNetwork.layerIntervals.length; i++) {
				newNeuralNetwork.layerIntervals[i] = this.layerIntervals[i].clone();
			}

			return newNeuralNetwork;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}

}

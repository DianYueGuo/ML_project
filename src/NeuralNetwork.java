import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONString;

public class NeuralNetwork implements JSONString {

	private int[] layerDepths;
	private ActivationFunction[] activationFunctions;
	private FloatMatrix[] variables;
	private int numberOfVariables;

	/*
	 * layerDepths[0] is the depth of the input layer.
	 * layerDepths[layerDepths.lenght - 1] is the depth of the output layer.
	 * 
	 * activationFunctions.length should be equal to layerDepths.length - 1.
	 */
	NeuralNetwork(int[] layerDepths, ActivationFunction[] activationFunctions) throws Exception {
		if (layerDepths.length < 2)
			throw new Exception();
		this.layerDepths = new int[layerDepths.length];

		for (int i = 0; i < layerDepths.length; i++) {
			if (layerDepths[i] <= 0)
				throw new Exception();
			this.layerDepths[i] = layerDepths[i];
		}

		if (activationFunctions.length != layerDepths.length - 1)
			throw new Exception();
		this.activationFunctions = new ActivationFunction[activationFunctions.length];
		for (int i = 0; i < activationFunctions.length; i++) {
			this.activationFunctions[i] = activationFunctions[i];
		}

		variables = new FloatMatrix[layerDepths.length - 1];
		for (int i = 0; i < variables.length; i++) {
			/*
			 * the first 1 to layerDepths[i] columns are for weight variables, and the last
			 * column, layerDepths[i] + 1, is for bias variables.
			 */
			variables[i] = new FloatMatrix(layerDepths[i + 1], layerDepths[i] + 1);
		}

		setNumberOfVariables();
	}
	
	NeuralNetwork(JSONObject JO) throws Exception {
		JSONArray jSONLayerDepthsArray = JO.getJSONArray("layerDepths");
		if (jSONLayerDepthsArray.length() < 2)
			throw new Exception();
		layerDepths = new int[jSONLayerDepthsArray.length()];

		for (int i = 0; i < layerDepths.length; i++) {
			if (jSONLayerDepthsArray.getInt(i) <= 0)
				throw new Exception();
			layerDepths[i] = jSONLayerDepthsArray.getInt(i);
		}

		JSONArray jSONActivationFunctionsArray = JO.getJSONArray("activationFunctions");
		if (jSONActivationFunctionsArray.length() != layerDepths.length - 1)
			throw new Exception();
		activationFunctions = new ActivationFunction[jSONActivationFunctionsArray.length()];
		for (int i = 0; i < activationFunctions.length; i++) {
			activationFunctions[i] = jSONActivationFunctionsArray.getEnum(ActivationFunction.class, i);
		}

		JSONArray jSONVariablesArray = JO.getJSONArray("variables");
		variables = new FloatMatrix[layerDepths.length - 1];
		for (int i = 0; i < variables.length; i++) {
			/*
			 * the first 1 to layerDepths[i] columns are for weight variables, and the last
			 * column, layerDepths[i] + 1, is for bias variables.
			 */
			variables[i] = new FloatMatrix(jSONVariablesArray.getJSONObject(i));
		}

		setNumberOfVariables();
	}

	public int getNumberOfLayers() {
		return layerDepths.length;
	}

	public int getLayerDepth(int index) throws Exception {
		if (index < 0 || index >= getNumberOfLayers())
			throw new Exception();

		return layerDepths[index];
	}

	/*
	 * "index" points to the weights between layer[index] and layer[index + 1].
	 */
	public float getWeightVariable(int index, int inNode_index, int outNode_index) throws Exception {
		if (index < 0 || index >= variables.length)
			throw new Exception();
		if (inNode_index < 0 || inNode_index >= getLayerDepth(index) || outNode_index < 0
				|| outNode_index >= getLayerDepth(index + 1))
			throw new Exception();

		return variables[index].get(outNode_index, inNode_index);
	}

	/*
	 * "index" points to the Biases between layer[index] and layer[index + 1].
	 */
	public float getBiasVariable(int index, int outNode_index) throws Exception {
		if (index < 0 || index >= variables.length)
			throw new Exception();
		if (outNode_index < 0 || outNode_index >= getLayerDepth(index + 1))
			throw new Exception();

		return variables[index].get(outNode_index, getLayerDepth(index + 1) - 1);
	}

	public void setWeightVariable(int index, int inNode_index, int outNode_index, float value) throws Exception {
		if (index < 0 || index >= variables.length)
			throw new Exception();
		if (inNode_index < 0 || inNode_index >= getLayerDepth(index) || outNode_index < 0
				|| outNode_index >= getLayerDepth(index + 1))
			throw new Exception();

		variables[index].set(outNode_index, inNode_index, value);
	}

	public void setBiasVariable(int index, int outNode_index, float value) throws Exception {
		if (index < 0 || index >= variables.length)
			throw new Exception();
		if (outNode_index < 0 || outNode_index >= getLayerDepth(index + 1))
			throw new Exception();

		variables[index].set(outNode_index, getLayerDepth(index + 1) - 1, value);
	}

	private void setNumberOfVariables() {
		int sum = 0;

		for (int i = 0; i < variables.length; i++) {
			sum += variables[i].getNumberOfRows() * variables[i].getNumberOfColumns();
		}

		numberOfVariables = sum;
	}

	public int getNumberOfVariables() {
		return numberOfVariables;
	}

	/*
	 * "index" is from 0 to getNumberOfVariables() - 1. This method access variables
	 * by index with a certain order.
	 */
	public float getVariableByIndex(int index) throws Exception {
		if (index < 0 || index >= numberOfVariables)
			throw new Exception();

		for (int i = 0; i < variables.length; i++) {
			if (variables[i].getNumberOfRows() * variables[i].getNumberOfColumns() - 1 >= index) {
				return variables[i].get(index / variables[i].getNumberOfRows(),
						index % variables[i].getNumberOfColumns());
			} else {
				index -= variables[i].getNumberOfRows() * variables[i].getNumberOfColumns();
			}
		}

		throw new Exception();
	}

	public FloatMatrix getOutput(FloatMatrix inputLayer) throws Exception {
		if (inputLayer.getNumberOfColumns() != 1 || inputLayer.getNumberOfRows() != layerDepths[0])
			throw new Exception();

		FloatMatrix propagatedValues = inputLayer;
		for (int i = 0; i < variables.length; i++) {
			/*
			 * "extendedLayer" is a copy of "propagatedValues" with an extra row on the
			 * bottom, which is for the bias variable calculation. The value is set to be
			 * "1".
			 */
			FloatMatrix extendedLayer = new FloatMatrix(propagatedValues.getNumberOfRows() + 1, 1);
			for (int r = 0; r < extendedLayer.getNumberOfRows() - 1; r++) {
				extendedLayer.set(r, 0, propagatedValues.get(r, 0));
			}
			extendedLayer.set(extendedLayer.getNumberOfRows() - 1, 0, 1);

			propagatedValues = variables[i].multipy(extendedLayer);

			for (int r = 0; r < propagatedValues.getNumberOfRows(); r++) {
				float sumValue = propagatedValues.get(r, 0);
				float activatedValue = activateValue(activationFunctions[i], sumValue);
				propagatedValues.set(r, 0, activatedValue);
			}
		}

		return propagatedValues;
	}

	public float activateValue(ActivationFunction activationFunction, float sumValue) {
		switch (activationFunction) {
		case LINEAR:
			return sumValue;
		case SIGMOID:
			return (float) (1 / (1 + Math.exp(-sumValue)));
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
	public String toJSONString() {
		JSONObject JO = new JSONObject();

		JO.put("layerDepths", layerDepths);
		JO.put("activationFunctions", activationFunctions);
		JO.put("variables", variables);

		return JO.toString();
	}

}

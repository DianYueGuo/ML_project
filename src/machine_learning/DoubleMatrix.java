package machine_learning;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONString;

public class DoubleMatrix implements JSONString {

	private int numberOfRows;
	private int numberOfColumns;
	private double[][] values;

	public DoubleMatrix(int numberOfRows, int numberOfColumns) throws Exception {
		if (numberOfRows <= 0)
			throw new Exception();
		this.numberOfRows = numberOfRows;

		if (numberOfColumns <= 0)
			throw new Exception();
		this.numberOfColumns = numberOfColumns;

		this.values = new double[numberOfRows][numberOfColumns];
	}

	public DoubleMatrix(double[][] values) throws Exception {
		this(values.length, values[0].length);

		for (int r = 0; r < numberOfRows; r++) {
			for (int c = 0; c < numberOfColumns; c++) {
				set(r, c, values[r][c]);
			}
		}
	}

	public DoubleMatrix(JSONObject jsonObject) throws JSONException, Exception {
		this(jsonObject.getInt("numberOfRows"), jsonObject.getInt("numberOfColumns"));

		JSONArray valuesJsonArray = jsonObject.getJSONArray("values");
		for (int r = 0; r < numberOfRows; r++) {
			JSONArray rowJsonArray = valuesJsonArray.getJSONArray(r);
			for (int c = 0; c < numberOfColumns; c++) {
				set(r, c, rowJsonArray.getDouble(c));
			}
		}
	}

	public void set(int r, int c, double value) {
		values[r][c] = value;
	}

	public double get(int r, int c) {
		return values[r][c];
	}

	public int getNumberOfRows() {
		return numberOfRows;
	}

	public int getNumberOfColumns() {
		return numberOfColumns;
	}

	/*
	 * "matrix_A.multiply(matrix_B)" means "AB"
	 */
	public DoubleMatrix multiply(DoubleMatrix matrix) throws Exception {
		if (this.getNumberOfColumns() != matrix.getNumberOfRows())
			throw new Exception();

		DoubleMatrix a_matrix = this;
		DoubleMatrix b_matrix = matrix;
		DoubleMatrix c_matrix = new DoubleMatrix(a_matrix.getNumberOfRows(), b_matrix.getNumberOfColumns());

		for (int r = 0; r < c_matrix.getNumberOfRows(); r++) {
			for (int c = 0; c < c_matrix.getNumberOfColumns(); c++) {
				double sum = 0;

				for (int k = 0; k < a_matrix.getNumberOfColumns(); k++) {
					sum += a_matrix.get(r, k) * b_matrix.get(k, c);
				}

				c_matrix.set(r, c, sum);
			}
		}

		return c_matrix;
	}

	public DoubleMatrix transpose() throws Exception {
		DoubleMatrix transposedMatrix = new DoubleMatrix(numberOfColumns, numberOfRows);

		for (int r = 0; r < transposedMatrix.getNumberOfRows(); r++) {
			for (int c = 0; c < transposedMatrix.getNumberOfColumns(); c++) {
				transposedMatrix.set(r, c, get(c, r));
			}
		}

		return transposedMatrix;
	}

	@Override
	public String toString() {
		StringBuilder outputSB = new StringBuilder();

		outputSB.append("[");

		for (int r = 0; r < numberOfRows; r++) {
			outputSB.append("[");

			for (int c = 0; c < numberOfColumns; c++) {
				outputSB.append(values[r][c]);

				if (c < numberOfColumns - 1) {
					outputSB.append(", ");
				}
			}

			outputSB.append("]");

			if (r < numberOfRows - 1) {
				outputSB.append(",\n");
			}
		}

		outputSB.append("]");

		return outputSB.toString();
	}

	@Override
	public String toJSONString() {
		JSONObject JO = new JSONObject();
		
		JO.put("numberOfRows", numberOfRows);
		JO.put("numberOfColumns", numberOfColumns);
		JO.put("values", values);
		
		return JO.toString();
	}
	
	@Override
	public DoubleMatrix clone() {
		try {
			DoubleMatrix newDoubleMatrix = new DoubleMatrix(numberOfRows, numberOfColumns);
			
			for(int r = 0; r < numberOfRows; r++) {
				newDoubleMatrix.values[r] = values[r].clone();
			}
			
			return newDoubleMatrix;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}

}

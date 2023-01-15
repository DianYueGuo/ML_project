
public class FloatMatrix {

	private int numberOfRows;
	private int numberOfColumns;
	private float[][] values;

	FloatMatrix(int numberOfRows, int numberOfColumns) throws Exception {
		if (numberOfRows <= 0)
			throw new Exception();
		this.numberOfRows = numberOfRows;

		if (numberOfColumns <= 0)
			throw new Exception();
		this.numberOfColumns = numberOfColumns;

		this.values = new float[numberOfRows][numberOfColumns];
	}

	FloatMatrix(float[][] values) throws Exception {
		this(values.length, values[0].length);

		for (int r = 0; r < numberOfRows; r++) {
			for (int c = 0; c < numberOfColumns; c++) {
				set(r, c, values[r][c]);
			}
		}
	}

	public void set(int r, int c, float value) {
		values[r][c] = value;
	}

	public float get(int r, int c) {
		return values[r][c];
	}

	public int getNumberOfRows() {
		return numberOfRows;
	}

	public int getNumberOfColumns() {
		return numberOfColumns;
	}

	/*
	 * "matrix_A.multipy(matrix_B)" means "AB"
	 */
	public FloatMatrix multipy(FloatMatrix matrix) throws Exception {
		if (this.getNumberOfColumns() != matrix.getNumberOfRows())
			throw new Exception();
		
		FloatMatrix a_matrix = this;
		FloatMatrix b_matrix = matrix;
		FloatMatrix c_matrix = new FloatMatrix(a_matrix.getNumberOfRows(), b_matrix.getNumberOfColumns());
		
		for(int r = 0; r < c_matrix.getNumberOfRows(); r++) {
			for(int c = 0; c < c_matrix.getNumberOfColumns(); c++) {
				int sum = 0;
				
				for(int k = 0; k < a_matrix.getNumberOfColumns(); k++) {
					sum += a_matrix.get(r, k) * b_matrix.get(k, c);
				}
				
				c_matrix.set(r, c, sum);
			}
		}
		
		return c_matrix;
	}
	
	public FloatMatrix transpose() throws Exception {
		FloatMatrix transposedMatrix = new FloatMatrix(numberOfColumns, numberOfRows);
		
		for(int r = 0; r < transposedMatrix.getNumberOfRows(); r++) {
			for(int c = 0; c < transposedMatrix.getNumberOfColumns(); c++) {
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

}

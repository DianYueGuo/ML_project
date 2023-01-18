package TrainToPlayTicTacToe;

/*
 * the game board is numbered in the following way:
 *  0 | 1 | 2
 * -----------
 *  3 | 4 | 5
 * -----------
 *  6 | 7 | 8
 */
public class TicTacToeGame {

	public enum BlockState {
		EMPTY, MARKED_By_PLAYER1, MARKED_By_PLAYER2
	}

	public enum GameState {
		PLAYER1_TURN, PLAYER2_TURN, PLAYER1_WIN, PLAYER2_WIN, DRAW
	}

	private BlockState[][] board = new BlockState[][] { { BlockState.EMPTY, BlockState.EMPTY, BlockState.EMPTY },
			{ BlockState.EMPTY, BlockState.EMPTY, BlockState.EMPTY },
			{ BlockState.EMPTY, BlockState.EMPTY, BlockState.EMPTY } };
	private GameState gameState = GameState.PLAYER1_TURN;
	private int markTimes = 0;
	
	public int getMarkTimes() {
		return markTimes;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();

		sb.append("game state: " + gameState + "\n");

		sb.append("board:" + "\n");
		try {
			sb.append(" " + getBlockMark(0) + " | " + getBlockMark(1) + " | " + getBlockMark(2) + "\n");
			sb.append("-----------" + "\n");
			sb.append(" " + getBlockMark(3) + " | " + getBlockMark(4) + " | " + getBlockMark(5) + "\n");
			sb.append("-----------" + "\n");
			sb.append(" " + getBlockMark(6) + " | " + getBlockMark(7) + " | " + getBlockMark(8));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return sb.toString();
	}

	public void printGame() throws Exception {
		System.out.println(toString());
	}

	public String getBlockMark(int index) throws Exception {
		int row_index = index / 3;
		int column_index = index % 3;

		if (row_index < 0 || row_index > 2 || column_index < 0 || column_index > 2)
			throw new Exception();

		switch (board[row_index][column_index]) {
		case EMPTY:
			return " ";
		case MARKED_By_PLAYER1:
			return "O";
		case MARKED_By_PLAYER2:
			return "X";
		}

		return " ";
	}

	public GameState getGameState() {
		return gameState;
	}

	public BlockState getBlockState(int index) throws Exception {
		int row_index = index / 3;
		int column_index = index % 3;

		if (row_index < 0 || row_index > 2 || column_index < 0 || column_index > 2)
			throw new Exception();

		return board[row_index][column_index];
	}

	public boolean isLegalToMark(int index) {
		int row_index = index / 3;
		int column_index = index % 3;

		if (row_index < 0 || row_index > 2 || column_index < 0 || column_index > 2)
			return false;

		if (board[row_index][column_index] != BlockState.EMPTY)
			return false;

		return true;
	}

	public void mark(int index) throws Exception {
		if (!isLegalToMark(index)) {
			switch (gameState) {
			case PLAYER1_TURN:
				gameState = GameState.PLAYER2_WIN;
				break;
			case PLAYER2_TURN:
				gameState = GameState.PLAYER1_WIN;
				break;
			default:
				break;
			}
		}

		int row_index = index / 3;
		int column_index = index % 3;

		switch (gameState) {
		case PLAYER1_TURN:
			board[row_index][column_index] = BlockState.MARKED_By_PLAYER1;
			break;
		case PLAYER2_TURN:
			board[row_index][column_index] = BlockState.MARKED_By_PLAYER2;
			break;
		default:
			return;
		}
		markTimes++;

		if ((board[(row_index + 1) % 3][column_index] == board[row_index][column_index]
				&& board[(row_index + 2) % 3][column_index] == board[row_index][column_index])
				|| (board[row_index][(column_index + 1) % 3] == board[row_index][column_index]
						&& board[row_index][(column_index + 2) % 3] == board[row_index][column_index])
				|| (board[(row_index + 1) % 3][(column_index + 1) % 3] == board[row_index][column_index]
						&& board[(row_index + 2) % 3][(column_index + 2) % 3] == board[row_index][column_index]
						&& row_index == column_index)
				|| (board[(row_index + 2) % 3][(column_index + 1) % 3] == board[row_index][column_index]
						&& board[(row_index + 1) % 3][(column_index + 2) % 3] == board[row_index][column_index]
						&& row_index + column_index == 2)) {
			switch (gameState) {
			case PLAYER1_TURN:
				gameState = GameState.PLAYER1_WIN;
				break;
			case PLAYER2_TURN:
				gameState = GameState.PLAYER2_WIN;
				break;
			default:
				break;
			}
		} else if (markTimes == 9) {
			gameState = GameState.DRAW;
		}

		switch (gameState) {
		case PLAYER1_TURN:
			gameState = GameState.PLAYER2_TURN;
			break;
		case PLAYER2_TURN:
			gameState = GameState.PLAYER1_TURN;
			break;
		default:
			break;
		}
	}

}

package train_to_play_snake_game;

import java.util.ArrayList;

public class SnakeGame {

	public final static int FIELD_WIDTH = 5;
	public final static int FIELD_HEIGHT = 5;

	private ArrayList<Coordinate> snakeBody;
	private boolean hasCollided;
	private Coordinate appleCoordinate;
	private boolean hasEatenApple;

	private ScoreBoard scoreBoard;

	public enum HeadDirection {
		UP, DOWN, LEFT, RIGHT
	}

	public enum BlockState {
		EMPTY, APPLE, SNAKE_BODY, SNAKE_HEAD
	}

	public class ScoreBoard {

		private int totalNumberOfSteps;
		private int numberOfApplesEaten;
		private int largestStepsToNewApple;

		private int previousAteAppleStep;

		ScoreBoard() {
			totalNumberOfSteps = 0;
			numberOfApplesEaten = 0;
			largestStepsToNewApple = 0;
			previousAteAppleStep = 0;
		}

		void addStep() {
			totalNumberOfSteps++;
		}

		void eatApple() {
			numberOfApplesEaten++;

			if (largestStepsToNewApple < totalNumberOfSteps - previousAteAppleStep) {
				largestStepsToNewApple = totalNumberOfSteps - previousAteAppleStep;
			}

			previousAteAppleStep = totalNumberOfSteps;
		}

		int getLargestStepsToNewApple() {
			if (numberOfApplesEaten == 0) {
				return totalNumberOfSteps;
			}

			return largestStepsToNewApple;
		}

		int getTotalNumberOfSteps() {
			return totalNumberOfSteps;
		}

		int getNumberOfApplesEaten() {
			return numberOfApplesEaten;
		}

		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append("totalNumberOfSteps: " + totalNumberOfSteps + "\n");
			sb.append("numberOfApplesEaten: " + numberOfApplesEaten + "\n");
			sb.append("largestStepsToNewApple: " + largestStepsToNewApple + "\n");

			return sb.toString();
		}

	}

	/*
	 * The coordinate system looks like this: |(0,0)|(1,0)|...|(FIELD_WIDTH - 1,0)|
	 * |(0,1)|(1,1)|...|(FIELD_WIDTH - 1,1)| |(0,2)|(1,2)|...|(FIELD_WIDTH - 1,2)|
	 * |(0,FIELD_HEIGHT - 1)|(1,FIELD_HEIGHT - 1)|...|(FIELD_WIDTH - 1,FIELD_HEIGHT
	 * - 1)|
	 */
	public class Coordinate {

		private int x;
		private int y;

		Coordinate(int x, int y) {
			this.x = x;
			this.y = y;
		}

		public int getX() {
			return x;
		}

		public int getY() {
			return y;
		}

		public void setX(int x) {
			this.x = x;
		}

		public void setY(int y) {
			this.y = y;
		}

	}

	public SnakeGame() {
		scoreBoard = new ScoreBoard();

		snakeBody = new ArrayList<Coordinate>();

		snakeBody.add(new Coordinate(FIELD_WIDTH / 2, FIELD_HEIGHT / 2));

		hasCollided = false;

		hasEatenApple = false;

		spawnApple();
	}

	public ScoreBoard getScoreBoard() {
		return scoreBoard;
	}

	public boolean isGameOver() {
		return hasCollided;
	}

	public BlockState[][] getGameField() {
		BlockState[][] gameField = new BlockState[FIELD_HEIGHT][FIELD_WIDTH];

		for (int y = 0; y < FIELD_HEIGHT; y++) {
			for (int x = 0; x < FIELD_WIDTH; x++) {
				gameField[y][x] = BlockState.EMPTY;
			}
		}

		gameField[snakeBody.get(0).getY()][snakeBody.get(0).getX()] = BlockState.SNAKE_HEAD;

		for (int i = 1; i < snakeBody.size(); i++) {
			gameField[snakeBody.get(i).getY()][snakeBody.get(i).getX()] = BlockState.SNAKE_BODY;
		}

		if (appleCoordinate != null) {
			gameField[appleCoordinate.getY()][appleCoordinate.getX()] = BlockState.APPLE;
		}

		return gameField;
	}

	public void move(HeadDirection headDirection) {
		if (hasCollided)
			return;

		scoreBoard.addStep();

		int newHeadX = snakeBody.get(0).getX();
		int newHeadY = snakeBody.get(0).getY();

		switch (headDirection) {
		case DOWN:
			newHeadY++;
			break;
		case LEFT:
			newHeadX--;
			break;
		case RIGHT:
			newHeadX++;
			break;
		case UP:
			newHeadY--;
		}
		
		if (!hasEatenApple) {
			snakeBody.remove(snakeBody.size() - 1);
		}
		hasEatenApple = false;

		// check collision (if collide, end game)
		if ((newHeadX < 0) || (newHeadX >= FIELD_WIDTH) || (newHeadY < 0) || (newHeadY >= FIELD_HEIGHT)) {
			hasCollided = true;
			return;
		} else {
			for (Coordinate part : snakeBody) {
				if (part.getX() == newHeadX && part.getY() == newHeadY) {
					hasCollided = true;
					return;
				}
			}
		}

		snakeBody.add(0, new Coordinate(newHeadX, newHeadY));

		checkEatApple();

		if (hasEatenApple) {
			spawnApple();
		}
	}

	private void checkEatApple() {
		if (appleCoordinate != null) {
			if (appleCoordinate.getX() == snakeBody.get(0).getX()
					&& appleCoordinate.getY() == snakeBody.get(0).getY()) {
				hasEatenApple = true;
				scoreBoard.eatApple();
			}
		}
	}

	private void spawnApple() {
		ArrayList<Integer> blocks = new ArrayList<Integer>(FIELD_WIDTH * FIELD_HEIGHT);

		for (int i = 0; i < FIELD_WIDTH * FIELD_HEIGHT; i++) {
			blocks.add(i);
		}

		for (Coordinate part : snakeBody) {
			blocks.set(part.getY() * FIELD_WIDTH + part.getX(), -1);
		}

		for (int i = 0; i < blocks.size();) {
			if (blocks.get(i) == -1) {
				blocks.remove(i);
			} else {
				i++;
			}
		}

		if (blocks.size() > 0) {
			int randomNumber = (int) (Math.random() * blocks.size());
			int newAppleX = blocks.get(randomNumber) % FIELD_WIDTH;
			int newAppleY = blocks.get(randomNumber) / FIELD_WIDTH;

			appleCoordinate = new Coordinate(newAppleX, newAppleY);
		} else {
			appleCoordinate = null;
		}
	}

	@Override
	public String toString() {
		BlockState[][] field = this.getGameField();
		
		StringBuilder sb = new StringBuilder();

		for (int x = 0; x < field[0].length + 2; x++) {
			sb.append("-");
		}

		sb.append("\n");

		for (int y = 0; y < field.length; y++) {
			sb.append("|");
			for (int x = 0; x < field[0].length; x++) {
				switch (field[y][x]) {
				case APPLE:
					sb.append("A");
					break;
				case EMPTY:
					sb.append(" ");
					break;
				case SNAKE_BODY:
					sb.append("B");
					break;
				case SNAKE_HEAD:
					sb.append("H");
					break;
				}
			}
			sb.append("|\n");
		}

		for (int x = 0; x < field[0].length + 2; x++) {
			sb.append("-");
		}

		return sb.toString();
	}

}

package train_to_play_snake_game;

import java.util.ArrayList;

public class SnakeGame {

	private final int FIELD_WIDTH = 16;
	private final int FIELD_HEIGHT = 16;

	private ArrayList<Coordinate> snakeBody;
	private boolean hasCollided;
	private Coordinate appleCoordinate;
	private boolean hasEatenApple;

	public enum HeadDirection {
		UP, DOWN, LEFT, RIGHT
	}

	public enum BlockState {
		EMPTY, APPLE, SNAKE_BODY, SNAKE_HEAD
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

	SnakeGame() {
		snakeBody = new ArrayList<Coordinate>();

		snakeBody.add(new Coordinate(FIELD_WIDTH / 2, FIELD_HEIGHT / 2));

		hasCollided = false;

		hasEatenApple = false;

		spawnApple();
	}
	
	public BlockState[][] getGameField() {
		BlockState[][] gameField = new BlockState[FIELD_HEIGHT][FIELD_WIDTH];
		
		for(int y = 0; y < FIELD_HEIGHT; y++) {
			for(int x = 0; x < FIELD_WIDTH; x++) {
				gameField[y][x] = BlockState.EMPTY;
			}
		}
		
		gameField[snakeBody.get(0).getY()][snakeBody.get(0).getX()] = BlockState.SNAKE_HEAD;
		
		for (int i = 1; i < snakeBody.size(); i++) {
			gameField[snakeBody.get(i).getY()][snakeBody.get(i).getX()] = BlockState.SNAKE_BODY;
		}
		
		gameField[appleCoordinate.getY()][appleCoordinate.getX()] = BlockState.APPLE;
		
		return gameField;
	}

	public void move(HeadDirection headDirection) {
		if (hasCollided)
			return;

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
		if (!hasEatenApple) {
			snakeBody.remove(snakeBody.size() - 1);
		}
		hasEatenApple = false;

		checkEatApple();

		if (hasEatenApple) {
			spawnApple();
		}
	}

	private void checkEatApple() {
		if (appleCoordinate.getX() == snakeBody.get(0).getX() && appleCoordinate.getY() == snakeBody.get(0).getY()) {
			hasEatenApple = true;
		}
	}

	private void spawnApple() {
		ArrayList<Integer> blocks = new ArrayList<Integer>(FIELD_WIDTH * FIELD_HEIGHT);

		for (int i = 0; i < blocks.size(); i++) {
			blocks.set(i, i);
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

		int randomNumber = (int) (Math.random() * blocks.size());
		int newAppleX = blocks.get(randomNumber) % FIELD_WIDTH;
		int newAppleY = blocks.get(randomNumber) / FIELD_WIDTH;

		appleCoordinate = new Coordinate(newAppleX, newAppleY);
	}

}
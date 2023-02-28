package train_to_play_snake_game;

import machine_learning.ActivationFunction;
import machine_learning.FloatMatrix;
import machine_learning.NeuralNetwork;
import train_to_play_snake_game.SnakeGame.BlockState;
import train_to_play_snake_game.SnakeGame.HeadDirection;

public class TrainModel {

	public static void train() throws Exception {
		NeuralNetwork model1 = new NeuralNetwork(
				new int[] { SnakeGame.FIELD_WIDTH * SnakeGame.FIELD_HEIGHT, 32, 32, 2 }, new ActivationFunction[] {
						ActivationFunction.SIGMOID, ActivationFunction.SIGMOID, ActivationFunction.STEP });

		model1.mutate(1);

		for (int generation = 1; generation <= 1000; generation++) {
			System.out.println("Generation " + generation);

			NeuralNetwork[] variants = new NeuralNetwork[500];

			variants[0] = model1;

			for (int i = 1; i < variants.length; i++) {
				variants[i] = model1.clone();
				variants[i].mutate((float) 0.008);
			}

			SnakeGame[] games = new SnakeGame[variants.length];

			for (int i = 0; i < variants.length; i++) {
				games[i] = new SnakeGame();

				play(games[i], variants[i]);
			}

			int selectionIndex = 0;
			for (int i = 0; i < variants.length; i++) {
				if (games[selectionIndex].getScoreBoard().getStepsPerApple() > games[i].getScoreBoard()
						.getStepsPerApple()) {
					selectionIndex = i;
				} else if (games[selectionIndex].getScoreBoard().getStepsPerApple() == games[i].getScoreBoard()
						.getStepsPerApple()) {
					if (games[selectionIndex].getScoreBoard().getTotalNumberOfSteps() < games[i].getScoreBoard()
							.getTotalNumberOfSteps()) {
						selectionIndex = i;
					} else if (games[selectionIndex].getScoreBoard().getTotalNumberOfSteps() == games[i].getScoreBoard()
							.getTotalNumberOfSteps()) {
						if (games[selectionIndex].getScoreBoard().getNumberOfApplesEaten() < games[i].getScoreBoard()
								.getNumberOfApplesEaten()) {
							selectionIndex = i;
						} else if (games[selectionIndex].getScoreBoard().getNumberOfApplesEaten() == games[i]
								.getScoreBoard().getNumberOfApplesEaten()) {

						}
					}
				}
			}

			model1 = variants[selectionIndex];

			System.out.println(games[selectionIndex].getScoreBoard());
		}
	}

	public static void play(SnakeGame game, NeuralNetwork model) throws Exception {
		while (!game.isEnd()) {
			game.move(getDirection(game.getGameField(), model));

			if (game.getScoreBoard().getTotalNumberOfSteps() > (game.getScoreBoard().getNumberOfApplesEaten() + 1)
					* SnakeGame.FIELD_HEIGHT * SnakeGame.FIELD_WIDTH)
				break;
		}
	}

	public static HeadDirection getDirection(BlockState[][] gameField, NeuralNetwork model) throws Exception {
		float[][] inputArray = new float[1][SnakeGame.FIELD_HEIGHT * SnakeGame.FIELD_WIDTH];

		for (int y = 0; y < gameField.length; y++) {
			for (int x = 0; x < gameField[0].length; x++) {
				inputArray[0][y * SnakeGame.FIELD_WIDTH + x] = convertToNumber(gameField[y][x]);
			}
		}

		FloatMatrix outputMatrix = model.getOutput(new FloatMatrix(inputArray).transpose());
		int ouputNumber = (int) (outputMatrix.get(0, 0) + 2 * outputMatrix.get(1, 0));

		switch (ouputNumber) {
		case 0:
			return HeadDirection.UP;
		case 1:
			return HeadDirection.DOWN;
		case 2:
			return HeadDirection.LEFT;
		case 3:
			return HeadDirection.RIGHT;
		}
		return null;
	}

	private static int convertToNumber(BlockState blockState) {
		switch (blockState) {
		case APPLE:
			return 3;
		case EMPTY:
			return 0;
		case SNAKE_BODY:
			return 1;
		case SNAKE_HEAD:
			return 2;
		}
		return 0;
	}

}

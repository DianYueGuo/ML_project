package train_to_play_snake_game;

import machine_learning.ActivationFunction;
import machine_learning.DoubleMatrix;
import machine_learning.NeuralNetwork;
import train_to_play_snake_game.SnakeGame.BlockState;
import train_to_play_snake_game.SnakeGame.HeadDirection;

public class TrainModel {

	static class AverageScoreBoard {

		private int totalNumberOfSteps;
		private int numberOfApplesEaten;
		private int numberOfGames;
		private int sumOfLargestStepsToNewApple;

		AverageScoreBoard() {
			totalNumberOfSteps = 0;
			numberOfApplesEaten = 0;
			numberOfGames = 0;
			sumOfLargestStepsToNewApple = 0;
		}

		void addGame(SnakeGame game) {
			numberOfGames++;
			totalNumberOfSteps += game.getScoreBoard().getTotalNumberOfSteps();
			numberOfApplesEaten += game.getScoreBoard().getNumberOfApplesEaten();
			sumOfLargestStepsToNewApple += game.getScoreBoard().getLargestStepsToNewApple();
		}

		double getNumberOfStepsPerGame() {
			return totalNumberOfSteps / (double) numberOfGames;
		}

		double getNumberOfApplesEatenPerGame() {
			return numberOfApplesEaten / (double) numberOfGames;
		}

		double getApplesPerStepPerGame() {
			return numberOfApplesEaten / (double) totalNumberOfSteps / numberOfGames;
		}

		double getLargestStepsToNewApple() {
			return sumOfLargestStepsToNewApple / (double) numberOfGames;
		}

		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append("NumberOfApplesEatenPerGame: " + getNumberOfApplesEatenPerGame() + "\n");
			sb.append("ApplesPerStepPerGame: " + getApplesPerStepPerGame() + "\n");
			sb.append("NumberOfStepsPerGame: " + getNumberOfStepsPerGame() + "\n");
			sb.append("LargestStepsToNewApple: " + getLargestStepsToNewApple() + "\n");

			return sb.toString();
		}

	}

	public static void train() throws Exception {
		NeuralNetwork model1 = new NeuralNetwork(
				new int[] { SnakeGame.FIELD_WIDTH * SnakeGame.FIELD_HEIGHT, 10, 10, 2 }, new ActivationFunction[] {
						ActivationFunction.SIGMOID, ActivationFunction.SIGMOID, ActivationFunction.STEP });

		model1.mutate(0);

		for (int generation = 1; generation <= 10000; generation++) {
			System.out.println("Generation " + generation);

			NeuralNetwork[] variants = new NeuralNetwork[400];

			variants[0] = model1;

			for (int i = 1; i < variants.length; i++) {
				variants[i] = variants[0].clone();
				variants[i].mutate(0.001);
			}

			AverageScoreBoard[] avgScoreBoards = new AverageScoreBoard[variants.length];

			for (int i = 0; i < variants.length; i++) {
				avgScoreBoards[i] = new AverageScoreBoard();

				for (int t = 0; t < SnakeGame.FIELD_WIDTH * SnakeGame.FIELD_HEIGHT * 2; t++) {
					SnakeGame game = new SnakeGame();

					play(game, variants[i]);

					avgScoreBoards[i].addGame(game);
				}
			}

			int selectionIndex = 0;
			for (int i = 0; i < variants.length; i++) {
				if (avgScoreBoards[selectionIndex].getNumberOfApplesEatenPerGame() < avgScoreBoards[i]
						.getNumberOfApplesEatenPerGame()) {
					selectionIndex = i;
					
					continue;
				}
				
//				if (avgScoreBoards[selectionIndex].getLargestStepsToNewApple() > avgScoreBoards[i]
//						.getLargestStepsToNewApple()) {
//					selectionIndex = i;
//					
//					continue;
//				}
//				
//				if (avgScoreBoards[selectionIndex].getNumberOfStepsPerGame() < avgScoreBoards[i]
//						.getNumberOfStepsPerGame()) {
//					selectionIndex = i;
//					
//					continue;
//				}

			}

			model1 = variants[selectionIndex];

			System.out.println(avgScoreBoards[selectionIndex]);
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
		double[][] inputArray = new double[1][SnakeGame.FIELD_HEIGHT * SnakeGame.FIELD_WIDTH];

		for (int y = 0; y < gameField.length; y++) {
			for (int x = 0; x < gameField[0].length; x++) {
				inputArray[0][y * SnakeGame.FIELD_WIDTH + x] = convertToNumber(gameField[y][x]);
			}
		}

		DoubleMatrix outputMatrix = model.getOutput(new DoubleMatrix(inputArray).transpose());
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
			return 2;
		case EMPTY:
			return 0;
		case SNAKE_BODY:
			return -1;
		case SNAKE_HEAD:
			return 1;
		}
		return 0;
	}

}

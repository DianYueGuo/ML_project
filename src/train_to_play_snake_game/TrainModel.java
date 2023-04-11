package train_to_play_snake_game;

import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;

import machine_learning.ActivationFunction;
import machine_learning.DoubleMatrix;
import machine_learning.NeuralNetwork;
import train_to_play_snake_game.SnakeGame.BlockState;
import train_to_play_snake_game.SnakeGame.HeadDirection;
import train_to_play_snake_game.SnakeGame.ScoreBoard;

public class TrainModel {

	static class Model_and_score_combine {

		private NeuralNetwork model;
		private int countScoreBoards;

		private int totalNumberOfSteps;
		private int numberOfApplesEaten;
		private int largestStepsToNewApple;

		public Model_and_score_combine(NeuralNetwork model) {
			this.model = model;
		}

		public NeuralNetwork getModel() {
			return this.model;
		}

		public void addScoreBoard(ScoreBoard scoreBoard) {
			countScoreBoards++;

			totalNumberOfSteps += scoreBoard.getTotalNumberOfSteps();
			numberOfApplesEaten += scoreBoard.getNumberOfApplesEaten();
			largestStepsToNewApple += scoreBoard.getLargestStepsToNewApple();
		}

		public double getTotalNumberOfSteps() {
			return totalNumberOfSteps / (double) countScoreBoards;
		}

		public double getNumberOfApplesEaten() {
			return numberOfApplesEaten / (double) countScoreBoards;
		}

		public double getLargestStepsToNewApple() {
			return largestStepsToNewApple / (double) countScoreBoards;
		}

	}

	public static void train() throws Exception {

		PrintWriter writer = new PrintWriter("/Users/Joseph/Desktop/" + new Date() + ".csv", "UTF-8");
		System.out.println("create file: \"/Users/Joseph/Desktop/" + new Date() + ".csv\"");

		writer.println("generation, numberOfApplesEaten");

		int numberOfSpecies = 10;
		int numberOfOffsprints = 20;
		Model_and_score_combine[] model_and_score_combine = new Model_and_score_combine[numberOfSpecies
				* (numberOfOffsprints + 1)];

		for (int i = 0; i < numberOfSpecies; i++) {
			model_and_score_combine[i] = new Model_and_score_combine(new NeuralNetwork(new int[] { 6, 20, 20, 20, 4 },
					new ActivationFunction[] { ActivationFunction.SIGMOID, ActivationFunction.SIGMOID,
							ActivationFunction.SIGMOID, ActivationFunction.SIGMOID }));

			model_and_score_combine[i].getModel().mutate(1);
		}

		for (int generation = 0; generation <= 1000; generation++) {

			for (int i = 0; i < numberOfOffsprints; i++) {
				for (int j = 0; j < numberOfSpecies; j++) {
					NeuralNetwork model = model_and_score_combine[j].getModel().clone();
					model.mutate(0.01);
					model_and_score_combine[numberOfSpecies * (i + 1) + j] = new Model_and_score_combine(model);
				}
			}

			int test_times = 30;
			for (int t = 0; t < test_times; t++) {
				for (int i = 0; i < model_and_score_combine.length; i++) {
					SnakeGame game = new SnakeGame();
					play(game, model_and_score_combine[i].getModel(), false);
					model_and_score_combine[i].addScoreBoard(game.getScoreBoard());
				}
			}

			Arrays.sort(model_and_score_combine, new Comparator<Model_and_score_combine>() {
				@Override
				public int compare(Model_and_score_combine o1, Model_and_score_combine o2) {
					if (o1.getNumberOfApplesEaten() > o2.getNumberOfApplesEaten()) {
						return -1;
					} else if (o1.getNumberOfApplesEaten() == o2.getNumberOfApplesEaten()) {
						return 0;
					}

					return 1;
				}
			});

			System.out.println("generation: " + generation);
			System.out.println("numberOfApplesEaten: " + model_and_score_combine[0].getNumberOfApplesEaten());

			writer.print(generation + ",");
			writer.println(model_and_score_combine[0].getNumberOfApplesEaten());
		}

		System.out.println(model_and_score_combine[0].getModel());
		writer.println(model_and_score_combine[0].getModel());

		writer.close();

		play(new SnakeGame(), model_and_score_combine[0].getModel(), true);
	}

	public static void play(SnakeGame game, NeuralNetwork model, boolean doesPrint) throws Exception {
		while (!game.isEnd()) {
			if (doesPrint) {
				System.out.println(game);
			}

			game.move(getDirection(game.getGameField(), model));

			if (game.getScoreBoard().getTotalNumberOfSteps() > (game.getScoreBoard().getNumberOfApplesEaten() + 1)
					* SnakeGame.FIELD_HEIGHT * SnakeGame.FIELD_WIDTH)
				break;
		}
	}

	public static HeadDirection getDirection(BlockState[][] gameField, NeuralNetwork model) throws Exception {

		int apple_x = -1;
		int apple_y = -1;
		for (int y = 0; y < SnakeGame.FIELD_HEIGHT; y++) {
			for (int x = 0; x < SnakeGame.FIELD_WIDTH; x++) {
				if (gameField[y][x] == BlockState.APPLE) {
					apple_x = x;
					apple_y = y;
					break;
				}
			}

			if (apple_x != -1) {
				break;
			}
		}

		int head_x = -1;
		int head_y = -1;
		for (int y = 0; y < SnakeGame.FIELD_HEIGHT; y++) {
			for (int x = 0; x < SnakeGame.FIELD_WIDTH; x++) {
				if (gameField[y][x] == BlockState.SNAKE_HEAD) {
					head_x = x;
					head_y = y;
					break;
				}
			}

			if (head_x != -1) {
				break;
			}
		}

		int left_distance = 1;
		for (; head_x - left_distance >= 0; left_distance++) {
			if (gameField[head_y][head_x - left_distance] == BlockState.SNAKE_BODY) {
				break;
			}
		}

		int right_distance = 1;
		for (; head_x + right_distance < SnakeGame.FIELD_WIDTH; right_distance++) {
			if (gameField[head_y][head_x + right_distance] == BlockState.SNAKE_BODY) {
				break;
			}
		}

		int up_distance = 1;
		for (; head_y - up_distance >= 0; up_distance++) {
			if (gameField[head_y - up_distance][head_x] == BlockState.SNAKE_BODY) {
				break;
			}
		}

		int down_distance = 1;
		for (; head_y + down_distance < SnakeGame.FIELD_HEIGHT; down_distance++) {
			if (gameField[head_y + down_distance][head_x] == BlockState.SNAKE_BODY) {
				break;
			}
		}

		DoubleMatrix outputMatrix = model.getOutput(new DoubleMatrix(new double[][] { {
				(apple_x - head_x) / (double) SnakeGame.FIELD_WIDTH,
				(apple_y - head_y) / (double) SnakeGame.FIELD_HEIGHT, left_distance / (double) SnakeGame.FIELD_WIDTH,
				right_distance / (double) SnakeGame.FIELD_WIDTH, up_distance / (double) SnakeGame.FIELD_HEIGHT,
				down_distance / (double) SnakeGame.FIELD_HEIGHT } }).transpose());

		int max_number_index = 0;
		for (int i = 0; i < 4; i++) {
			if (outputMatrix.get(i, 0) > outputMatrix.get(max_number_index, 0)) {
				max_number_index = i;
			}
		}

		switch (max_number_index) {
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

}

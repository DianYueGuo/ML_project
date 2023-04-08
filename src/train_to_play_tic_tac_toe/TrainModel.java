package train_to_play_tic_tac_toe;

import java.util.Arrays;
import java.util.Comparator;

import machine_learning.ActivationFunction;
import machine_learning.DoubleMatrix;
import machine_learning.NeuralNetwork;
import train_to_play_tic_tac_toe.TicTacToeGame.BlockState;

public class TrainModel {

	static class Player_and_score_combine {

		private NeuralNetwork player;
		private double score;

		Player_and_score_combine(NeuralNetwork player) {
			this.setPlayer(player);
			score = 0;
		}

		public NeuralNetwork getPlayer() {
			return player;
		}

		private void setPlayer(NeuralNetwork player) {
			this.player = player;
		}

		public double getScore() {
			return score;
		}

		public void addSore(double score) {
			this.score += score;
		}

		public void setScoreToZero() {
			this.score = 0;
		}

	}

	public static NeuralNetwork train() throws Exception {
		int numberOfSpecies = 10;
		int numberOfOffsprints = 10;
		Player_and_score_combine[] player_and_score_combine = new Player_and_score_combine[numberOfSpecies
				* (numberOfOffsprints + 1)];

		for (int i = 0; i < numberOfSpecies; i++) {
			player_and_score_combine[i] = new Player_and_score_combine(new NeuralNetwork(new int[] { 9, 18, 1 },
					new ActivationFunction[] { ActivationFunction.SIGMOID, ActivationFunction.SIGMOID }));
			player_and_score_combine[i].getPlayer().mutate(1);
		}

		for (int generation = 0; generation < 100; generation++) {
			for (int i = 0; i < numberOfSpecies; i++) {
				player_and_score_combine[i].setScoreToZero();
			}

			for (int i = 0; i < numberOfOffsprints; i++) {
				for (int j = 0; j < numberOfSpecies; j++) {
					NeuralNetwork player = player_and_score_combine[j].getPlayer().clone();
					player.mutate(0.05f);
					player_and_score_combine[numberOfSpecies * (i + 1) + j] = new Player_and_score_combine(player);
				}
			}

			int test_times = 200;
			for (int i = 0; i < player_and_score_combine.length; i++) {
				for (int t = 0; t < test_times; t++) {
					TicTacToeGame game1 = match(null, player_and_score_combine[i].getPlayer());
//					switch (game1.getGameState()) {
//					case DRAW:
//						player_and_score_combine[i].addSore(0.1f);
//						break;
//					case PLAYER2_WIN:
//						player_and_score_combine[i].addSore(0.5f);
//						break;
//					}

					game1 = match(player_and_score_combine[i].getPlayer(), null);
					switch (game1.getGameState()) {
					case DRAW:
						player_and_score_combine[i].addSore(0.5f);
						break;
					case PLAYER1_WIN:
						player_and_score_combine[i].addSore(1);
						break;
					case PLAYER2_WIN:
						player_and_score_combine[i].addSore(0);
						break;
					}
				}
			}

			Arrays.sort(player_and_score_combine, new Comparator<Player_and_score_combine>() {

				@Override
				public int compare(Player_and_score_combine o1, Player_and_score_combine o2) {
					if (o1.getScore() - o2.getScore() < 0) {
						return 1;
					} else if (o1.getScore() - o2.getScore() == 0) {
						return 0;
					} else {
						return -1;
					}
				}

			});

			System.out.println("generation: " + generation);
			System.out.println("max_score: " + player_and_score_combine[0].getScore() / test_times);
//			System.out.println(match(null, player_and_score_combine[0].getPlayer()));
			System.out.println(match(player_and_score_combine[0].getPlayer(), null));
		}

		return player_and_score_combine[0].getPlayer();

	}

	public static TicTacToeGame match(NeuralNetwork player1, NeuralNetwork player2) throws Exception {
		TicTacToeGame game = new TicTacToeGame();

		while (game.getGameState() == TicTacToeGame.GameState.PLAYER1_TURN
				|| game.getGameState() == TicTacToeGame.GameState.PLAYER2_TURN) {
			int nextMove = 0;
			switch (game.getGameState()) {
			case PLAYER1_TURN:
				nextMove = getNextMove(player1, game);
				break;
			case PLAYER2_TURN:
				nextMove = getNextMove(player2, game);
				break;
			}
			game.mark(nextMove);
		}

		return game;
	}

	public static int getNextMove(NeuralNetwork model, TicTacToeGame game) throws Exception {
		if (model != null && (model.getLayerDepth(0) != 9 || model.getLayerDepth(model.getNumberOfLayers() - 1) != 1))
			throw new Exception();

		double maxProbability = 0;
		int nextMove = 0;
		for (int i = 0; i < 9; i++) {
			if (!game.isLegalToMark(i))
				continue;

			double[][] board = new double[][] { { convertToNumber(game.getBlockState(0)),
					convertToNumber(game.getBlockState(1)), convertToNumber(game.getBlockState(2)),
					convertToNumber(game.getBlockState(3)), convertToNumber(game.getBlockState(4)),
					convertToNumber(game.getBlockState(5)), convertToNumber(game.getBlockState(6)),
					convertToNumber(game.getBlockState(7)), convertToNumber(game.getBlockState(8)) } };

			switch (game.getGameState()) {
			case PLAYER1_TURN:
				board[0][i] = convertToNumber(BlockState.MARKED_By_PLAYER1);
				break;
			case PLAYER2_TURN:
				board[0][i] = convertToNumber(BlockState.MARKED_By_PLAYER2);
				break;
			}

			if (model != null) {
				DoubleMatrix modelOutput = model.getOutput(new DoubleMatrix(board).transpose());

				if (modelOutput.get(0, 0) > maxProbability) {
					maxProbability = modelOutput.get(0, 0);
					nextMove = i;
				}
			} else {
				double randomNumber = Math.random();

				if (randomNumber > maxProbability) {
					maxProbability = randomNumber;
					nextMove = i;
				}
			}

		}

		return nextMove;
	}

	private static int convertToNumber(BlockState blockState) throws Exception {
		switch (blockState) {
		case EMPTY:
			return 0;
		case MARKED_By_PLAYER1:
			return 1;
		case MARKED_By_PLAYER2:
			return -1;
		default:
			throw new Exception();
		}
	}

}

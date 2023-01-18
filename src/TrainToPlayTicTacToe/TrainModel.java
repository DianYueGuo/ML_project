package TrainToPlayTicTacToe;

import TrainToPlayTicTacToe.TicTacToeGame.BlockState;
import machine_learning.ActivationFunction;
import machine_learning.FloatMatrix;
import machine_learning.NeuralNetwork;

public class TrainModel {

	public static NeuralNetwork train() throws Exception {
		NeuralNetwork model = new NeuralNetwork(new int[] { 9, 32, 32, 9, 1 },
				new ActivationFunction[] { ActivationFunction.SIGMOID, ActivationFunction.SIGMOID,
						ActivationFunction.SIGMOID, ActivationFunction.LINEAR });
		model.mutate(1);
		
		for (int generation = 0; generation < 2000; generation++) {
			NeuralNetwork[] players = new NeuralNetwork[90];

			players[0] = model;
			for (int i = 1; i < players.length; i++) {
				players[i] = model.clone();
				players[i].mutate(0.03f);
			}

			int[] scores = new int[players.length];
			for (int i = 0; i < players.length; i++) {
				for (int j = i + 1; j < players.length; j++) {
					TicTacToeGame game1 = match(players[i], players[j]);
					switch (game1.getGameState()) {
					case DRAW:
						scores[i]++;
						scores[j]++;
						break;
					case PLAYER1_WIN:
						scores[i]++;
						break;
					case PLAYER2_WIN:
						scores[j]++;
						break;
					}
					scores[i] += game1.getMarkTimes() * 10;
					scores[j] += game1.getMarkTimes() * 10;

					TicTacToeGame game2 = match(players[j], players[i]);
					switch (game2.getGameState()) {
					case DRAW:
						scores[i]++;
						scores[j]++;
						break;
					case PLAYER1_WIN:
						scores[j]++;
						break;
					case PLAYER2_WIN:
						scores[i]++;
						break;
					}
					scores[i] += game2.getMarkTimes() * 10;
					scores[j] += game2.getMarkTimes() * 10;
				}
			}

			int max_score = 0;
			int max_score_index = 0;
			for (int i = 0; i < scores.length; i++) {
				if (scores[i] >= max_score) {
					max_score = scores[i];
					max_score_index = i;
				}
			}

			model = players[max_score_index];

			System.out.println("generation: " + generation);
			
			System.out.println("score: " + scores[max_score_index]);
			System.out.println(match(model, model));
		}
		
		return model;
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
		if (model.getLayerDepth(0) != 9 || model.getLayerDepth(model.getNumberOfLayers() - 1) != 1)
			throw new Exception();

		FloatMatrix modelOutput = model
				.getOutput(new FloatMatrix(new float[][] { { convertToNumber(game.getBlockState(0)),
						convertToNumber(game.getBlockState(1)), convertToNumber(game.getBlockState(2)),
						convertToNumber(game.getBlockState(3)), convertToNumber(game.getBlockState(4)),
						convertToNumber(game.getBlockState(5)), convertToNumber(game.getBlockState(6)),
						convertToNumber(game.getBlockState(7)), convertToNumber(game.getBlockState(8)) } })
								.transpose());

		return (int) modelOutput.get(0, 0);
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

package train_to_play_tic_tac_toe;

import machine_learning.ActivationFunction;
import machine_learning.FloatMatrix;
import machine_learning.NeuralNetwork;
import train_to_play_tic_tac_toe.TicTacToeGame.BlockState;

public class TrainModel {

	public static NeuralNetwork train() throws Exception {
		NeuralNetwork model1 = new NeuralNetwork(new int[] { 9, 9, 1 },
				new ActivationFunction[] { ActivationFunction.SIGMOID, ActivationFunction.SIGMOID });

		model1.mutate(0);

		for (int generation = 0; generation < 10000; generation++) {
			NeuralNetwork[] players1 = new NeuralNetwork[100];

			players1[0] = model1;
			for (int i = 1; i < players1.length; i++) {
				players1[i] = model1.clone();
				players1[i].mutate(0.002f);
			}

			float[] scores1 = new float[players1.length];
			int repeat_times = 100;
			for (int i = 0; i < players1.length; i++) {
				for (int t = 0; t < repeat_times; t++) {
					TicTacToeGame game1 = match(null, players1[i]);
					switch (game1.getGameState()) {
					case DRAW:
						scores1[i] += 0.1;
						break;
					case PLAYER2_WIN:
						scores1[i] += 0.5;
						break;
					}

					game1 = match(players1[i], null);
					switch (game1.getGameState()) {
					case DRAW:
						scores1[i] += 0.1;
						break;
					case PLAYER1_WIN:
						scores1[i] += 0.5;
						break;
					}
				}
			}

			float max_score1 = 0;
			int max_score_index1 = 0;
			for (int i = 0; i < scores1.length; i++) {
				if (scores1[i] >= max_score1) {
					max_score1 = scores1[i];
					max_score_index1 = i;
				}
			}

			model1 = players1[max_score_index1];

			System.out.println("generation: " + generation);
			System.out.println("max_score: " + max_score1 / repeat_times);
			System.out.println(match(model1, model1));
		}

		return model1;
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

		float maxProbability = 0;
		int nextMove = 0;
		for (int i = 0; i < 9; i++) {
			if (!game.isLegalToMark(i))
				continue;

			float[][] board = new float[][] { { convertToNumber(game.getBlockState(0)),
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
				FloatMatrix modelOutput = model.getOutput(new FloatMatrix(board).transpose());

				if (modelOutput.get(0, 0) > maxProbability) {
					maxProbability = modelOutput.get(0, 0);
					nextMove = i;
				}
			} else {
				float randomNumber = (float) Math.random();

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

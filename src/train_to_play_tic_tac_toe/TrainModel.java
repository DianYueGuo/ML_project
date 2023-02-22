package train_to_play_tic_tac_toe;

import machine_learning.ActivationFunction;
import machine_learning.FloatMatrix;
import machine_learning.NeuralNetwork;
import train_to_play_tic_tac_toe.TicTacToeGame.BlockState;

public class TrainModel {

	public static NeuralNetwork train() throws Exception {
		NeuralNetwork model1 = new NeuralNetwork(new int[] { 9, 36, 36, 1 }, new ActivationFunction[] {
				ActivationFunction.SIGMOID, ActivationFunction.SIGMOID, ActivationFunction.SIGMOID });
		NeuralNetwork model2 = model1.clone();

		model1.mutate(1);
		model2.mutate(1);

		for (int generation = 0; generation < 1000; generation++) {
			NeuralNetwork[] players1 = new NeuralNetwork[100];
			NeuralNetwork[] players2 = new NeuralNetwork[players1.length];

			players1[0] = model1;
			for (int i = 1; i < players1.length; i++) {
				players1[i] = model1.clone();
				players1[i].mutate(0.005f);
			}

			players2[0] = model2;
			for (int i = 1; i < players2.length; i++) {
				players2[i] = model2.clone();
				players2[i].mutate(0.005f);
			}

			int[] scores1 = new int[players1.length];
			int[] scores2 = new int[players1.length];
			for (int i = 0; i < players1.length; i++) {
				TicTacToeGame game1 = match(model2, players1[i]);
				switch (game1.getGameState()) {
				case DRAW:
					scores1[i]++;
					break;
				case PLAYER2_WIN:
					scores1[i]++;
					break;
				}

				game1 = match(players1[i], model2);
				switch (game1.getGameState()) {
				case DRAW:
					scores1[i]++;
					break;
				case PLAYER1_WIN:
					scores1[i]++;
					break;
				}
				
				TicTacToeGame game2 = match(model1, players2[i]);
				switch (game2.getGameState()) {
				case DRAW:
					scores2[i]++;
					break;
				case PLAYER2_WIN:
					scores2[i]++;
					break;
				}

				game2 = match(players2[i], model1);
				switch (game2.getGameState()) {
				case DRAW:
					scores2[i]++;
					break;
				case PLAYER1_WIN:
					scores2[i]++;
					break;
				}
			}

			int max_score1 = 0;
			int max_score_index1 = 0;
			for (int i = 0; i < scores1.length; i++) {
				if (scores1[i] >= max_score1) {
					max_score1 = scores1[i];
					max_score_index1 = i;
				}
			}

			model1 = players1[max_score_index1];
			
			int max_score2 = 0;
			int max_score_index2 = 0;
			for (int i = 0; i < scores2.length; i++) {
				if (scores2[i] >= max_score2) {
					max_score2 = scores2[i];
					max_score_index2 = i;
				}
			}

			model2 = players2[max_score_index2];

			System.out.println("generation: " + generation);
			System.out.println(match(model1, model2));
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
		if (model.getLayerDepth(0) != 9 || model.getLayerDepth(model.getNumberOfLayers() - 1) != 1)
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

			FloatMatrix modelOutput = model.getOutput(new FloatMatrix(board).transpose());

			if (modelOutput.get(0, 0) > maxProbability) {
				maxProbability = modelOutput.get(0, 0);
				nextMove = i;
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

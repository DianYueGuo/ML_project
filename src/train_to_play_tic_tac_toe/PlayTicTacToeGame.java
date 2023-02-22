package train_to_play_tic_tac_toe;

import java.util.Scanner;

import machine_learning.NeuralNetwork;

public class PlayTicTacToeGame {

	public static void playInCommandLine() throws Exception {
		TicTacToeGame game = new TicTacToeGame();

		while (game.getGameState() == TicTacToeGame.GameState.PLAYER1_TURN
				|| game.getGameState() == TicTacToeGame.GameState.PLAYER2_TURN) {
			System.out.println(game);

			System.out.print("input mark index(0 ~ 8): ");
			Scanner scanner = new Scanner(System.in);
			game.mark(scanner.nextInt());
		}

		System.out.println(game);
	}

	public static void playWithAI(NeuralNetwork model) throws Exception {
		TicTacToeGame game = new TicTacToeGame();

		while (game.getGameState() == TicTacToeGame.GameState.PLAYER1_TURN
				|| game.getGameState() == TicTacToeGame.GameState.PLAYER2_TURN) {
			System.out.println(game);

			switch (game.getGameState()) {
			case PLAYER1_TURN:
				System.out.print("input mark index(0 ~ 8): ");
				Scanner scanner = new Scanner(System.in);
				game.mark(scanner.nextInt());
				break;
			case PLAYER2_TURN:
				game.mark(TrainModel.getNextMove(model, game));
				break;
			}
		}

		System.out.println(game);
		
		game = new TicTacToeGame();

		while (game.getGameState() == TicTacToeGame.GameState.PLAYER1_TURN
				|| game.getGameState() == TicTacToeGame.GameState.PLAYER2_TURN) {
			System.out.println(game);

			switch (game.getGameState()) {
			case PLAYER2_TURN:
				System.out.print("input mark index(0 ~ 8): ");
				Scanner scanner = new Scanner(System.in);
				game.mark(scanner.nextInt());
				break;
			case PLAYER1_TURN:
				game.mark(TrainModel.getNextMove(model, game));
				break;
			}
		}

		System.out.println(game);
	}

}

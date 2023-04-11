package train_to_play_snake_game;

import java.util.Scanner;

import train_to_play_snake_game.SnakeGame.HeadDirection;

public class PlaySnakeGame {

	public static void playInCommandLine() {
		SnakeGame game = new SnakeGame();

		Scanner scanner = new Scanner(System.in);
		do {
			System.out.println(game);

			System.out.print("input direction: ");
			String direction = scanner.nextLine();

			switch (direction) {
			case "w":
				game.move(HeadDirection.UP);
				break;
			case "s":
				game.move(HeadDirection.DOWN);
				break;
			case "a":
				game.move(HeadDirection.LEFT);
				break;
			case "d":
				game.move(HeadDirection.RIGHT);
				break;
			}
		} while (!game.isEnd());
		scanner.close();

		System.out.println("GAME OVER");

		System.out.println(game.getScoreBoard());
	}

}

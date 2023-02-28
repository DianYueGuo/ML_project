package train_to_play_snake_game;

import java.util.Scanner;

import train_to_play_snake_game.SnakeGame.HeadDirection;

public class PlaySnakeGame {

	public static void playInCommandLine() {
		SnakeGame game = new SnakeGame();

		Scanner scanner = new Scanner(System.in);
		do {
			printField(game.getGameField());
			
			System.out.print("input direction: ");
			String direction = scanner.nextLine();
			
			switch(direction) {
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
			default:
				game.move(HeadDirection.UP);
			}
		} while (!game.isEnd());
		scanner.close();
		
		System.out.println("GAME OVER");
		
		System.out.println(game.getScoreBoard());
	}
	
	private static void printField(SnakeGame.BlockState[][] field) {
		StringBuilder sb = new StringBuilder();
		
		for(int x = 0; x < field[0].length + 2; x++) {
			sb.append("-");
		}
		
		sb.append("\n");
		
		for(int y = 0; y < field.length; y++) {
			sb.append("|");
			for(int x = 0; x < field[0].length; x++) {
				switch(field[y][x]) {
				case APPLE:
					sb.append("A");
					break;
				case EMPTY:
					sb.append(" ");
					break;
				case SNAKE_BODY:
					sb.append("B");
					break;
				case SNAKE_HEAD:
					sb.append("H");
					break;
				}
			}
			sb.append("|\n");
		}
		
		for(int x = 0; x < field[0].length + 2; x++) {
			sb.append("-");
		}
		
		sb.append("\n");
		
		System.out.print(sb);
	}
	
}

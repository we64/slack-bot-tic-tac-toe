package slack.tictactoe.models;

import java.util.Random;

import slack.tictactoe.models.Board.Result;

public class Game {
	private Player opponent;
	private Player challenger;
	private Board board;
	private Player nextMovePlayer;

	public Game(Player opponent, Player challenger) {
		this.opponent = opponent;
		this.challenger = challenger;
		this.board = new Board();
		int randomNum = new Random().nextInt(2);
		this.nextMovePlayer = randomNum == 0 ? opponent : challenger;
	}

	public synchronized Result makeMove(int x, int y) throws Exception {
		Result result = board.move(x, y);
		if (result.equals(Result.UNDECIDED)) {
			nextMovePlayer = nextMovePlayer == opponent ? challenger : opponent;
		}

		return result;
	}

	public Player getOpponent() {
		return opponent;
	}

	public Player getChallenger() {
		return challenger;
	}

	public Player getNextMovePlayer() {
		return nextMovePlayer;
	}

	public String displayBoard() {
		return board.display();
	}
}

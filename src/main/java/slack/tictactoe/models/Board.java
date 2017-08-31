package slack.tictactoe.models;

public class Board {
	public enum Result {
		WIN, DRAW, UNDECIDED
	};

	private static int size = 3;
	private static BoardMarker[][] board = new BoardMarker[size][size];

	private int numOfPlacedSquares = 0;
	private BoardMarker markerToBePlaced = BoardMarker.X;

	public synchronized Result move(int x, int y) throws Exception {
		if (x < 0 || x >= size || y < 0 || y >= size) {
			throw new Exception("Coordinate is out of bound.");
		}

		if (board[x][y] == null || board[x][y] == BoardMarker.Empty) {
			board[x][y] = markerToBePlaced;
		} else {
			throw new Exception("Grid location already has marker placed.");
		}

		numOfPlacedSquares++;
		if (numOfPlacedSquares <= (size - 1) * 2) {
			markerToBePlaced = markerToBePlaced == BoardMarker.X ? BoardMarker.O : BoardMarker.X;
			return Result.UNDECIDED;
		}

		// check end game conditions
		// check across the row
		for (int i = 0; i < size; i++) {
			if (board[i][y] != markerToBePlaced) {
				break;
			}

			if (i == size - 1) {
				return Result.WIN;
			}
		}

		// check across the column
		for (int i = 0; i < size; i++) {
			if (board[x][i] != markerToBePlaced) {
				break;
			}
			if (i == size - 1) {
				return Result.WIN;
			}
		}

		// need to check the backward diagonal
		if (x == y) {
			// we're on a diagonal
			for (int i = 0; i < size; i++) {
				if (board[i][i] != markerToBePlaced) {
					break;
				}
				if (i == size - 1) {
					return Result.WIN;
				}
			}
		}

		// need to check the forward diagonal
		if (x + y == size - 1) {
			for (int i = 0; i < size; i++) {
				if (board[i][(size - 1) - i] != markerToBePlaced) {
					break;
				}
				if (i == size - 1) {
					return Result.WIN;
				}
			}
		}

		// check draw
		if (numOfPlacedSquares == (size * size - 1)) {
			return Result.DRAW;
		}

		markerToBePlaced = markerToBePlaced == BoardMarker.X ? BoardMarker.O : BoardMarker.X;
		return Result.UNDECIDED;
	}

	public synchronized String display() {
		String startEndPadding = "```";

		String[] boardDisplay = new String[(size * 2) - 1];
		int counter = 0;
		for (int row = 0; row < size; row++) {
			String[] rowContent = new String[size];
			for (int col = 0; col < size; col++) {
				rowContent[col] = board[row][col] == null ? BoardMarker.Empty.toString() : board[row][col].toString();
			}

			String rowContentToDisplay = "| " + String.join(" | ", rowContent) + " |";
			boardDisplay[counter] = rowContentToDisplay;
			counter++;
			if (counter < boardDisplay.length) {
				boardDisplay[counter] = "|---+---+---|";
				counter++;
			}
		}

		String result = startEndPadding + String.join("\n", boardDisplay) + startEndPadding;
		return result;
	}
}

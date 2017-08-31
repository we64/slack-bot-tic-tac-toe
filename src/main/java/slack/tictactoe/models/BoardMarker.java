package slack.tictactoe.models;

public enum BoardMarker {
	X("X"), O("O"), Empty(" ");

	private final String text;

	private BoardMarker(final String text) {
		this.text = text;
	}

	@Override
	public String toString() {
		return text;
	}
}

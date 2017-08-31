package slack.tictactoe.models;

import org.springframework.util.StringUtils;

public class Player {
	private String playerId;
	private String playerUsername;

	public Player(String playerId, String playerUsername) {
		this.playerId = playerId;
		this.playerUsername = playerUsername;
	}

	public String getPlayerId() {
		return this.playerId;
	}

	public String getPlayerUsername() {
		return this.playerUsername;
	}

	public String getEncodedPlayerText() {
		return "<@" + playerId + "|" + playerUsername + ">";
	}

	public static Player makePlayerFromEncodedText(String text) {
		if (StringUtils.isEmpty(text) || !text.startsWith("<@") || !text.endsWith(">") || !text.contains("|")) {
			return null;
		}

		String[] textSplitted = text.split("\\|");
		String playerId = textSplitted[0].replace("<@", "").toUpperCase();
		String playerUsername = textSplitted[1].replace(">", "");

		return new Player(playerId, playerUsername);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}

		if (!Player.class.isAssignableFrom(obj.getClass())) {
			return false;
		}

		final Player other = (Player) obj;
		if ((this.playerId == null) ? (other.playerId != null) : !this.playerId.equalsIgnoreCase(other.playerId)) {
			return false;
		}

		if ((this.playerUsername == null) ? (other.playerUsername != null)
				: !this.playerUsername.equalsIgnoreCase(other.playerUsername)) {
			return false;
		}

		return true;
	}
}

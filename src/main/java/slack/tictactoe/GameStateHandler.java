package slack.tictactoe;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Component;

import slack.tictactoe.models.Game;
import slack.tictactoe.models.Player;

/**
 * This class maintains overall application's state
 * 
 * @author tonyzhang
 *
 */
@Component
public class GameStateHandler {

	private final static Map<Integer, Game> gameInProgress = Collections.synchronizedMap(new HashMap<Integer, Game>(1));
	private final static Set<String> challenges = Collections.synchronizedSet(new HashSet<String>());

	public boolean isGameInProgress() {
		return gameInProgress.get(1) == null ? false : true;
	}

	public void addGame(Game game) {
		gameInProgress.put(1, game);
	}

	public Game getGame() {
		return gameInProgress.get(1);
	}

	public void resetGame(Game game) {
		challenges.remove(challengeSetLookupKey(game.getChallenger(), game.getOpponent()));
		gameInProgress.remove(1);
	}

	public boolean isThereChallengeBetweenPlayers(Player player1, Player player2) {
		String challengeSetKey = challengeSetLookupKey(player1, player2);
		if (challenges.contains(challengeSetKey)) {
			return true;
		}
		return false;
	}

	public void addChallengeBetweenPlayers(Player challenger, Player opponent) {
		String challengeSetKey = challengeSetLookupKey(challenger, opponent);
		challenges.add(challengeSetKey);
	}

	private String challengeSetLookupKey(Player player1, Player player2) {
		String key = "";
		if (player1 == null || player1.getPlayerId() == null || player2 == null || player2.getPlayerId() == null) {
			return key;
		}

		if (player1.getPlayerId().compareToIgnoreCase(player2.getPlayerId()) >= 0) {
			key = player1.getPlayerId().toLowerCase() + player2.getPlayerId().toLowerCase();
		} else {
			key = player2.getPlayerId().toLowerCase() + player1.getPlayerId().toLowerCase();
		}

		return key;
	}
}

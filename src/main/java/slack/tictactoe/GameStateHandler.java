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

	private final static Map<String, Game> gameInProgress = Collections.synchronizedMap(new HashMap<String, Game>());
	private final static Set<String> challenges = Collections.synchronizedSet(new HashSet<String>());

	public boolean isGameInProgress(String channelId) {
		return gameInProgress.get(channelId) == null ? false : true;
	}

	public void addGame(String channelId, Game game) {
		gameInProgress.put(channelId, game);
	}

	public Game getGame(String channelId) {
		return gameInProgress.get(channelId);
	}

	public void resetGame(String channelId, Game game) {
		String challengeSetKey = challengeSetLookupKey(channelId, game.getChallenger(), game.getOpponent());
		if (!challengeSetKey.isEmpty()) {
			challenges.remove(challengeSetKey);
		}
		gameInProgress.remove(channelId);
	}

	public boolean isThereChallengeBetweenPlayers(String channelId, Player player1, Player player2) {
		String challengeSetKey = challengeSetLookupKey(channelId, player1, player2);
		if (!challengeSetKey.isEmpty() && challenges.contains(challengeSetKey)) {
			return true;
		}
		return false;
	}

	public void addChallengeBetweenPlayers(String channelId, Player challenger, Player opponent) {
		String challengeSetKey = challengeSetLookupKey(channelId, challenger, opponent);
		if (challengeSetKey.isEmpty()) {
			return;
		}

		challenges.add(challengeSetKey);
	}

	private String challengeSetLookupKey(String channelId, Player player1, Player player2) {
		String key = "";
		if (player1 == null || player1.getPlayerId() == null || player2 == null || player2.getPlayerId() == null) {
			return key;
		}

		key = channelId;
		if (player1.getPlayerId().compareToIgnoreCase(player2.getPlayerId()) >= 0) {
			key += player1.getPlayerId().toLowerCase() + player2.getPlayerId().toLowerCase();
		} else {
			key += player2.getPlayerId().toLowerCase() + player1.getPlayerId().toLowerCase();
		}

		return key;
	}
}

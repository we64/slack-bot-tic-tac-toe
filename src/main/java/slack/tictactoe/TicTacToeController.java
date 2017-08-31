package slack.tictactoe;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import slack.tictactoe.models.Board.Result;
import slack.tictactoe.models.Game;
import slack.tictactoe.models.Player;
import slack.tictactoe.models.third.party.Attachment;
import slack.tictactoe.models.third.party.RichMessage;

@RestController
public class TicTacToeController {

	private static Pattern mentionPattern = Pattern.compile("<@[0-9A-Za-z]*\\|[0-9A-Za-z]*>", Pattern.CASE_INSENSITIVE);
	private static Pattern markCommandPattern = Pattern.compile("^mark\\s[0-9A-Za-z]:[0-9A-Za-z]",
			Pattern.CASE_INSENSITIVE);

	@Value("${slashCommandToken}")
	private String slackToken;

	@Autowired
	private GameStateHandler gameStateHandler;

	@RequestMapping(value = "/ttt", method = RequestMethod.POST, consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
	public RichMessage onReceiveSlashCommand(@RequestParam("token") String token,
			@RequestParam("team_id") String teamId, @RequestParam("team_domain") String teamDomain,
			@RequestParam("channel_id") String channelId, @RequestParam("channel_name") String channelName,
			@RequestParam("user_id") String userId, @RequestParam("user_name") String userName,
			@RequestParam("command") String command, @RequestParam("text") String text,
			@RequestParam("response_url") String responseUrl) {
		if (!token.equals(slackToken)) {
			return new RichMessage("Sorry! You can't play Tic Tac Toe!");
		}

		String commandText = text == null ? "" : text.trim().toLowerCase();
		if (commandText.startsWith("challenge")) {
			// check to see if there is another player in the challenge
			String challengeUserEncodedString = extractUserEncodedString(commandText);
			if (StringUtils.isEmpty(challengeUserEncodedString)) {
				return new RichMessage("Must mention a person for your challenge!");
			}

			// check to see if there is a game going on
			if (gameStateHandler.isGameInProgress(channelId)) {
				return new RichMessage("Another game is already in progress, please wait until it is done.");
			}

			Player challenger = new Player(userId, userName);
			Player opponent = Player.makePlayerFromEncodedText(challengeUserEncodedString);

			if (gameStateHandler.isThereChallengeBetweenPlayers(channelId, challenger, opponent)) {
				return new RichMessage(challenger.getEncodedPlayerText()
						+ ", there is a challenge between you guys already, type /ttt bring it on "
						+ challenger.getEncodedPlayerText() + ", to start game!");
			}

			gameStateHandler.addChallengeBetweenPlayers(channelId, challenger, opponent);

			RichMessage richMessage = new RichMessage(opponent.getEncodedPlayerText() + ", type: /ttt bring it on "
					+ challenger.getEncodedPlayerText() + ", to accept the challenge!");
			richMessage.setResponseType("in_channel");

			return richMessage;
		} else if (commandText.startsWith("bring it on")) {
			// check to see if there is another player being referenced
			String bringItOnUserEncodedString = extractUserEncodedString(commandText);
			if (StringUtils.isEmpty(bringItOnUserEncodedString)) {
				return new RichMessage("Must mention the person who is challenging you!");
			}

			// check to see if there is a game going on
			if (gameStateHandler.isGameInProgress(channelId)) {
				return new RichMessage("Another game is already in progress, please wait until it is done.");
			}

			Player challenger = new Player(userId, userName);
			Player opponent = Player.makePlayerFromEncodedText(bringItOnUserEncodedString);

			// check to see if a challenge has been made yet
			if (!gameStateHandler.isThereChallengeBetweenPlayers(channelId, challenger, opponent)) {
				return new RichMessage("Please issue a challenge to " + opponent.getEncodedPlayerText() + " first!");
			}

			Game game = new Game(opponent, challenger);
			gameStateHandler.addGame(channelId, game);

			RichMessage richMessage = new RichMessage(game.displayBoard());
			richMessage.setResponseType("in_channel");
			Attachment[] attachments = new Attachment[1];
			attachments[0] = new Attachment();
			attachments[0].setText(
					game.getNextMovePlayer().getEncodedPlayerText() + " make your first move by typing: /ttt mark x:y");
			richMessage.setAttachments(attachments);

			return richMessage;
		} else if (markCommandPattern.matcher(commandText).find()) {
			RichMessage richMessage = null;
			Attachment[] attachments = new Attachment[1];
			attachments[0] = new Attachment();

			Player currentPlayer = new Player(userId, userName);
			Game game = gameStateHandler.getGame(channelId);
			if (game == null) {
				richMessage = new RichMessage("No game in progress, type: /ttt challenge @someone to play!");
			} else if (game.getNextMovePlayer().equals(currentPlayer)) {
				// make sure the coordinates are all numeric
				String[] coordinates = commandText.substring(commandText.length() - 3).split(":");
				int x = -1;
				int y = -1;
				try {
					x = Integer.valueOf(coordinates[0]);
					y = Integer.valueOf(coordinates[1]);
				} catch (Exception ex) {
					ex.printStackTrace();
				}

				if (x == -1 || y == -1) {
					return new RichMessage(
							"Mark command invalid, please type in the coordinate by entering: /ttt mark x:y");
				}

				try {
					Result result = game.makeMove(x, y);
					if (result.equals(Result.DRAW)) {
						richMessage = new RichMessage(game.displayBoard());
						richMessage.setResponseType("in_channel");
						attachments[0].setText("Game has ended in a DRAW!");
						gameStateHandler.resetGame(channelId, game);
					} else if (result.equals(Result.WIN)) {
						richMessage = new RichMessage(game.displayBoard());
						richMessage.setResponseType("in_channel");
						attachments[0].setText(currentPlayer.getEncodedPlayerText() + " has WON the game!");
						gameStateHandler.resetGame(channelId, game);
					} else {
						richMessage = new RichMessage(game.displayBoard());
						richMessage.setResponseType("in_channel");
						attachments[0].setText(game.getNextMovePlayer().getEncodedPlayerText()
								+ " please make your move by typing: /ttt mark x:y");
					}
				} catch (Exception e) {
					e.printStackTrace();

					richMessage = new RichMessage(game.displayBoard());
					attachments[0].setText(game.getNextMovePlayer().getEncodedPlayerText()
							+ " mark command failed, please check the coordinates and try again. Note, you can only mark on empty grids.");
				}
			} else {
				richMessage = new RichMessage(
						"Only " + currentPlayer.getEncodedPlayerText() + " can make the next move right now.");
			}

			richMessage.setAttachments(attachments);
			return richMessage;
		} else if (commandText.equalsIgnoreCase("show game")) {
			Game game = gameStateHandler.getGame(channelId);
			RichMessage richMessage = null;
			if (game == null) {
				richMessage = new RichMessage("No game in progress, type: /ttt challenge @someone to play!");
			} else {
				richMessage = new RichMessage(game.displayBoard());
				Attachment[] attachments = new Attachment[1];
				attachments[0] = new Attachment();
				attachments[0].setText(game.getNextMovePlayer().getEncodedPlayerText() + " is making the next move.");
				richMessage.setAttachments(attachments);
				richMessage.setResponseType("in_channel");
			}

			return richMessage;
		}

		return new RichMessage("Don't recognize the command, type: /ttt show game to see if there is a game going on.");
	}

	private String extractUserEncodedString(String text) {
		String userText = "";
		Matcher matcher = mentionPattern.matcher(text);
		if (matcher.find()) {
			userText = text.substring(matcher.start(0), matcher.end(0));
		}

		return userText;
	}

}

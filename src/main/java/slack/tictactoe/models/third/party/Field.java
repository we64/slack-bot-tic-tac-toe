package slack.tictactoe.models.third.party;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Taken from https://github.com/ramswaroop/jbot/blob/master/jbot/src/main/java/me/ramswaroop/jbot/core/slack/models/Field.java
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Field {
	private String title;
	private String value;
	@JsonProperty("short_enough")
	private boolean shortEnough;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public boolean isShortEnough() {
		return shortEnough;
	}

	public void setShortEnough(boolean shortEnough) {
		this.shortEnough = shortEnough;
	}
}

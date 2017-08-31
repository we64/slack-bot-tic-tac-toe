package slack.tictactoe;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;

@SpringBootApplication
public class TicTacToeSlackBotApplication extends SpringBootServletInitializer {

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(TicTacToeSlackBotApplication.class);
	}

	public static void main(String[] args) {
		SpringApplication.run(TicTacToeSlackBotApplication.class, args);
	}

}

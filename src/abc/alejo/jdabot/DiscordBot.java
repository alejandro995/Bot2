package abc.alejo.jdabot;

import javax.security.auth.login.LoginException;

import abc.alejo.listener.ReadyListener;
import net.dv8tion.jda.JDA;
import net.dv8tion.jda.JDABuilder;
import net.dv8tion.jda.exceptions.RateLimitedException;

public class DiscordBot {
	
	private JDA jda;
	private JDABuilder builder;
	
	public DiscordBot(){
		builder = new JDABuilder();
		builder.setBotToken(SECRET_INFO.TOKEN);
		builder.setAudioEnabled(false);
		builder.setBulkDeleteSplittingEnabled(false);
		
		
		builder.addListener(new ReadyListener());
		
		try {
			JDA jda = builder.buildBlocking();
		} catch (LoginException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (RateLimitedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	

}
}


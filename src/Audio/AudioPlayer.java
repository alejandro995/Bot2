package Audio;

import java.io.File;
import java.net.URL;
import java.util.Deque;
import java.util.LinkedList;
import java.util.Queue;

import javax.sound.midi.Sequence;
import javax.sound.sampled.UnsupportedAudioFileException;

import Exceptions.AudioException;
import net.dv8tion.jda.audio.player.URLPlayer;
import net.dv8tion.jda.entities.Guild;
import net.dv8tion.jda.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.managers.AudioManager;
import net.dv8tion.jda.player.MusicPlayer;


public class AudioPlayer {
	
	
	 MusicPlayer player;

	
	private Thread playListThread = null;
	
	
	
	public AudioPlayer(GuildMessageReceivedEvent event){
		this.playlist = new LinkedList<Track>();
		this.volume = 1f;
	}
	
	
	public void join(){
		
	}
	
}

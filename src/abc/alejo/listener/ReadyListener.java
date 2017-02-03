package abc.alejo.listener;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import net.dv8tion.jda.JDA;
import net.dv8tion.jda.MessageBuilder;
import net.dv8tion.jda.entities.Message;
import net.dv8tion.jda.entities.MessageChannel;
import net.dv8tion.jda.entities.User;
import net.dv8tion.jda.entities.VoiceChannel;
import net.dv8tion.jda.events.ReadyEvent;
import net.dv8tion.jda.events.message.MessageReceivedEvent;
import net.dv8tion.jda.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.hooks.ListenerAdapter;
import net.dv8tion.jda.managers.AudioManager;
import net.dv8tion.jda.player.MusicPlayer;
import net.dv8tion.jda.player.Playlist;
import net.dv8tion.jda.player.source.AudioInfo;
import net.dv8tion.jda.player.source.AudioSource;
import net.dv8tion.jda.player.source.AudioTimestamp;
import net.dv8tion.jda.player.source.RemoteSource;

public class ReadyListener extends ListenerAdapter {
	 public static final float DEFAULT_VOLUME = 0.35f;
	@Override
	public void onReady(ReadyEvent event) {
		System.out.println(event.getJDA().getSelfInfo().getUsername());
	}
	
	@Override
	public void onMessageReceived(MessageReceivedEvent event) {
				 JDA jda = event.getJDA(); 
				 long responseNumber = event.getResponseNumber();
				 User author = event.getAuthor();
				 Message message = event.getMessage();
				 MessageChannel channel = event.getChannel();
				 
				 String msg = message.getContent(); 
				 
				 if (msg.equals("!alejo"))
			        {
			            //This will send a message, "pong!", by constructing a RestAction and "queueing" the action with the Requester.
			            // By calling queue(), we send the Request to the Requester which will send it to discord. Using queue() or any
			            // of its different forms will handle ratelimiting for you automatically!

			            channel.sendMessage("El es el programador de este Bot, no puede hacer nada contra el.");
			        }			 
				 
	}
	
	@Override
	public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
		
		
		  try
	        {
	            //We specifically reread the admins.txt each time a command is run so that we can update the admins.txt
	            // while the bot is running. Basically this is just me being lazy.
	            if (!Files.readAllLines(Paths.get("admins.txt")).contains(event.getAuthor().getId()))
	                return;
	        }
	        catch (IOException e)
	        {
	            //Fail silently. Allows the admin system to be "disabled" when admins.txt does not exist.
//	            e.printStackTrace();
	        }

	        String message = event.getMessage().getContent();
	        AudioManager manager = event.getGuild().getAudioManager();
	        MusicPlayer player;
	        if (manager.getSendingHandler() == null)
	        {
	            player = new MusicPlayer();
	            player.setVolume(DEFAULT_VOLUME);
	            manager.setSendingHandler(player);
	        }
	        else
	        {
	            player = (MusicPlayer) manager.getSendingHandler();
	        }

	        if (message.startsWith("volume "))
	        {
	            float volume = Float.parseFloat(message.substring("volume ".length()));
	            volume = Math.min(1F, Math.max(0F, volume));
	            player.setVolume(volume);
	            event.getChannel().sendMessage("Volume was changed to: " + volume);
	        }

	        if (message.equals("list"))
	        {
	            List<AudioSource> queue = player.getAudioQueue();
	            if (queue.isEmpty())
	            {
	                event.getChannel().sendMessage("The queue is currently empty!");
	                return;
	            }


	            MessageBuilder builder = new MessageBuilder();
	            builder.appendString("__Current Queue.  Entries: " + queue.size() + "__\n");
	            for (int i = 0; i < queue.size() && i < 10; i++)
	            {
	                AudioInfo info = queue.get(i).getInfo();
//	                builder.appendString("**(" + (i + 1) + ")** ");
	                if (info == null)
	                    builder.appendString("*Could not get info for this song.*");
	                else
	                {
	                    AudioTimestamp duration = info.getDuration();
	                    builder.appendString("`[");
	                    if (duration == null)
	                        builder.appendString("N/A");
	                    else
	                        builder.appendString(duration.getTimestamp());
	                    builder.appendString("]` " + info.getTitle() + "\n");
	                }
	            }

	            boolean error = false;
	            int totalSeconds = 0;
	            for (AudioSource source : queue)
	            {
	                AudioInfo info = source.getInfo();
	                if (info == null || info.getDuration() == null)
	                {
	                    error = true;
	                    continue;
	                }
	                totalSeconds += info.getDuration().getTotalSeconds();
	            }

	            builder.appendString("\nTotal Queue Time Length: " + AudioTimestamp.fromSeconds(totalSeconds).getTimestamp());
	            if (error)
	                builder.appendString("`An error occured calculating total time. Might not be completely valid.");
	            event.getChannel().sendMessage(builder.build());
	        }
	        if (message.equals("nowplaying"))
	        {
	            if (player.isPlaying())
	            {
	                AudioTimestamp currentTime = player.getCurrentTimestamp();
	                AudioInfo info = player.getCurrentAudioSource().getInfo();
	                if (info.getError() == null)
	                {
	                    event.getChannel().sendMessage(
	                            "**Playing:** " + info.getTitle() + "\n" +
	                            "**Time:**    [" + currentTime.getTimestamp() + " / " + info.getDuration().getTimestamp() + "]");
	                }
	                else
	                {
	                    event.getChannel().sendMessage(
	                            "**Playing:** Info Error. Known source: " + player.getCurrentAudioSource().getSource() + "\n" +
	                            "**Time:**    [" + currentTime.getTimestamp() + " / (N/A)]");
	                }
	            }
	            else
	            {
	                event.getChannel().sendMessage("The player is not currently playing anything!");
	            }
	        }

	        //Start an audio connection with a VoiceChannel
	        if (message.startsWith("join "))
	        {
	            //Separates the name of the channel so that we can search for it
	            String chanName = message.substring(5);

	            //Scans through the VoiceChannels in this Guild, looking for one with a case-insensitive matching name.
	            VoiceChannel channel = event.getGuild().getVoiceChannels().stream().filter(
	                    vChan -> vChan.getName().equalsIgnoreCase(chanName))
	                    .findFirst().orElse(null);  //If there isn't a matching name, return null.
	            if (channel == null)
	            {
	                event.getChannel().sendMessage("There isn't a VoiceChannel in this Guild with the name: '" + chanName + "'");
	                return;
	            }
	            manager.openAudioConnection(channel);
	        }
	        //Disconnect the audio connection with the VoiceChannel.
	        if (message.equals("leave"))
	            manager.closeAudioConnection();

	        if (message.equals("skip"))
	        {
	            player.skipToNext();
	            event.getChannel().sendMessage("Skipped the current song.");
	        }

	        if (message.equals("repeat"))
	        {
	            if (player.isRepeat())
	            {
	                player.setRepeat(false);
	                event.getChannel().sendMessage("The player has been set to **not** repeat.");
	            }
	            else
	            {
	                player.setRepeat(true);
	                event.getChannel().sendMessage("The player been set to repeat.");
	            }
	        }

	        if (message.equals("shuffle"))
	        {
	            if (player.isShuffle())
	            {
	                player.setShuffle(false);
	                event.getChannel().sendMessage("The player has been set to **not** shuffle.");
	            }
	            else
	            {
	                player.setShuffle(true);
	                event.getChannel().sendMessage("The player been set to shuffle.");
	            }
	        }

	        if (message.equals("reset"))
	        {
	            player.stop();
	            player = new MusicPlayer();
	            player.setVolume(DEFAULT_VOLUME);
	            manager.setSendingHandler(player);
	            event.getChannel().sendMessage("Music player has been completely reset.");
	        }

	        //Start playing audio with our FilePlayer. If we haven't created and registered a FilePlayer yet, do that.
	        if (message.startsWith("play"))
	        {
	            //If no URL was provided.
	            if (message.equals("play"))
	            {
	                if (player.isPlaying())
	                {
	                    event.getChannel().sendMessage("Player is already playing!");
	                    return;
	                }
	                else if (player.isPaused())
	                {
	                    player.play();
	                    event.getChannel().sendMessage("Playback as been resumed.");
	                }
	                else
	                {
	                    if (player.getAudioQueue().isEmpty())
	                        event.getChannel().sendMessage("The current audio queue is empty! Add something to the queue first!");
	                    else
	                    {
	                        player.play();
	                        event.getChannel().sendMessage("Player has started playing!");
	                    }
	                }
	            }
	            else if (message.startsWith("play "))
	            {
	                String msg = "";
	                String url = message.substring("play ".length());
	                Playlist playlist = Playlist.getPlaylist(url, event.getGuild().getId());
	                List<AudioSource> sources = new LinkedList(playlist.getSources());
	                AudioSource source = new RemoteSource(url, url);
	                sources.add(source);
//	                AudioSource source = new LocalSource(new File(url));
	                AudioInfo info = source.getInfo();   //Preload the audio info.
	                if (sources.size() > 1)
	                {
	                    event.getChannel().sendMessage("Found a playlist with **" + sources.size() + "** entries.\n" +
	                            "Proceeding to gather information and queue sources. This may take some time...");
	                    final MusicPlayer fPlayer = player;
	                    Thread thread = new Thread()
	                    {
	                        @Override
	                        public void run()
	                        {
	                            for (Iterator<AudioSource> it = sources.iterator(); it.hasNext();)
	                            {
	                                AudioSource source = it.next();
	                                AudioInfo info = source.getInfo();
	                                List<AudioSource> queue = fPlayer.getAudioQueue();
	                                if (info.getError() == null)
	                                {
	                                    queue.add(source);
	                                    if (fPlayer.isStopped())
	                                        fPlayer.play();
	                                }
	                                else
	                                {
	                                    event.getChannel().sendMessage("Error detected, skipping source. Error:\n" + info.getError());
	                                    it.remove();
	                                }
	                            }
	                            event.getChannel().sendMessage("Finished queuing provided playlist. Successfully queued **" + sources.size() + "** sources");
	                        }
	                    };
	                    thread.start();
	                }
	                else
	                {
	                    source = sources.get(0);
	                    info = source.getInfo();
	                    if (info.getError() == null)
	                    {
	                        player.getAudioQueue().add(source);
	                        msg += "The provided URL has been added the to queue";
	                        if (player.isStopped())
	                        {
	                            player.play();
	                            msg += " and the player has started playing";
	                        }
	                        event.getChannel().sendMessage(msg + ".");
	                    }
	                    else
	                    {
	                        event.getChannel().sendMessage("There was an error while loading the provided URL.\n" +
	                                "Error: " + info.getError());
	                    }
	                }
	            }
	        }
	        if (message.equals("pause"))
	        {
	            player.pause();
	            event.getChannel().sendMessage("Playback has been paused.");
	        }
	        if (message.equals("stop"))
	        {
	            player.stop();
	            event.getChannel().sendMessage("Playback has been completely stopped.");
	        }
	        if (message.equals("restart"))
	        {
	            if (player.isStopped())
	            {
	                if (player.getPreviousAudioSource() != null)
	                {
	                    player.reload(true);
	                    event.getChannel().sendMessage("The previous song has been restarted.");
	                }
	                else
	                {
	                    event.getChannel().sendMessage("The player has never played a song, so it cannot restart a song.");
	                }
	            }
	            else
	            {
	                player.reload(true);
	                event.getChannel().sendMessage("The currently playing song has been restarted!");
	            }
	        }
		
	}
}

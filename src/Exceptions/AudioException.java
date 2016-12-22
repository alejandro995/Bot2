package Exceptions;

public class AudioException extends Exception{
	
	private static final long serialVersionUID = -1220053575230289581L;
	
	private String audioName;
	
	
	public  AudioException() {
		super("Could not load the track!");
		audioName = "";
	}
	
	public AudioException(String audioName) {
		super("Could not load the audio: " + audioName);
		this.audioName = audioName;
	}

	public String getAudioName() {
		return audioName;
	}

}

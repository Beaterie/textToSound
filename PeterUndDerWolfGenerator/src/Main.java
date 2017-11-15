import java.io.FileNotFoundException;
import java.io.IOException;

import org.jfugue.player.Player;

public class Main {

	public static void main(String[] args) throws FileNotFoundException, IOException{
		
		// process the file
	    String music = "Hallo";
	    // play music
	  	Player player = new Player();
	  	player.play(music);
	  }
	
}

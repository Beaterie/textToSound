import java.io.FileNotFoundException;
import java.io.IOException;

import org.jfugue.pattern.Pattern;
import org.jfugue.player.Player;

public class Main {

	public static void main(String[] args) throws FileNotFoundException, IOException {
			
		//MusicProcessor mp = new MusicProcessor("wolf", 1, 4);
		//Player player = new Player();
		//Pattern pattern = new Pattern("C D E F G A B 72");
//		Pattern mattern = new Pattern("B A G F E D C");
//		Pattern pattern = new Pattern(lattern + mattern);
		//player.play(pattern);
		
		TextProcessor processor1 = new TextProcessor("data/the-happy-prince.txt");
		TextProcessor processor2 = new TextProcessor("data/the-fox-and-the-crow.txt");
		MusicProcessor mp = new MusicProcessor(processor2.process());
		mp.process();
		
		
			
	}
	
}

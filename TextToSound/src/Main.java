import java.io.FileNotFoundException;
import java.io.IOException;

public class Main {

	public static void main(String[] args) throws FileNotFoundException, IOException {
			
		//MusicProcessor mp = new MusicProcessor("wolf", 1, 4);
		//Player player = new Player();
		//Pattern pattern = new Pattern("C D E F G A B 72");
//		Pattern mattern = new Pattern("B A G F E D C");
//		Pattern pattern = new Pattern(lattern + mattern);
		//player.play(pattern);
		
		TextLexProcessor processor1 = new TextLexProcessor("data/the-happy-prince.txt", "data/lexicon_animals.csv");
		TextLexProcessor processor2 = new TextLexProcessor("data/the-happy-prince.txt", "data/lexicon_environment.csv");
		TextLexProcessor processor3 = new TextLexProcessor("data/the-fox-and-the-crow.txt", "data/lexicon_animals.csv");
		MusicProcessor mp = new MusicProcessor(processor1.process());
		mp.process();
//		mp = new MusicProcessor(processor2.process());
//		mp.process();
//		mp = new MusicProcessor(processor3.process());
//		mp.process();
		
			
	}
	
}

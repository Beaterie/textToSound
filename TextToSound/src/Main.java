import java.io.FileNotFoundException;
import java.io.IOException;

public class Main {

	public static void main(String[] args) throws FileNotFoundException, IOException {
			
		//MusicProcessor mp = new MusicProcessor("wolf", 1, 4);
//		Player player = new Player();
//		Pattern pattern = new Pattern("L1 60/1.0 60/0.5 60/0.5");
//		//player.play(pattern);
//		//pattern.save(new File("twinkle.jfugue"));
//		    try {
//		        MidiFileManager.savePatternToMidi((PatternProducer) pattern, new File("pen"));
//		        System.out.println("damn");
//		    } catch (Exception ex) {
//		        ex.getStackTrace();
//		    }
	
		//TextLexProcessor processor1 = new TextLexProcessor("data/the-happy-prince.txt", "data/lexicon_animals.csv");
		//TextLexProcessor processor2 = new TextLexProcessor("data/the-happy-prince.txt", "data/lexicon_environment.txt");
		//TextLexProcessor processor3 = new TextLexProcessor("data/the-fox-and-the-crow.txt", "data/lexicon_animals.csv");
		
		//MusicProcessor mp = new MusicProcessor(processor3.process());
		//mp.setM_numOfSections(8);
		
		NEREmotionProcessor NERprocessor1 = new NEREmotionProcessor("data/the-happy-prince.txt", 10);
		//NEREmotionProcessor NERprocessor2 = new NEREmotionProcessor("data/the-fox-and-the-crow.txt", mp.getM_numOfSections());
		
		//NERprocessor1.main(args);
		EmotionResult EmotionResults = NERprocessor1.main(args);
	
		//mp.process(EmotionResults);
		
		
		
		System.out.println("done");
//		mp = new MusicProcessor(processor2.process());
//		mp.process();
//		mp = new MusicProcessor(processor3.process());
//		mp.process();
		
			
	}
	
}

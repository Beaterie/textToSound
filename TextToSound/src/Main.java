import java.io.FileNotFoundException;
import java.io.IOException;

public class Main {

	public static void main(String[] args) throws FileNotFoundException, IOException {
		String s = "Start";
		s+= "NOW";
		System.out.println(s);
		//MusicProcessor mp = new MusicProcessor("wolf", 1, 4);
//		Player player = new Player();
//		Pattern pattern = new Pattern("T60 V1 I0 (60+63+56)/1.0a127d127 (60+63+55)/1.0a127d127 " +
//				"V2 I0 48/0.5a127d127 48/0.5a127d127 R/0.5 43/0.5a127d127");
//		player.play(pattern);
		//pattern.save(new File("twinkle.jfugue"));
//		    try {
//		        MidiFileManager.savePatternToMidi((PatternProducer) pattern, new File("hi.midi"));
//		        System.out.println("worked");
//		    } catch (Exception ex) {
//		        ex.getStackTrace();
//		    }
	
		TextLexProcessor processor1 = new TextLexProcessor("data/The-Wolf-and-the-Seven-Kids.txt", "data/lexicon_animals.csv");
		//TextLexProcessor processor2 = new TextLexProcessor("data/the-happy-prince.txt", "data/lexicon_environment.txt");
		//TextLexProcessor processor3 = new TextLexProcessor("data/the-fox-and-the-crow.txt", "data/lexicon_animals.csv");
		
		MusicProcessor mp = new MusicProcessor(processor1.process());
		mp.setM_numOfSections(20);
		
		NEREmotionProcessor NERprocessor1 = new NEREmotionProcessor("data/The-Wolf-and-the-Seven-Kids.txt", mp.getM_numOfSections());
		//NEREmotionProcessor NERprocessor2 = new NEREmotionProcessor("data/the-fox-and-the-crow.txt", mp.getM_numOfSections());
		
		//NERprocessor1.main(args);
		EmotionResult EmotionResults = NERprocessor1.main(args);
	
		mp.process(EmotionResults);
		
		s = "done";
		s+= "ok";
		System.out.println(s);
//		mp = new MusicProcessor(processor2.process());
//		mp.process();
//		mp = new MusicProcessor(processor3.process());
//		mp.process();
		
			
	}
	
}
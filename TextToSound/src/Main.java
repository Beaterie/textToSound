import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Map;

public class Main {

	public static void main(String[] args) throws FileNotFoundException, IOException {

		System.out.println("Start analysing and generating music.");
		
		FilenameFilter filter = new FilenameFilter(){
			@Override
		    public boolean accept(File directory, String fileName) {
				if (fileName.endsWith(".txt")) {
		            return true;
		        }
		        return false;
		    }
		};
		
		File directory = new File("data/lit");
		String[] fairytales = directory.list(filter);
		int count = fairytales.length;
		String lex_path = "";
		
		for (int i = 0; i < count; i++) {
			System.out.println("Fairytale " + i + ": " + fairytales[i]);
			lex_path = "data/lit/" + fairytales[i];
			
			// DO THE MAGIC HERE
			Map<String, EmotionResult> animal_emotion_vecs = ExperimentalCharacterAnalysis.main(lex_path);
			TextLexProcessor processor1 = new TextLexProcessor(lex_path, "data/lexicon_people_and_animal_individual.csv");
			MusicProcessor mp = new MusicProcessor(processor1.process(), fairytales[i]);
			NEREmotionProcessor NERprocessor1 = new NEREmotionProcessor(lex_path, mp.getM_numOfSections());
			EmotionResult EmotionResults = NERprocessor1.main(args);
			mp.process(EmotionResults, animal_emotion_vecs);
			
			System.out.println("Fairytale " + i + " (" + fairytales[i] + ") completely analysed and set to music.");
			System.out.println("--------------------------------------------");
			System.out.println();
			System.out.println();
		}
		
//		fairytale = "Hansel-and-Gretel";
//		fairytale = "little-red-riding-hood";
		
//		Map<String, EmotionResult> animal_emotion_vecs = ExperimentalCharacterAnalysis.main(lex_path);
//		
//		TextLexProcessor processor1 = new TextLexProcessor(lex_path, "data/lexicon_people_and_animal_individual.csv");
		
//		NEREmotionProcessor NERprocessor1 = new NEREmotionProcessor(lex_path, mp.getM_numOfSections());
		
		//NERprocessor1.main(args);
//		EmotionResult EmotionResults = NERprocessor1.main(args);
//		
//		mp.process(EmotionResults, animal_emotion_vecs);
		
		System.out.println("FINISHED!");
//		mp = new MusicProcessor(processor2.process());
//		mp.process();
//		mp = new MusicProcessor(processor3.process());
//		mp.process();
		
			
	}
	
}
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Map;

public class Main {

	public static void main(String[] args) throws FileNotFoundException, IOException {
		
		PrintStream stdout = System.out;
		PrintStream printer = new PrintStream(new FileOutputStream("data/console-prints/console-print"));
		String lit_path = "";
		Boolean autoplay = false;
		
		
		// Example without autoplay (false):
		// $ java -jar textToSound.jar fairytale.txt false
		
		// Example with autoplay (true):
		// $ java -jar textToSound.jar fairytale.txt true
		
		// Example for working with all test-fairytales (possible only without autoplay):
		// $ java -jar textToSound.jar
		
		
		// indiviual input (fairytale)
		if (args.length > 0) {
			// check input correctness
			if (args.length != 2 || args[0].endsWith(".txt") == false) {
				System.out.println("Error: Please set only one fairytale.txt and" +
						" false/true for the midi-autoplay as input.\n" +
						"Please try again with the fairytale.txt in the 'your-fairytale'-folder.");
			}
			else if (args[1].equals("true") == false && args[1].equals("false") == false ) {
				System.out.println("Error: Please set only one fairytale.txt and" +
						" false/true for the midi-autoplay as input.\n" +
						"Please try again with the fairytale.txt in the 'your-fairytale'-folder.");
			}
			else {
				if (args[1].equals("true") == true) {
					autoplay = true;
				}
				Boolean canRun = false;
				lit_path = args[0];
				String[] steps = lit_path.split("/");
				String fairytale = "";
				if (steps.length >= 1) {
					if (steps.length > 2) {
						System.out.println("Error: Please set only one fairytale.txt and" +
								" false/true for the midi-autoplay as input.\n" +
								"Please try again with the fairytale.txt in the 'your-fairytale'-folder.");
					}
					else {
						if (steps.length == 2) {
							if (steps[0].equals("your-fairytale")) {
								canRun = true;
								fairytale = steps[1];
							}
						}
						else if (steps.length == 1) {
							canRun = true;
							fairytale = steps[0];
						}
						else {
							System.out.println("Error: Please set only one fairytale.txt and" +
									" false/true for the midi-autoplay as input.\n" +
									"Please try again with the fairytale.txt in the 'your-fairytale'-folder.");
						}
					}
//					System.out.println(fairytale);
//					System.out.println(lit_path);
//					System.out.println(canRun);
				}
				if (canRun == true) {
					System.out.println("The following input.txt will be analysed: " + fairytale);
					System.out.println("Start analysing and generating music.");
					printer = new PrintStream(new FileOutputStream("your-fairytale/console-print-" + fairytale));
					System.setOut(printer);
					
					// DO THE MAGIC HERE
					Map<String, EmotionResult> animal_emotion_vecs = ExperimentalCharacterAnalysis.main(lit_path);
					TextLexProcessor processor1 = new TextLexProcessor(lit_path,
							"data/lexica/lexicon_people_and_animal_individual.csv");
					MusicProcessor mp = new MusicProcessor(processor1.process(),
							fairytale, "your-fairytale/", autoplay);
					NEREmotionProcessor NERprocessor1 = new NEREmotionProcessor(lit_path, mp.getM_numOfSections());
					EmotionResult EmotionResults = NERprocessor1.main(args);
					mp.process(EmotionResults, animal_emotion_vecs);
					
					System.out.println("Fairytale " + " (" + lit_path +
							") completely analysed and set to music.");
					System.out.println("--------------------------------------------");
					System.out.println();
					System.out.println();
					
					printer.close();
					System.setOut(stdout);
					
					System.out.println("Fairytale " + "(" + lit_path +
							") completely analysed and set to music.");
					System.out.println("--------------------------------------------");
					System.out.println();
				}
			}	
		}
		
		// OR
		// analyse our test-fairytales
		if (args.length == 0) {
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
			
			for (int i = 0; i < count; i++) {
				System.out.println("Fairytale " + i + ": " + fairytales[i]);
				
				printer = new PrintStream(new FileOutputStream(
						"data/console-prints/console-print-" + fairytales[i]));
				System.setOut(printer);
				
				System.out.println("Fairytale " + i + ": " + fairytales[i]);
				lit_path = "data/lit/" + fairytales[i];
				
				// DO THE MAGIC HERE
				Map<String, EmotionResult> animal_emotion_vecs = ExperimentalCharacterAnalysis.main(lit_path);
				TextLexProcessor processor1 = new TextLexProcessor(lit_path,
						"data/lexica/lexicon_people_and_animal_individual.csv");
				MusicProcessor mp = new MusicProcessor(processor1.process(),
						fairytales[i], "data/music/", false);
				NEREmotionProcessor NERprocessor1 = new NEREmotionProcessor(lit_path, mp.getM_numOfSections());
				EmotionResult EmotionResults = NERprocessor1.main(args);
				mp.process(EmotionResults, animal_emotion_vecs);
				
				System.out.println("Fairytale " + i + " (" + fairytales[i] +
						") completely analysed and set to music.");
				System.out.println("--------------------------------------------");
				System.out.println();
				
				printer.close();
				System.setOut(stdout); 
				
				System.out.println("Fairytale " + i + " (" + fairytales[i] +
						") completely analysed and set to music.");
				System.out.println("--------------------------------------------");
				System.out.println();
			}
		}
		
		System.out.println("Programme has finished.");
			
	}
	
}
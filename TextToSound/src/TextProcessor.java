import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TextProcessor {
	
	// --------------------------------------------------------
	// Members
	// --------------------------------------------------------
	
	private String mSrcFileName;
	private String mFileLexicon = "data/lexicon_animals.txt";
	private List<String> mAnimalLex;
	
	
	// --------------------------------------------------------
	// Constructors
	// --------------------------------------------------------
	
	public TextProcessor() {};
	public TextProcessor(String src) {
		mSrcFileName = src;
	};
	
	
	// --------------------------------------------------------
	// Text-processing methods
	// --------------------------------------------------------
	
	/**
	 * Process the given source text and analyze occurrence of animals
	 * @throws IOException
	 */
	public ProcessResult process() throws IOException {
		
		verifySrc();					// Verify source file. Prints error message if needed.
		mAnimalLex = readLexicon();		// Read animal lexicon and store in list
		
		// Initialize variables, read input file
		String line;
		int numLine = 0, numMatch = 0, textLength = 0;
		Map<String, TargetOccurenceInfo> result = new HashMap<String, TargetOccurenceInfo>();
		Scanner scanner = new Scanner(new File(mSrcFileName), "UTF-8");
		scanner.useDelimiter("\n");
		
		// Process file line by line
		while(scanner.hasNextLine()){
			line = scanner.nextLine();
			// Remove all characters that are not alphabets
			String text = line.replaceAll("[^a-zA-Z ]", "").toLowerCase();
			// Find all animal occurrences in line
			for (String animal : mAnimalLex) {
				// Setting word boundaries to find matches of the whole word
				String pattern = "(?<!\\S)" + Pattern.quote(animal) + "(?!\\S)";
				Matcher m = Pattern.compile(pattern).matcher(text);
				while (m.find()) {
					int matchPos = m.start() + textLength;
					pushMatchPos(result, animal, matchPos);
				    numMatch++;
				}
			}
			textLength += text.length();
			numLine++;
		}
		scanner.close();
		calcRelativOccPos(result, textLength);
		
		ProcessResult animAnalysis = 
				new ProcessResult(textLength, numLine, result);
		animAnalysis.printRes();
		MusicProcessor mp = new MusicProcessor(animAnalysis.getmOccurenceInfos());
		mp.process();
		return animAnalysis; 
	}
	
	/**
	 * Read the animal lexicon for further text analysis which
	 * identifies the occurrence of animals
	 * @return A list of animal names
	 * @throws IOException
	 */
	private List<String> readLexicon() throws IOException {
		
		// Preparing to read animal lexicon
		FileReader lexReader = new FileReader(mFileLexicon);
		BufferedReader bufferedLexReader = new BufferedReader(lexReader);
		List<String> animals = new ArrayList<String>();
		String line;
		
		// Reading animal lexicon
		while ((line = bufferedLexReader.readLine())!= null) {
			if (!line.isEmpty()) {
				animals.add(line);
			}
		}
		
		// Close readers
		bufferedLexReader.close();
		lexReader.close();
		return animals;
	}
	
	/**
	 * Verify source file. Check if the file is given or if the path exists.
	 * Prints error message if needed.
	 */
	private void verifySrc() {
		// Check if a source file is given
		if(mSrcFileName == null){
			System.out.println("Error: file missing!");
			System.exit(1);
		}
		// Check the path of the file
		else {
			Path path = Paths.get(mSrcFileName);
			if (!Files.exists(path)) {
				System.out.println("Error: file path doesn't exist!");
				System.exit(1);
			}
		}
	}
	
	/**
	 * Push newly found match position to result map.
	 * @param result result map
	 * @param target target string
	 * @param matchPos match position
	 */
	private void pushMatchPos(Map<String, TargetOccurenceInfo> result, 
			String target, int matchPos) {
		
		TargetOccurenceInfo occurenceInfo;
		if (result.containsKey(target)) {
			occurenceInfo = result.get(target);
		} else {
			occurenceInfo = new TargetOccurenceInfo(target);
		}
		occurenceInfo.pushOccurenceIndexes(matchPos);
		result.put(target, occurenceInfo);
	}
	
	/**
	 * Update the percentage expression of where the match is from the whole text
	 * in the TargetOccurenceInfo object for the target.
	 * @param result
	 * @param textLength
	 */
	public void calcRelativOccPos(
			Map<String, TargetOccurenceInfo> result, int textLength) {
		for (Map.Entry<String, TargetOccurenceInfo> entry : result.entrySet()) {
			entry.getValue().calcRelativOccPos(textLength);
		}
	}
	
	
	public static void main(String[] args) throws IOException {
		TextProcessor processor1 = new TextProcessor("data/the-happy-prince.txt");
		TextProcessor processor2 = new TextProcessor("data/the-fox-and-the-crow.txt");
		processor2.process();
//		String teString = "This is just a stupid little test.".replaceAll("(?<!\\S)" + "stupid" + "(?!\\S)", "smart");
//		System.out.println(teString);
	}
	
}

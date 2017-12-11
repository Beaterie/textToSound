import java.io.BufferedReader;
import java.io.File;
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

public class TextLexProcessor {
	
	// --------------------------------------------------------
	// Members
	// --------------------------------------------------------
	
	private String mSrcFileName;
	private String mFileLexicon;
	private List<String> mLex;
	
	
	// --------------------------------------------------------
	// Constructors
	// --------------------------------------------------------
	
	public TextLexProcessor() {};
	/**
	 * Constructor.
	 * @param src source text to be analysed
	 * @param lex lexicon to use
	 */
	public TextLexProcessor(String src, String lex) {
		mSrcFileName = src;
		mFileLexicon = lex;
	};
	
	
	// --------------------------------------------------------
	// Text-processing methods
	// --------------------------------------------------------
	
	/**
	 * Process the given source text and analyze occurrence of tokens
	 * @throws IOException
	 */
	public ProcessResult process() throws IOException {
		
		verifySrc();					// Verify source file. Prints error message if needed.
		mLex = readLexicon();		// Read tokens from lexicon and store in list
		
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
			// Find all target token occurrences in line
			for (String token : mLex) {
				// Setting word boundaries to find matches of the whole word
				String pattern = "(?<!\\S)" + Pattern.quote(token) + "(?!\\S)";
				Matcher m = Pattern.compile(pattern).matcher(text);
				while (m.find()) {
					int matchPos = m.start() + textLength;
					pushMatchPos(result, token, matchPos);
				    numMatch++;
				}
			}
			textLength += text.length();
			numLine++;
		}
		scanner.close();
		updateAnalysisComputation(result, textLength);
		
		ProcessResult tokenAnalysis = 
				new ProcessResult(textLength, numLine, result);
		tokenAnalysis.printRes();
		return tokenAnalysis; 
	}
	
	/**
	 * Read the lexicon for further text analysis which
	 * identifies the occurrence of tokens
	 * @return A list of token targets
	 * @throws IOException
	 */
	private List<String> readLexicon() throws IOException {
		
		// Preparing to read target lexicon
		FileReader lexReader = new FileReader(mFileLexicon);
		BufferedReader bufferedLexReader = new BufferedReader(lexReader);
		List<String> tokens = new ArrayList<String>();
		String line;
		
		// Reading lexicon
		while ((line = bufferedLexReader.readLine())!= null) {
			if (!line.isEmpty()) {
				tokens.add(line);
			}
		}
		
		// Close readers
		bufferedLexReader.close();
		lexReader.close();
		return tokens;
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
	 * Update other information of the TargetOccurence class, including
	 * the percentage expression of where the match is from the whole text
	 * for the target and total occurences.
	 * @param result
	 * @param textLength
	 */
	public void updateAnalysisComputation(
			Map<String, TargetOccurenceInfo> result, int textLength) {
		for (Map.Entry<String, TargetOccurenceInfo> entry : result.entrySet()) {
			entry.getValue().calcRelativOccPos(textLength);
			entry.getValue().calcTotalOcc();
		}
	}
	
	
	public static void main(String[] args) throws IOException {
		TextLexProcessor processor1 = new TextLexProcessor("data/the-happy-prince.txt", "data/lexicon_animals.txt");
		TextLexProcessor processor2 = new TextLexProcessor("data/the-fox-and-the-crow.txt", "data/lexicon_animals.txt");
		processor2.process();
//		String teString = "This is just a stupid little test.".replaceAll("(?<!\\S)" + "stupid" + "(?!\\S)", "smart");
//		System.out.println(teString);
	}
	
}

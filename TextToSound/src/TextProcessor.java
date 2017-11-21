import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class TextProcessor {
	
	// --------------------------------------------------------
	// Members
	// --------------------------------------------------------
	
	private String mSrcFileName;
	private String mAnimalLexicon = "data/lexicon_animals.txt";
	private List<String> mAnimals;
	
	
	// --------------------------------------------------------
	// Constructors
	// --------------------------------------------------------
	
	public TextProcessor() {};
	public TextProcessor(String src) {
		mSrcFileName = src;
	};
	
	
	// --------------------------------------------------------
	// 
	// --------------------------------------------------------
	
	public void process() throws IOException {
		
		// Verify source file. Prints error message if needed.
		verifySrc();
		
		// Read animal lexicon and store in list
		mAnimals = readLexicon();
		
		// Read input story
		FileReader srcReader = new FileReader(mSrcFileName);
		BufferedReader bufferedSrcReader = new BufferedReader(srcReader);
		// Find animal occurrence line by line
		String line;
		while ((line = bufferedSrcReader.readLine())!= null) {	
			
		}
		
	}
	
	/**
	 * Read the animal lexicon for further text analysis which
	 * identifies the occurrence of animals
	 * @return A list of animal names
	 * @throws IOException
	 */
	private List<String> readLexicon() throws IOException {
		
		// Preparing to read animal lexicon
		FileReader lexReader = new FileReader(mAnimalLexicon);
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
	
}

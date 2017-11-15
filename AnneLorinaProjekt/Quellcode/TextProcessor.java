import java.util.*;
import java.io.*;

/**
 * Processes Text to Audio
 *
 * @author Anne Peter, Norina Marie Grosch
 * @version 1.0
 */

public class TextProcessor{
	private String filename;
	private String lexicon = "data/lexicon_german.csv";
	//private String lexicon = "data/lexicon_english.csv";
	private int n = 100;
	private int text = 0;
	private String i1 = "Piano";
	private String i2 = "Piano";
	private String i3 = "Piano";
	private Section section1 = new Section();
	private Section section2 = new Section();
	private Section section3 = new Section();
	private Section section4 = new Section();
	// store the words with theire emotion in a map
	private Map<String, Word> words = new HashMap<String, Word>();
	// melodies Mo, Me1 and Me2
	public Music m = new Music();
	public Music m1 = new Music();
	public Music m2 = new Music();
	// contains all pitches, durations and bars
	private String music = "";
	private boolean minorOrMajorKey;
	private int tempo;
	private int[] dominant;
	private Map<Integer, String> emos = new HashMap<Integer, String>();

	public TextProcessor(String file){
		filename = file;

		emos.put(2, "Anger");
		emos.put(3, "Anticipation");
		emos.put(4, "Disgust");
		emos.put(5, "Fear");
		emos.put(6, "Joy");
		emos.put(7, "Sadness");
		emos.put(8, "Surprise");
		emos.put(9, "Trust");
	}	

	public TextProcessor(String file, int methode, String lang){
		filename = file;
		text = methode;
		if(lang.equals("English")) {
			lexicon = "data/lexicon_english.csv";
		}

		emos.put(2, "Anger");
		emos.put(3, "Anticipation");
		emos.put(4, "Disgust");
		emos.put(5, "Fear");
		emos.put(6, "Joy");
		emos.put(7, "Sadness");
		emos.put(8, "Surprise");
		emos.put(9, "Trust");
	}

	public TextProcessor(String file, String lang, String ins1, String ins2, 
											 String ins3) {
		filename = file;
		if(lang.equals("English")) {
			lexicon = "data/lexicon_english.csv";
		}
		i1 = ins1;
		i2 = ins2;
		i3 = ins3;

		emos.put(2, "Anger");
		emos.put(3, "Anticipation");
		emos.put(4, "Disgust");
		emos.put(5, "Fear");
		emos.put(6, "Joy");
		emos.put(7, "Sadness");
		emos.put(8, "Surprise");
		emos.put(9, "Trust");
	}

	public TextProcessor(String file, String lang, String ins1, String ins2, 
											 String ins3, int methode) {
		filename = file;
		if(lang.equals("English")) {
			lexicon = "data/lexicon_english.csv";
		}
		i1 = ins1;
		i2 = ins2;
		i3 = ins3;

		emos.put(2, "Anger");
		emos.put(3, "Anticipation");
		emos.put(4, "Disgust");
		emos.put(5, "Fear");
		emos.put(6, "Joy");
		emos.put(7, "Sadness");
		emos.put(8, "Surprise");
		emos.put(9, "Trust");

		text = methode;
	}

	/**
	 * Processes specifyed file from text to jfugue compatible string
	 *
	 * @return String in Jfugue format with music of text
	 */
	public String process() throws FileNotFoundException, IOException{
		//Exeption handling
		if(filename == null){
			System.out.println("Error: file missing!");
			System.exit(1);
		}

		//Step 1
		//read input file and store in sections
		readInput();
		//read lexicon
		readLexicon();
		// System.out.println(words.size());

		//Step 2
		//analyse sections
		int language = 0;
		if(lexicon.equals("data/lexicon_english.csv")){
			language = 1;
		}

		switch (text){
			case 1: 
				section1.calculateDensitiesAdvanced(words);
				section2.calculateDensitiesAdvanced(words);
				section3.calculateDensitiesAdvanced(words);
				section4.calculateDensitiesAdvanced(words);
				break;
			case 2: 
				section1.calculateDensitiesAdvancedPlus(words, language);
				section2.calculateDensitiesAdvancedPlus(words, language);
				section3.calculateDensitiesAdvancedPlus(words, language);
				section4.calculateDensitiesAdvancedPlus(words, language);
				break;
			default: 
				section1.calculateDensities(words);
				section2.calculateDensities(words);
				section3.calculateDensities(words);
				section4.calculateDensities(words);	
				break;		
		}

		//Step 3
		//make melodys

		//analyse key
		int posEmo = section1.sectionDensity[0] + section2.sectionDensity[0] 
							 + section3.sectionDensity[0] + section4.sectionDensity[0];
		int negEmo = section1.sectionDensity[1] + section2.sectionDensity[1] 
							 + section3.sectionDensity[1] + section4.sectionDensity[1];

		minorOrMajorKey = (posEmo >= negEmo) ? true : false;

		// calulate ovctaves
		// joy/sadness density
		double js = (double) (section1.sectionDensity[6] + 
													section2.sectionDensity[6] + 
													section3.sectionDensity[6] + 
													section4.sectionDensity[6])
						  / (double) (section1.sectionDensity[7] + 
						  						section2.sectionDensity[7] + 
						  						section3.sectionDensity[7] +
						  						section4.sectionDensity[7]);

		js = (js > 1) ? 1 : js;

		// jsMax & jsMin are given 
		double jsMin = 0.0;
		double jsMax = 1.0;

		int ocataveM0 = 4 + (int)((js - jsMin) * (6 - 4) / (jsMax - jsMin));

		// calculate emotions for Me1 and Me2
		dominant = getDominantEmo(section1, section2, section3, section4);

		// Melody Mo
		m = new Music(section1, section2, section3, section4, words, 11, ocataveM0, 
									minorOrMajorKey, text, language);
		// Melody Me1
		m1 = new Music(section1, section2, section3, section4, words, dominant[0], 
									ocataveM0, minorOrMajorKey, text, language);
		// Melody Me2
		m2 = new Music(section1, section2, section3, section4, words, dominant[1], 
									ocataveM0, minorOrMajorKey, text, language);

		// calculate tempo
		tempo = calculateTempo(section1, section2, section3, section4);

		// set C major or minor in melody
		// happy: 0 = minor, 1 = major
		if (minorOrMajorKey == true)
			music += "KCmaj ";
		else
			music += "KCmin ";

		// add melodies to music
		music += "V0 " + "T" + tempo + " I[" + i1 +"] " + m.music;
		music += "V1 " + "T" + tempo + " I[" + i2 +"] " + m1.music;
		music += "V2 " + "T" + tempo + " I[" + i3 +"] " + m2.music;

		return music;
	}

	/**
	 * Reads in file and split sections
	 */
	public void readInput() throws FileNotFoundException{

		//read in file
		Scanner scanner = new Scanner(new File(filename), "UTF-8");
		scanner.useDelimiter("\n");
		String content = "";
		while(scanner.hasNextLine()){
			// remove numbers, special chars and multiple spaces
			content += (scanner.nextLine().replaceAll("\\W\\ßäüöÄÜÖ",
									" ").replaceAll("[0-9]", "").replaceAll(" +", " "));
		}
		content = content.toLowerCase();
		scanner.close();
		System.out.println();
		//System.out.println(content);

		//count words in file, error if < 16
		int wordCounter = content.split("\\w+").length;
		if(wordCounter < 16){
			System.out.println("Error: not enough words! " + content);
			System.exit(2);
		}
		System.out.println("Words in File: " + wordCounter);

		// subdividing content into 4 equal sections and preserve whole words
		String dividedContent = 
					 content.replaceAll("(.{0,"+(int)content.length()/4+"})\\b", "$1\n");
		//System.out.println(dividedContent);
		String[] section = dividedContent.split("\n");
		// check if divided in 5
		// if yes, append [3]
		if(section.length > 4){
			section[3] += section[4];
		}

		// split up sections into subsections
		section1.split(section[0]);
		section2.split(section[1]);
		section3.split(section[2]);
		section4.split(section[3]);
		
	}

  /**
	 * Reads in Lexicon and stores words in lexicon map
	 */
	public void readLexicon() throws FileNotFoundException, IOException{
		//read in file
		Scanner scanner = new Scanner(new File(lexicon), "UTF-8");
		scanner.useDelimiter("\n");
		//Skip header
		scanner.nextLine();
		while(scanner.hasNextLine()){
			//splitt line by tab
			String[] s = scanner.nextLine().split("\t");
			//count emoitions
			Word w = new Word();
			for(int i = 0; i < 10; i++){
				// max = (a > b) ? a : b;
				w.emotions[i] = (Integer.parseInt(s[i+1]) == 1) ? true : false;
				if(w.emotions[i])
					w.isEmotional = true;
			}
			// save how many words are part of the entry
			w.length = s[0].split(" ").length;
			//put word with related emoitions into map except string is empty
			if(!s[0].equals(""))
				words.put(s[0].toLowerCase(), w);
			
		}
		scanner.close();
	}

	/**
	 * Calculates first and second dominant emotion of the text
	 *
	 * @param s1 Section one of text, s2 Section two of text, 
	 *        s3 Section three of text, s4 Section four of text 
	 *
	 * @return integer array with first and second dominant emotion as integer
	 */
	public int[] getDominantEmo(Section s1, Section s2, Section s3, Section s4){
		int max1 = 0;
		int max2 = 0;
		int i1 = 0;
		int i2 = 0;

		// start at 2 to skip positive and negative emotions and stops at 10 to skip
		// words in total
		// and emotion words in total
		for(int i = 2; i < 10; i++){
			int tmp = (s1.sectionDensity[i] + s2.sectionDensity[i] + 
								 s3.sectionDensity[i] + s4.sectionDensity[i]);
			if(tmp > max2){
				if(tmp > max1){
					max2 = max1;
					max1 = tmp;
					i2 = i1;
					i1 = i;
				}
				else{
					max2 = tmp;
					i2 = i;
				}
			}
		}
		int[] result = {i1, i2};
		return result;
	}

	/**
	 * Calculates tempo for the music of the text (activity score)
	 *
	 * @param s1 Section one of text, s2 Section two of text, 
	 *        s3 Section three of text, s4 Section four of text 
	 *
	 * @return tempo as integer
	 */
	public int calculateTempo(Section s1, Section s2, Section s3, Section s4){
		// calculate activity = active - passive emotion densitys
		// active emotions (anger + joy)
		double angerDens = (double)(s1.sectionDensity[2] + s2.sectionDensity[2] + 
																s3.sectionDensity[2] + s4.sectionDensity[2])/
											 (double)(s1.sectionDensity[10] + s2.sectionDensity[10] + 
											 					s3.sectionDensity[10] + s4.sectionDensity[10]);
		double joyDens = (double)(s1.sectionDensity[6] + s2.sectionDensity[6] + 
																s3.sectionDensity[6] + s4.sectionDensity[6])/
										 (double)(s1.sectionDensity[10] + s2.sectionDensity[10] + 
										 					s3.sectionDensity[10] + s4.sectionDensity[10]);
		double active = (angerDens + joyDens) / 2;

		// passive emotions (sadness)
		double passive = (double)(s1.sectionDensity[7] + s2.sectionDensity[7] + 
															s3.sectionDensity[7] + s4.sectionDensity[7])/
	 									 (double)(s1.sectionDensity[10] + s2.sectionDensity[10] + 
	 									 					s3.sectionDensity[10] + s4.sectionDensity[10]);

		//activity 
	  double activity = (active > passive) ? (active - passive) : 
	  									(passive - active);

		double actMin = -0.002;
		double actMax = 0.017;

		activity = (activity > actMax) ? (actMax) : (activity);
		activity = (activity < actMin) ? (actMin) : (activity);


		int tempo = 40 + (int)(((activity - actMin) * 
								(180 - 40))/(actMax - actMin));

		return tempo;
	}

	public Map<String, Word> getLexicon(){
		return words;
	}

	public String getKey(){
		if (minorOrMajorKey == true){
			return "C Major";
		}
		return "C Minor";
	}

	public String getDominant(){
		return emos.get(dominant[0]);
	}

	public String getDominantTwo(){
		return emos.get(dominant[1]);
	}

	public int getTempo(){
		return tempo;
	}

	public int getTextAnalysis(){
		return text;
	}
}
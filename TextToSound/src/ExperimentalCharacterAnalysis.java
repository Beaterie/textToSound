import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class ExperimentalCharacterAnalysis {
	
	// --------------------------------------------------------
	// Members
	// --------------------------------------------------------
	
	private StringBuilder mText = new StringBuilder();
	private String[] mQuoteSpeakers;
	private List<Integer> mQuoteIndexes = new ArrayList<Integer>();
	private List<StringBuilder> mQuoteList = new ArrayList<StringBuilder>();
	private Map<String, StringBuilder> mQuoteMap = new HashMap<String, StringBuilder>();
	
	private String mFirstPerson = "";
	private String mThirdPerson = "";
	private Map<String, String> mGenderMap = new HashMap<String, String>();
	
	
	// --------------------------------------------------------
	// Primary Methods
	// --------------------------------------------------------
	
	/**
	 * Check if the given number is even.
	 */
	private boolean isEven(int x) {
		return x%2 == 0;
	}
	
	/**
	 * Return the bigger number of the 2 given numbers.
	 */
	private int max(int a, int b) {
		int x = a>b ? a:b;
		return x;
	}
	
	/**
	 * Return the smaller number of the 2 given numbers.
	 */
	private int min(int a, int b) {
		int x = a<b ? a:b;
		return x;
	}
	
	
	// --------------------------------------------------------
	// Preparation for analysis
	// --------------------------------------------------------
	
	/**
	 * Read the text and store the text in the member mText.
	 * @param fileName
	 * @throws FileNotFoundException
	 */
	private void readText(String fileName) throws FileNotFoundException {
		Scanner scanner = new Scanner(new File(fileName), "UTF-8");
		scanner.useDelimiter("\n");
		
		while(scanner.hasNextLine()){
			String line = scanner.nextLine();
			mText.append(line + " ");
		}
		scanner.close();
	}
	
	/**
	 * Find the quotes in the stored source text and stock the indexes of the quotes,
	 * including the start and end indexes, in a list.
	 */
	private void findQuotes() {
		Pattern p = Pattern.compile("‘([^‘]*|[^’]*)’|“([^“]*|[^”]*)”|\"([^\"]*|[^\"]*)\"");
		Matcher m = p.matcher(mText);
		while (m.find()) {
			mQuoteIndexes.add(m.start());
			mQuoteIndexes.add(m.end());
		}
		
		// Enforce the size of mQuoteSpeaker to the same
		mQuoteSpeakers = new String[mQuoteIndexes.size()];
	}
	
	/**
	 * Prepare the members mFirstperson and mThirdperson from TextLexProcessor results.
	 * The found target and their first-person pronouns will be stored in mFirstperson,
	 * and their third-person pronouns like his,her etc. will be stored in mThirdperson.
	 * <BR><BR>
	 * This step should be then executed, when a result from TextLexProcessor is ready.
	 * @param nameList 
	 */
	private void extractPeopleFromTxtLexProc(ProcessedResult result, List<String> nameList) {
		fillFirstThirdperson(result, nameList);
		cleanFirstThirdperson();
	}
	
	private void fillFirstThirdperson(ProcessedResult result, List<String> nameList) {
		// Adding each target and their pronouns to the members.
		for (Map.Entry<String, TargetInfo> entry : result.getOccurenceInfos().entrySet()) {
			String target = entry.getKey();
			String pronoun = entry.getValue().getTargetPronoun();
			fillGenderMap(target, pronoun);
			
			if (pronoun != null) {
				mFirstPerson += ("|" + target + "||" + pronoun + "|");
				
				if (pronoun.equals("he")) {
					mThirdPerson += ("|his||him|");
				} else if (pronoun.equals("she")) {
					mThirdPerson += ("|her|");
				}
//				} else if (pronoun.equals("it")) {
//					mThirdPerson += ("|its|");
//				}
			}
		}
		
		for (String name : nameList) {
			mFirstPerson += ("|" + name + "|");
		}
	}
	
	/**
	 * Store the persons and their pronouns into the member mGenderMap.
	 */
	private void fillGenderMap(String person, String pronoun) {
		mGenderMap.put(person, pronoun);
	}
	
	
	private void cleanFirstThirdperson() {
		// Remove excessive entries from the members.
		if (mFirstPerson.indexOf("|he|") != -1) {	// If "he" already exists
			mFirstPerson = mFirstPerson.replaceAll("\\|he\\|", "|") + "|he|";
			mThirdPerson = mThirdPerson.replaceAll("\\|(his\\|)", "") + "|his|";
			mThirdPerson = mThirdPerson.replaceAll("\\|(him\\|)", "") + "|him|";
		}
		if (mFirstPerson.indexOf("|she|") != -1) {
			mFirstPerson = mFirstPerson.replaceAll("\\|she\\|", "|") + "|she|";
			mThirdPerson = mThirdPerson.replaceAll("\\|her\\|", "") + "|her|";
		}
		if (mFirstPerson.indexOf("|it|") != -1) {
			mFirstPerson = mFirstPerson.replaceAll("\\|it\\|", "|") + "|it|";
			mThirdPerson = mThirdPerson.replaceAll("\\|its\\|", "") + "|its|";
		}
		// Remove excessive "|"
		mFirstPerson = mFirstPerson.replaceAll("\\|+", "|");
		mThirdPerson = mThirdPerson.replaceAll("\\|+", "|");
		mFirstPerson = mFirstPerson.substring(1, mFirstPerson.length()-1);
		mThirdPerson = mThirdPerson.substring(1, mThirdPerson.length()-1);
		
		System.out.println(mFirstPerson + "\t" + mThirdPerson);
	}
	
//	private static String discardSubConjunctions(String substring) {
//		return substring;
//	}
	
	
	// --------------------------------------------------------
	// Analysis methods
	// --------------------------------------------------------
	
	/**
	 * Determine the speaker of the quote by analyzing the text before or after the quote.
	 * @param substring
	 * @return
	 */
	private String determineSpeaker(String substring) {
		// If someone is spoken to, he won't be the subject of the sentence.
		Pattern preposition = Pattern.compile("\\W(to|at)\\W(\\w*\\W\\w*)\\W", Pattern.CASE_INSENSITIVE);
		Matcher m = preposition.matcher(substring);
		String subject = "", object = "";
		while (m.find()) {
			object = m.group(2);
//			System.out.print("Object: " + object + "\n");
		}
		
		String pattern = "\\W(" + mFirstPerson + ")\\W";
		Pattern nouns = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE);
		Matcher m1 = nouns.matcher(substring);
		int count = 0;
		// Return the first person that is not a known object in the sentence.
		while (m1.find() && count < 1) {
			count++;
			String noun = m1.group(1);
			boolean nounIsObject = (object.indexOf(noun)!=-1);
			if (!nounIsObject) {
				subject = noun;
//				System.out.print("Subject: " + noun + "\n");
			}
		}
		return subject;
	}
	
	/**
	 * After determining the speaker, store the speaker in quotes before/after or not 
	 * store at all according to properties of the sentence analyzed. These are properties
	 * showing where the whole sentence starts/ends according to punctuation and cases,
	 * deciding if the speaker already spoken or is about to speak (or both).
	 * @param substring
	 * @param subject
	 * @param arrIndex
	 */
	private void saveQuoteSpeaker(String substring, String subject, int arrIndex) {
		substring = substring.trim();
		int numQuotes = mQuoteIndexes.size();
		if (substring.length() > 0) {
			
			if (arrIndex == 0) {
				mQuoteSpeakers[0] = subject;
				mQuoteSpeakers[1] = subject;
			}
			else if (arrIndex == numQuotes-1) {
				if (mQuoteSpeakers[numQuotes-2] == null) {
					mQuoteSpeakers[numQuotes-2] = subject;
					mQuoteSpeakers[numQuotes-1] = subject;
				}
			}
			else {
				char firstChar = substring.charAt(0);
				String lastChar = Character.toString(substring.charAt(substring.length()-1));
				if (Character.isLowerCase(firstChar)) {
					if (mQuoteSpeakers[max(arrIndex-1, 0)] == null) {
						mQuoteSpeakers[max(arrIndex-1, 0)] = subject;
						mQuoteSpeakers[arrIndex] =  subject;
					}
				}
				if ((lastChar.equals(",") | lastChar.equals(":") | lastChar.equals(";"))) {
					if (mQuoteSpeakers[min(arrIndex+1, numQuotes)] == null) {
						mQuoteSpeakers[min(arrIndex+1, numQuotes)] = subject;
						mQuoteSpeakers[min(arrIndex+2, numQuotes)] = subject;
					}
				}
			}
		}
	}
	
	/**
	 * Determine the speaker of the quotes
	 */
	private void determineSpeakersOfQuotes() {
		for (int i = 0; i < mQuoteIndexes.size(); i++) {
			int txtIndex = mQuoteIndexes.get(i);
			String subject = null, substring = null;
			// Search in the previous 50 characters before the first quote
			if (i == 0) {
				substring = mText.substring(max(0, txtIndex - 50), txtIndex).trim();
				System.out.println("From -1 to 0: \n" + substring + "\n");
				String lastPunctuation = Character.toString(substring.charAt(substring.length()-1));
				// If the preceding text isn't a part of the whole sentence including the quote,
				// disregard. Otherwise determine the speaker.
				if (lastPunctuation.equals(":") | lastPunctuation.equals(",")) {
					subject = determineSpeaker(substring);
					saveQuoteSpeaker(substring, subject, i);
				}
			}
			// Search in the following 50 characters after the last quote
			if (i == mQuoteIndexes.size()-1) {
				substring = mText.substring(txtIndex, min(txtIndex + 50, mText.length()-1)).trim();
				System.out.println("From " + i + "\n" + substring);
				if (substring != null && substring.length() > 0) {
					char firstChar = substring.charAt(0);
					// If the following text isn't a part of the previous sentence, disregard.
					if (Character.isLowerCase(firstChar)) {
						subject = determineSpeaker(substring);
						saveQuoteSpeaker(substring, subject, i);
					}
					
					System.out.println();
					for (String index : mQuoteSpeakers) {
						System.out.print(index + ", ");
					}
				}
				return;
			}
			
			int next = mQuoteIndexes.get(i+1);
			substring = mText.substring(txtIndex, next);
			
			System.out.println("From " + i + "\n" + substring);
			
			// Search the span of quotes
			if (isEven(i)) {	// If i is even, it indicates this index is the start pos of a quote
				// Save the quote into mQuoteList
				mQuoteList.add(new StringBuilder(substring));
			}
			// Search outside of quotes
			else {
				subject = determineSpeaker(substring);
				saveQuoteSpeaker(substring, subject, i);
			}
			System.out.println("Subject: " + subject + "\n");
		}
	}
	
	private void updateEntryQuoteMap(String name, int i) {
		// If the entry already exists, add quote to StringBuilder
		StringBuilder newQuote;
		if (mQuoteMap.containsKey(name)) {
			newQuote = mQuoteMap.get(name).append(mQuoteList.get(i/2));
		} else {	// Otherwise, add new entry
			newQuote = new StringBuilder(mQuoteList.get(i/2));
		}
		mQuoteMap.put(name, newQuote);
	}
	
	/**
	 * Map the quotes to their speakers with String speaker as key and a StringBuilder of 
	 * quotes as value (The map is stored as the member mQuoteMap).
	 * This method also finds out the person a pronoun is pointed to, if a quote's speaker 
	 * is a pronoun, and assigns the quote to the found character in the map.
	 */
	private void mapQuoteToSpeaker() {
		// Assign the quotes directly from the characters to the corresponding characters
		for (int i = 0; i < mQuoteSpeakers.length; i = i+2) {
			String name = mQuoteSpeakers[i];
			if (name == null) {
				determineSpeakerAdvanced(i);
			} else {
				switch (name.toLowerCase()) {
				case "he":
					// Find the male person with closest distance before "he"
					determinePronounPerson(i, "he");
					break;
				case "she":
					// Find the female person with closest distance before "she"
					determinePronounPerson(i, "she");
					break;
				case "they":
					// Find the plural people with closest distance before "they"
					determinePronounPerson(i, "they");
					break;
				case "":
					updateEntryQuoteMap(name, i);
					break;
				default:
					// The real names or titles of the people are stored directly into the map
					updateEntryQuoteMap(name, i);
					break;
				}
			}
		}
	}
	
	/**
	 * Determine the speaker from the text before. The last talking person should be the other 
	 * person in the conversation, which means the person before the last talking person should
	 * be what we are looking for.
	 * <BR><BR>
	 * This method should be used only in mapQuoteToSpeaker(), and is only meant to find 
	 * speaker, where determineSpeaker() returned null for.
	 */
	private void determineSpeakerAdvanced(int i) {
		for (int j = i-4; j > -1; j = j-2) {
			String prevPerson = mQuoteSpeakers[j+2];
			String targetPerson = mQuoteSpeakers[j];
			// If the person is found, update the quote map with the found key person
			// And update the mQuoteSpeakers list
			if (targetPerson!= null && prevPerson!=null && !targetPerson.equals(prevPerson)) {
				updateEntryQuoteMap(targetPerson, i);
				mQuoteSpeakers[i] = targetPerson;
				mQuoteSpeakers[i+1] = targetPerson;
				break;
			}
		}
	}
	
	/**
	 * Determine whom the pronoun points to. After finding the name or title of the person,
	 * update the mQuoteMap by updating the corresponding entry.
	 * <BR><BR>
	 * This method should be used only in the for-loop in mapQuoteToSpeaker().
	 * @param i The i-th run from the outer for-loop. Also indicates which quote speaker 
	 * is being determined from mQuoteSpeakers
	 * @param targetPronoun The pronoun being determined
	 */
	private void determinePronounPerson(int i, String targetPronoun) {
		// If the pronoun is plural, find plural people occurring in this story
		// (!!!ATTENTION: right now, there's only children that is plural)
		if (targetPronoun.equals("they")) {
			for (Entry<String, String> entry : mGenderMap.entrySet()) {
				if (entry.getValue().equals("they")) {
					String name = entry.getKey();
					updateEntryQuoteMap(name, i);
					mQuoteSpeakers[i] = name;
					mQuoteSpeakers[i+1] = name;
					return;
				}
			}
		}
		for (int j = i-2; j > -1; j = j-2) {
			String prevPerson = mQuoteSpeakers[j];
			if (prevPerson!=null && !prevPerson.isEmpty()) {
				String pronoun = mGenderMap.get(prevPerson);
				// If a match is found, update the quote map with the found key person
				// And update the mQuoteSpeakers list
				if (pronoun!=null && !pronoun.isEmpty() && pronoun.equals(targetPronoun)) {
					String name = prevPerson;
					updateEntryQuoteMap(name, i);
					mQuoteSpeakers[i] = name;
					mQuoteSpeakers[i+1] = name;
					break;
				}
			}
		}
	}
	
	/**
	 * Analyse the characters with the emotional lexicon based on their quotes.
	 * This function's goal is to find out which emotions the characters are most related to.
	 * @throws IOException 
	 */
	private Map<String, EmotionResult> characterAnalysis(NEREmotionProcessor NERprocessor) throws IOException {
		Map<String, EmotionResult> characterEmotionRes = new HashMap<>();
		for (Entry<String, StringBuilder> entry : mQuoteMap.entrySet()) {
			String person = entry.getKey();
			StringBuilder quotes = entry.getValue();
			EmotionResult EmotionResults = NERprocessor.AssessEmotion(quotes.toString(), 1);
			characterEmotionRes.put(person, EmotionResults);
			
			System.out.println(person);
			EmotionResults.printResult();
			System.out.println();
		}
		return characterEmotionRes;
	}
	
	
	// --------------------------------------------------------
	// The main function for testing
	// --------------------------------------------------------
	
	public static Map<String, EmotionResult> main(String arg) throws IOException {
		
		//String sourceFile = "data/pride-and-prejudice-test.txt";
		String sourceFile = arg;
//		String sourceFile = "data/test-character.txt";
		TextLexProcessor proc = new TextLexProcessor(sourceFile, "data/lexicon_people_and_animal.csv");
		ProcessedResult result = proc.process();
		
		NEREmotionProcessor NERprocessor1 = new NEREmotionProcessor(sourceFile, 10);
		List<String> nameList = NERprocessor1.nameDetection();
		
		
		ExperimentalCharacterAnalysis exp = new ExperimentalCharacterAnalysis();
		exp.readText(sourceFile);
		exp.findQuotes();
		exp.extractPeopleFromTxtLexProc(result, nameList);
		exp.determineSpeakersOfQuotes();
		exp.mapQuoteToSpeaker();
		
		System.out.println("\n" + exp.mQuoteMap.keySet());
		for (String index : exp.mQuoteSpeakers) {
			System.out.print(index + ", ");
		}System.out.println();
		
//		Map<String, EmotionResult> characterEmoRes = exp.characterAnalysis(NERprocessor1);
		return exp.characterAnalysis(NERprocessor1);
		
		
		// Presentation
//		System.out.println("\n============================================================");
//		System.out.println("============================================================\n");
//		for (Entry<String, StringBuilder> entry : exp.mQuoteMap.entrySet()) {
//			System.out.println(entry.getKey());
//			System.out.println("[" + entry.getValue() + "]\n");
//		}
	}
}

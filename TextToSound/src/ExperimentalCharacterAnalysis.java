import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class ExperimentalCharacterAnalysis {
	
	private StringBuilder mText = new StringBuilder();
	private List<Integer> mQuoteIndexes = new ArrayList<Integer>();
	private String[] mQuoteSpeakers;
	private Map<String, StringBuilder> mQuoteMap = new HashMap<String, StringBuilder>();
	
	private String mFirstPerson = "";
	private String mThirdPerson = "";
	
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
		Pattern p = Pattern.compile("‘([^‘]*|[^’]*)’|\"([^\"]*|[^\"]*)\"");
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
			
			if (pronoun != null) {
				mFirstPerson += ("|" + target + "||" + pronoun + "|");
				
				if (pronoun.equals("he")) {
					mThirdPerson += ("|his||him|");
				} else if (pronoun.equals("she")) {
					mThirdPerson += ("|her|");
				} else if (pronoun.equals("it")) {
					mThirdPerson += ("|its|");
				}
			}
		}
		
		for (String name : nameList) {
			mFirstPerson += ("|" + name + "|");
		}
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
				return;
			}
			
			int next = mQuoteIndexes.get(i+1);
			substring = mText.substring(txtIndex, next);
			
			System.out.println("From " + i + "\n" + substring);
			
			// Search the span of quotes
			if (isEven(i)) {	// If i is even, it indicates this index is the start pos of a quote
				characterAnalysis();
			}
			// Search outside of quotes
			else {
				subject = determineSpeaker(substring);
				saveQuoteSpeaker(substring, subject, i);
				characterAnalysis(); 
			}
			System.out.println("Subject: " + subject + "\n");
		}
		System.out.println(mQuoteSpeakers.toString());
	}
	
	private void mapQuoteToSpeaker() {
		
	}
	
	/**
	 * Analyse the characters with the emotional lexicon based on their quotes.
	 * This function's goal is to find out which emotions the characters are most related to.
	 */
	private void characterAnalysis() {}
	
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
	
	
	public static void main(String[] args) throws IOException {
		
		String sourceFile = "data/little-red-riding-hood.txt";
		TextLexProcessor proc = new TextLexProcessor(sourceFile, "data/lexicon_people_and_animal.csv");
		ProcessedResult result = proc.process();
		
		NEREmotionProcessor NERprocessor1 = new NEREmotionProcessor(sourceFile, 10);
		List<String> nameList = NERprocessor1.nameDetection();
		
		
		ExperimentalCharacterAnalysis exp = new ExperimentalCharacterAnalysis();
		exp.readText("data/little-red-riding-hood.txt");
		exp.findQuotes();
		exp.extractPeopleFromTxtLexProc(result, nameList);
		exp.determineSpeakersOfQuotes();
		
		exp.mapQuoteToSpeaker();
		
		exp.characterAnalysis();
	}
}

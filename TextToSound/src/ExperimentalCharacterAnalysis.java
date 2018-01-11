import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.omg.PortableInterceptor.INACTIVE;

public class ExperimentalCharacterAnalysis {
	
	private static StringBuilder mText = new StringBuilder();
	private static List<Integer> mQuoteIndexes = new ArrayList<Integer>();
	private static List<String> mQuoteSpeakers = new ArrayList<String>();
	
	private static void readText(String fileName) throws FileNotFoundException {
		Scanner scanner = new Scanner(new File(fileName), "UTF-8");
		scanner.useDelimiter("\n");
		
		while(scanner.hasNextLine()){
			String line = scanner.nextLine();
			mText.append(line + " ");
		}
		scanner.close();
	}
	
	private static void findQuotes() {
		Pattern p = Pattern.compile("‘([^‘]*|[^’]*)’");
		Matcher m = p.matcher(mText);
		while (m.find()) {
			mQuoteIndexes.add(m.start());
			mQuoteIndexes.add(m.end());
		}
		
		// Enforce the size of mQuoteSpeaker to the same
		mQuoteSpeakers = new ArrayList<String>(mQuoteIndexes.size());
	}
	
	private static void searchPersonWholeTxt() {
		Pattern man = Pattern.compile("\\W(he|his|him|man|husband)\\W", Pattern.CASE_INSENSITIVE);
		Matcher matcher = man.matcher(mText);
		while (matcher.find()) {
			System.out.print(matcher.start() + " " + matcher.group(1) + "  ");
		}
		System.out.println();

		Pattern wife = Pattern.compile("\\W(she|her|wife|woman)\\W", Pattern.CASE_INSENSITIVE);
		Matcher mtch = wife.matcher(mText);
		while (mtch.find()) {
			System.out.print(mtch.start() + " " + mtch.group(1) + "  ");
		}
	}
	
	private static void searchPerson(String text) {
		// \\W stands for non-alphabetic character
		Pattern man = Pattern.compile("\\W(he|his|him|man|husband)\\W", Pattern.CASE_INSENSITIVE);
		Matcher matcher = man.matcher(text);
		while (matcher.find()) {
			System.out.print(matcher.group(1) + "  ");
		}

		Pattern wife = Pattern.compile("\\W(she|her|wife|woman)\\W", Pattern.CASE_INSENSITIVE);
		Matcher mtch = wife.matcher(text);
		while (mtch.find()) {
			System.out.print(mtch.group(1) + "  ");
		}
	}
	
	private static void findNouns(String substring) {
		// \\W stands for non-alphabetic character
		Pattern noun = Pattern.compile("\\W(he|man|husband|she|wife|woman)\\W", Pattern.CASE_INSENSITIVE);
		Matcher m1 = noun.matcher(substring);
		while (m1.find()) {
			System.out.print("Noun: " + m1.group(1) + "\n");
		}
	}
	
	private static void findPronouns(String substring) {
		Pattern pronoun = Pattern.compile("\\W(her|his|him)\\W", Pattern.CASE_INSENSITIVE);
		Matcher m2 = pronoun.matcher(substring);
		while (m2.find()) {
			System.out.print("Pronoun: " + m2.group(1) + "\n");
		}
	}
	
	private static void determinOtherSpeaker() {}
	
	private static String determineSpeaker(String substring) {
		Pattern preposition = Pattern.compile("\\W(to)\\W(\\w*\\W\\w*)\\W", Pattern.CASE_INSENSITIVE);
		Matcher m = preposition.matcher(substring);
		String subject = "", object = "";
		while (m.find()) {
			object = m.group(2);
//			System.out.print("Object: " + object + "\n");
		}
		
		Pattern nouns = Pattern.compile("\\W(he|man|husband|she|wife|woman)\\W", Pattern.CASE_INSENSITIVE);
		Matcher m1 = nouns.matcher(substring);
		while (m1.find()) {
			String noun = m1.group(1);
			boolean nounIsObject = (object.indexOf(noun)!=-1);
			if (!nounIsObject) {
				subject = noun;
				System.out.print("Subject: " + noun + "\n");
			}
		}
		return subject;
	}
	
	private static void saveQuoteSpeaker(String substring, String subject, int arrIndex) {
		substring = substring.trim();
		int numQuotes = mQuoteIndexes.size();
		if (substring.length() > 0) {
			
			if (arrIndex == 0) {
				mQuoteSpeakers.add(0, subject);
				mQuoteSpeakers.add(1, subject);
			}
			else if (arrIndex == numQuotes-1) {
				try {
					mQuoteSpeakers.get(numQuotes-2);
				} catch (Exception e) {
					mQuoteSpeakers.add(numQuotes-2, subject);
					mQuoteSpeakers.add(numQuotes-1, subject);
				}
			}
			else {
				char firstChar = substring.charAt(0);
				String lastChar = Character.toString(substring.charAt(substring.length()-1));
				if (Character.isLowerCase(firstChar)) {
					try {
						mQuoteSpeakers.get(max(arrIndex-1, 0));
					} catch (Exception e) {
						mQuoteSpeakers.add(max(arrIndex-1, 0), subject);
						mQuoteSpeakers.add(arrIndex, subject);
					}
				}
				if ((lastChar.equals(",") | lastChar.equals(":"))) {
					try {
						mQuoteSpeakers.get(min(arrIndex+1, numQuotes));
					} catch (Exception e) {
						mQuoteSpeakers.add(min(arrIndex+1, numQuotes), subject);
						mQuoteSpeakers.add(min(arrIndex+2, numQuotes), subject);
					}
				}
				mQuoteSpeakers.set(0, subject);
			}
		}
	}
	
	private static void characterAnalysis() {}
	
	private static boolean isEven(int x) {
		return x%2 == 0;
	}
	
	private static int max(int a, int b) {
		int x = a>b ? a:b;
		return x;
	}
	
	private static int min(int a, int b) {
		int x = a<b ? a:b;
		return x;
	}
	
	public static void main(String[] args) throws FileNotFoundException {
		
		readText("data/test-character.txt");
		
		findQuotes();
		
		for (int i = 0; i < mQuoteIndexes.size(); i++) {
			int txtIndex = mQuoteIndexes.get(i);
			String subject = null, substring = null;
			// Search in the previous 50 characters before the first quote
			if (i == 0) {
				substring = mText.substring(max(0, txtIndex - 50), txtIndex).trim();
				System.out.println("\n" + substring + "\n");
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
				System.out.println("\n" + substring + "\n");
				char firstChar = substring.charAt(0);
				// If the following text isn't a part of the previous sentence, disregard.
				if (Character.isLowerCase(firstChar)) {
					subject = determineSpeaker(substring);
					saveQuoteSpeaker(substring, subject, i);
				}
				

				System.out.print(subject + " returned. \n");
				System.out.println(mQuoteSpeakers.toString());
				return;
			}
			
			int next = mQuoteIndexes.get(i+1);
			substring = mText.substring(txtIndex, next);
			
			System.out.println(mQuoteIndexes.get(i));
			System.out.println("\n" + substring + "\n");
			
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

			System.out.print(subject + " returned. \n");
			System.out.println(mQuoteSpeakers.toString());
		}
		
	}
}

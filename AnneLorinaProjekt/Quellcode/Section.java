import java.util.*;
import java.io.*;

public class Section {
	// Class for a section and the subsections
	String section = "";
	String subsect1 = ""; 
	String subsect2 = "";
	String subsect3 = "";
	String subsect4 = "";

	// Positiv,Negativ,Wut,Erwartung,Abneigung,Angst,Freude,Trauer,Überraschung,
	// Vertrauen, words in total, emotion words in total
	int[] sectionDensity = new int[12];
	int[] subsect1Density;
	int[] subsect2Density;
	int[] subsect3Density;
	int[] subsect4Density;

	/**
	 * Splits section into four equal subsections
	 *
	 * @param s String of section
	 */
	public void split(String s){
		section = s;
		// devide section into equal parts
		String[] sub = s.replaceAll("(.{0,"+(int)s.length()/4+"})\\b", 
																"$1\n").split("\n");
		subsect1 = sub[0];
		subsect2 = sub[1];
		subsect3 = sub[2];
		subsect4 = sub[3];

		// if s is divided in 5 parts
		if(sub.length > 4){
			subsect4 += sub[4];
		}
	}

	/**
	 * Calls countWords to calculate densitys of the different emotions of the 
	 * subsections (only one word per search) and store them in the global 
	 * variables
	 *
	 * @param words Lexicon as a map
	 */
	public void calculateDensities(Map<String, Word> words){
		subsect1Density = countWords(words, subsect1);
		subsect2Density = countWords(words, subsect2);
		subsect3Density = countWords(words, subsect3);
		subsect4Density = countWords(words, subsect4);

		for(int i = 0; i < 12; i++){
			sectionDensity[i] = subsect1Density[i] + subsect2Density[i] + 
													subsect3Density[i] + subsect4Density[i];
		}
	}

	/**
	 * Counts emotion words of the subsection
	 *
	 * @param words Lexicon as a map, s Subsection as Sting
	 *
	 * @return Integer array with emotions (positiv, negative, anger, 
	 *				 anticipation, disgust, fear, joy, sadness, surprise, trust, 
	 *				 words in total, words with emotions in total)
	 */
	public int[] countWords(Map<String, Word> words, String s){
		String[] tmp = s.split(" ");
		int[] counter = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}; //12 elements

		// for all words in s
		for(int i = 0; i < tmp.length; i++){

			//count words in total
			counter[10]++;

			//search for word in map
			Word w = words.get(tmp[i]);
			
			// add emotions of word to counter if word has emotions
			if((w != null) && w.isEmotional ){

				for(int j = 0; j < 10; j++){
					if(w.emotions[j]){
						counter[j]++;
					}
				}
				
				// counter of emotion words
				counter[11]++;
			}

		}

		return counter;
	}

	/**
	 * Calls countWordsAdvanced to calculate densitys of the different emotions 
	 * of the subsections (up to four words per search) and store them in the 
	 * global variables
	 *
	 * @param words Lexicon as a map
	 */
	public void calculateDensitiesAdvanced(Map<String, Word> words){
		subsect1Density = countWordsAdvanced(words, subsect1);
		subsect2Density = countWordsAdvanced(words, subsect2);
		subsect3Density = countWordsAdvanced(words, subsect3);
		subsect4Density = countWordsAdvanced(words, subsect4);

		for(int i = 0; i < 12; i++){
			sectionDensity[i] = subsect1Density[i] + subsect2Density[i] + 
													subsect3Density[i] + subsect4Density[i];
			
		}
	}

	/**
	 * Counts emotion words of the subsection, checks up to four words at once
	 *
	 * @param words Lexicon as a map, s Subsection as Sting
	 *
	 * @return Integer array with emotions (positiv, negative, anger, 
	 *				 anticipation, disgust, fear, joy, sadness, surprise, trust, 
	 *         words in total, words with emotions in total)
	 */
	public int[] countWordsAdvanced(Map<String, Word> words, String s){
		String[] tmp = s.split(" ");
		int[] counter = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}; //12 elements

		// for all words in s
		for(int i = 0; i < tmp.length; i++){

			// count words in total
			counter[10]++;

			// search for word in map
			// take length of words into account (next 3 words)
			String string = tmp[i];
			Word w = null;
			for(int j = 1; (j < 4) && ((i+j) < tmp.length); j++){

				Word tmpW = words.get(string);

				// searchin the longest match
				if (tmpW != null){
					w = tmpW;
				}

				string += " " + tmp[i+j];
			}

			if(w != null)
				i += w.length - 1;
			
			// add emotions of word to counter if word has emotions
			if((w != null) && w.isEmotional ){

				for(int j = 0; j < 10; j++){
					if(w.emotions[j]){
						counter[j]++;
					}
				}
				
				// counter of emotion words
				counter[11]++;
			}

		}

		return counter;
	}

	/**
	 * Calls countWordsAdvancedPlus to calculate densitys of the different 
	 * emotions of the subsections (up to four words per search and negations) 
 	 * and store them in the global variables
	 *
	 * @param words Lexicon as a map
	 */
	public void calculateDensitiesAdvancedPlus(Map<String, Word> words, 
																						 int language){
		subsect1Density = countWordsAdvancedPlus(words, subsect1, language);
		subsect2Density = countWordsAdvancedPlus(words, subsect2, language);
		subsect3Density = countWordsAdvancedPlus(words, subsect3, language);
		subsect4Density = countWordsAdvancedPlus(words, subsect4, language);

		for(int i = 0; i < 12; i++){
			sectionDensity[i] = subsect1Density[i] + subsect2Density[i] + 
													subsect3Density[i] + subsect4Density[i];
			
		}
	}

	/**
	 * Counts emotion words of the subsection, checks up to four words at once and
	 * regards negations
	 *
	 * @param words Lexicon as a map, s Subsection as Sting
	 *
	 * @return Integer array with emotions (positiv, negative, anger, 
	 * 				 anticipation, disgust, fear, joy, sadness, surprise, trust, 
	 *				 words in total, words with emotions in total)
	 */
	public int[] countWordsAdvancedPlus(Map<String, Word> words, String s, 
																			int language){
		String[] tmp = s.split(" ");
		int[] counter = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}; //12 elements

		// for all words in s
		for(int i = 0; i < tmp.length; i++){

			// count words in total
			counter[10]++;

			// search for word in map
			// take length of words into account (next 3 words)
			String string = tmp[i];
			Word w = null;
			for(int j = 1; (j < 4) && ((i+j) < tmp.length); j++){

				Word tmpW = words.get(string);

				// searchin the longest match
				if (tmpW != null){
					w = tmpW;
				}

				string += " " + tmp[i+j];
			}

			// if more than one word is selected, skip them for i
			if(w != null)
				i += w.length - 1;
			
			// add emotions of word to counter if word has emotions
			if((w != null) && w.isEmotional ){

				for(int j = 0; j < 10; j++){
					if(w.emotions[j]){
						counter[j]++;
					}
				}
				
				// counter of emotion words
				counter[11]++;
			}
			
			if((w != null) && (i-w.length > 0)){
				if(language == 0)
					counter = checkNegativeGer(tmp[i-w.length], counter, w);
				else
					counter = checkNegativeEn(tmp[i-w.length], counter, w);
			}
		}

		return counter;
	}

	/**
	 * Checks if string contains a german negation and changes word from positiv 
	 * to negativ or vice verca 
	 *
	 * @param s String to be checked, counter Integer array of counted emotions, 
	 *				w word in the string
	 *
	 * @return Integer array of counted emotions
	 */
	private int[] checkNegativeGer(String s, int[] counter, Word w){

		String[] negatives = {"kein", "keine", "keinen", "keinem", "keiner", 
													"keines", "nicht"};
		for(int i = 0; i < negatives.length; i++){

			if(s.equals(negatives[i])){

				if(w.emotions[0]){
					counter[0]--;
					counter[1]++;
				}
				else if (w.emotions[1]){
					counter[0]++;
					counter[1]--;								
				}
				break;
			}
		}

		return counter;
	}	

	/**
	 * Checks if string contains an english negation and changes word from positiv
	 * to negativ or vice verca 
	 *
	 * @param s String to be checked, counter Integer array of counted emotions, 
	 * 				w word in the string
	 *
	 * @return Integer array of counted emotions
	 */
	private int[] checkNegativeEn(String s, int[] counter, Word w){

		// ' is eliminated in the text analysis before
		String[] negatives = {"not", "none", "dont", "doesnt", "didnt", "cant", 
													"couldnt", "wont", "wasnt", "werent", "arent", "isnt",
													"shouldnt", "neednt", "musnt", "havent", "hasnt", 
													"hadnt"};

		for(int i = 0; i < negatives.length; i++){

			if(s.equals(negatives[i])){

				if(w.emotions[0]){
					counter[0]--;
					counter[1]++;
				}
				else if (w.emotions[1]){
					counter[0]++;
					counter[1]--;								
				}
				break;
			}
		}

		return counter;
	}
}
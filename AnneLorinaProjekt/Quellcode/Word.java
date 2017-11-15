import java.util.*;

public class Word {
	//Class for a word in the lexicon to store the emotions
	boolean[] emotions = new boolean[10];
	boolean isEmotional = false;
	int length = 0;
	//int counter = 0;

	/**
	 * Turns emotion array into String.
	 *
	 * @return Emotion array as String
	 */	
	public String toString(){
		String s = "";
		for (int i = 0; i < 10 ;i ++ ) {
			s += "Emotion " + i + ": " + emotions[i] + " \n";
		}
		return s;
	}
}
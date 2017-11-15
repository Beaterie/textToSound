import java.util.*;
import java.io.*;
import java.lang.*;

public class Music {

	// positiv or negativ key of the melody: 
	// if more negative emotions in the section its minor,
	// else it is major
	private boolean minorOrMajorKey = true; //0 = minor, 1 = major 

	private int octaveM = 4;

	// number of words for one pitch
	private int n = 50;

	// emotion density
	// calculate which note tempo to use
	private double overallEmoDens = 0;

	//emotion of the melody
	private int emotion;
	private double maxEmotion;

	// store the words with theire emotion in a map
	private Map<String, Word> words = new HashMap<String, Word>();

	// map for pitches
	private Map<Integer, String> pitches = new HashMap<Integer, String>();

	public String music = "";

	private int text = 0;
	private int language = 0;

	public Music(){

	}

	public Music(Section s1, Section s2, Section s3, Section s4, 
							Map<String, Word> wordsMap, int emotionToUse, int octave, 
							boolean key, int method, int lang){
		// set variables
		words = wordsMap;
		emotion = emotionToUse;
		//music = musicSoFar;
		octaveM = octave;

		text = method;
		language = lang;
		
		if(!key){
			// initialize pitches for minor
			pitches.put(1,"C");
			pitches.put(2,"G");
			pitches.put(3,"Db");
			pitches.put(4,"Gb");
			pitches.put(5,"D");
			pitches.put(6,"F");
			pitches.put(7,"Ab");
		}
		else{// initialize pitches for major
			pitches.put(1,"C");
			pitches.put(2,"G");
			pitches.put(3,"E");
			pitches.put(4,"A");
			pitches.put(5,"D");
			pitches.put(6,"F");
			pitches.put(7,"B");
		}

		// overall emotion density of novel
		overallEmoDens = (double) (s1.sectionDensity[emotion] + 
															 s2.sectionDensity[emotion] + 
															 s3.sectionDensity[emotion] + 
															 s4.sectionDensity[emotion])
									 / (double) (s1.sectionDensity[10] + s2.sectionDensity[10] + 
									 					   s3.sectionDensity[10] + s4.sectionDensity[10]);

	  // octave calculation for Me1 and Me2 based on the emotion
	  if(emotion != 11){
	  	// joy or trust
	  	if((emotion == 6) || (emotion == 9)){
	  		octaveM++;
	  	}
	  	// expectation or surprise
	  	else if((emotion == 3) || (emotion == 8)){
	  		//nothing
	  	}
	  	// anger, fear, sadness or disgust
	  	else{
	  		octaveM--;
	  	}
	  } 				 

		calculateDuration(s1, s2, s3, s4);
		music += pitchSection(s1);
		music += " " + pitchSection(s2);
		music += " " + pitchSection(s3);
		music += " " + pitchSection(s4);

		if(music.equals("   ")){
			System.out.println("Not enough emotional words found in the text.");
			System.exit(1);
		}
	}

	/**
	 * Calls pitchSubsection for every subsection of the section
	 *
	 * @param Section 
	 *
	 * @return String with pitches, octaves and durations
	 */	
	public String pitchSection(Section sec){
		String pitch = "";
		pitch += pitchSubsection(sec.subsect1, sec.subsect1Density[emotion], 
														 sec.subsect1Density[10]);
		pitch += pitchSubsection(sec.subsect2, sec.subsect2Density[emotion], 
														 sec.subsect2Density[10]);
		pitch += pitchSubsection(sec.subsect3, sec.subsect3Density[emotion], 
														 sec.subsect3Density[10]);
		pitch += pitchSubsection(sec.subsect4, sec.subsect4Density[emotion], 
														 sec.subsect4Density[10]);
		return pitch;
	}

	/**
	 * Calculates pitches and durations of subsection 
	 *
	 * @param s Subsection, emotionNum the count of emotional words, wordsInTotal 
	 *        total words in subsection
	 *
	 * @return String with pitches, octaves and durations
	 */
	public String pitchSubsection(String s, int emotionNum, int wordsInTotal){

		// calculate emotion denstity of whole subsection
		double emotionDensity = (double) emotionNum/ (double)wordsInTotal;
		// calculate note duration for every subsection (1, 1/2, 1/4, 1/8, 1/16)
		//Problem: manchmal emotionDensity > overallEmoDens = werte > 5
		double duration = Math.ceil(emotionDensity/(maxEmotion/5));
		if(duration < 1){
			duration = 1;
		}
		// calculate pitches per messure, here 4/4
		int pitchesPerNote = (int)(Math.pow(2, duration-1));

		// devide into parts for every note
		ArrayList<Double> densities = new ArrayList<Double>();
		String[] tmp = s.split(" ");
		String wordsForPitch = "";
		int counter = 0;
		if(pitchesPerNote != 0)
			n = tmp.length / pitchesPerNote;
		else 
			return "";


		for(String w : tmp){
			wordsForPitch += w + " ";
			counter++;
			if(counter == n){
			//if(counter == Math.pow(2, duration-1)){
				counter = 0;
				// list.add(wordsForPitch);

				// calculate emotions
				Section tmpSect = new Section();
				int[] emos = new int[12];
				switch(text){
					case 1:
						emos = tmpSect.countWordsAdvanced(words, wordsForPitch);
						break;	
					case 2:
						emos = tmpSect.countWordsAdvancedPlus(words, wordsForPitch, 
																									language);
						break;	
					default:
						emos = tmpSect.countWords(words, wordsForPitch);
						break;	
				}
				

				// emotion desity, emos[10] == n
				double dens = (double)(emos[emotion]) / (double)emos[10];
				densities.add(dens);

				wordsForPitch = "";
			}
		}

		String pitchString = "";
		counter = 0;

		//System.out.println("Dens: " + densities);

		double maxDensity = 0.0;
		if( !densities.isEmpty()){
			maxDensity = Collections.max(densities);
		}
		if (maxDensity == 0.0){
			return "";
		}
		
		for(int i = 0; i < densities.size(); i++){

			// map pitch linearly to density
			double pitch = 7;
			if(densities.get(i) <= maxEmotion){
				pitch = Math.ceil(densities.get(i)/(maxEmotion/6))+1;
			}
			//set pitch + octave + duration(4 because of the 4/4 bar)
			pitchString += pitches.get((int) pitch) + octaveM + "/" + 
																(1/(double)pitchesPerNote) + " ";
			counter++;
		}

		return pitchString;
	}
	/**
	 * Calls calculateDurationSubsec, sets global variable maxEmotion
	 *
	 * @param s1 first section, s2 second section, s3 third section, 
	 *				s4 fourth section
	 */
	public void calculateDuration(Section s1, Section s2, Section s3, Section s4){
		ArrayList<Double> tmp = new ArrayList<Double>();
		tmp.add(calculateDurationSubsec(s1));
		tmp.add(calculateDurationSubsec(s2));
		tmp.add(calculateDurationSubsec(s3));
		tmp.add(calculateDurationSubsec(s4));

		maxEmotion = Collections.max(tmp);
	}
	/**
	 * Calculates maximum emotion density of the subsections
	 *
	 * @param s1 section 
	 *
	 * @return maximum emotion density
	 */
	public double calculateDurationSubsec(Section s1){
		ArrayList<Double> tmp = new ArrayList<Double>();
		tmp.add((double)s1.subsect1Density[emotion]/(double)s1.subsect1Density[10]);
		tmp.add((double)s1.subsect2Density[emotion]/(double)s1.subsect2Density[10]);
		tmp.add((double)s1.subsect3Density[emotion]/(double)s1.subsect3Density[10]);
		tmp.add((double)s1.subsect4Density[emotion]/(double)s1.subsect4Density[10]);

		return Collections.max(tmp);
	}
}

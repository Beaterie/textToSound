import java.io.BufferedReader;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.io.File;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.FileReader;
import java.io.FileWriter;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import javax.xml.namespace.QName;

class Main {
	
	public static void main(String[] args) throws IOException {
		
		// **************
		// Lists with all the important words
		// **************
		List<NERElement> NNList = new ArrayList<>();// List with Nouns
		List<NERElement> NNPList = new ArrayList<>(); // List with Proper Nouns (Names)
		List<NERElement> AdjList = new ArrayList<>(); // List with Adjectives			
		
		
		// Get time-wise starting point
		long startTime = System.nanoTime();
		
	

		// first argument = text to analyze
		String story = args[0];
		String tempstory = "temp" + story;
		File Ftempstory = new File(tempstory);

		prepareText(story, tempstory);
		classify(tempstory); // classifies provided story
		xml(tempstory, NNList, NNPList, AdjList); // takes the xml-file and makes something useful out of it
		
		AssessEmotion(AdjList);

		try {
			Ftempstory.delete();
		} catch (Exception e) {

			e.printStackTrace();
		}

		// Get time-wise end point
		long endTime = System.nanoTime();
		// show total runtime in seconds
		System.out.println("Took " + (endTime - startTime) / 1000000000.0 + " seconds");

	}

	// **************
	// *
	// *Create temp text file of text without punctuation
	// *
	// **************
	public static void prepareText(String story, String tempstory) {

		try {
			FileReader reader = new FileReader(story);
			BufferedReader bufferedReader = new BufferedReader(reader);
			FileWriter writer = new FileWriter(tempstory, true);

			String line;

			while ((line = bufferedReader.readLine()) != null) {
				String text = line.replaceAll(".,", " ");
				text = line.replaceAll("[^a-zA-Z ]", "");
				writer.write(text + " ");

			}
			writer.close();
			reader.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// **************
	// *
	// *Run Stanford Classifier and create XMl-File
	// *
	// **************
	public static void classify(String story) {
		System.out.println(story);// just a test
		try {
			Runtime rt = Runtime.getRuntime();
			Process pr = rt.exec(
					// "java -cp \"stanford-corenlp/*\" -Xmx2g
					// edu.stanford.nlp.pipeline.StanfordCoreNLP -annotators
					// tokenize,ssplit,pos,lemma,ner,parse,dcoref -file example.txt");
					"java -cp \"stanford-corenlp/*\" -Xmx2g edu.stanford.nlp.pipeline.StanfordCoreNLP -annotators tokenize,ssplit,pos,lemma,ner -file "
							+ story);

			int exitVal = pr.waitFor();
			System.out.println("Exited with error code " + exitVal);

		} catch (Exception e) {
			System.out.println("it does not work :(");
			e.printStackTrace();
		}
	}

	// **************
	// *
	// *Get important information from xml-file
	// *
	// **************
	public static void xml(String story,List<NERElement> NNList, List<NERElement> NNPList, List<NERElement> AdjList) {
		Integer iSentence = 0; // number of sentences
		Integer iToken = 0; // number of words
		Attribute SidAttr = null; // Sentence ID
		Attribute TidAttr = null; // Word ID
		String TName = null; // Word Name
		String category; // temporary variable to classify token for the two lists
		NERElement NEREle = null;

		try {
			XMLInputFactory factory = XMLInputFactory.newInstance();
			XMLEventReader eventReader = factory.createXMLEventReader(new FileReader(story + ".xml")); // read in the
																										// xml file

			while (eventReader.hasNext()) { // while there are still elements unread in xml
				XMLEvent event = eventReader.nextEvent();

				if (event.isStartElement()) {
					StartElement startElement = event.asStartElement();
					if (startElement.getName().getLocalPart().equals("sentence")) {// find element tagged as "sentence"
						SidAttr = startElement.getAttributeByName(new QName("id")); // define id attribute
						if (SidAttr != null) {
							iSentence++; // if Sentence has ID, increase count variable
						}
					} else if (startElement.getName().getLocalPart().equals("token")) {// find element tagged as "token"
						TidAttr = startElement.getAttributeByName(new QName("id")); // define id attribute
						if (TidAttr != null) {
							iToken++; // if Token has ID, increase count variable
						}
					}

					else if (startElement.getName().getLocalPart().equals("word")) {
						event = eventReader.nextEvent();
						TName = String.valueOf(event.asCharacters().getData());

					}

					else if (startElement.getName().getLocalPart().equals("POS")) {
						event = eventReader.nextEvent();
						NEREle = new NERElement();
						NEREle.setName(TName);
						NEREle.setTokenID(Integer.parseInt(TidAttr.getValue()));
						NEREle.setSentenceID(Integer.parseInt(SidAttr.getValue()));
						NEREle.setTotalPosition(iToken);

						category = String.valueOf(event.asCharacters().getData());
						// System.out.println(category);

						if (Objects.equals(category, "NN")) {
							NNList.add(NEREle);
						} else if (Objects.equals(category, "NNP")) {
							NNPList.add(NEREle);
						} else if (Objects.equals(category, "JJ") || Objects.equals(category, "JJR")
								|| Objects.equals(category, "JJS")) {
							AdjList.add(NEREle);
						}
					}
				}
			}

			for (NERElement Element : NNList) {
				Element.setRelativePosition(Math.round(Element.getTotalPosition() / iToken * 10000D) / 100D);
				// NNList.get(Element)
			}

			for (NERElement Element : NNPList) {
				Element.setRelativePosition(Math.round(Element.getTotalPosition() / iToken * 10000D) / 100D);
				// NNList.get(Element)
			}

			for (NERElement Element : AdjList) {
				Element.setRelativePosition(Math.round(Element.getTotalPosition() / iToken * 10000D) / 100D);
				// NNList.get(Element)
			}

			//System.out.println(NNList);
			// System.out.println(NNPList);
			// System.out.println(AdjList);

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (XMLStreamException e) {
			e.printStackTrace();
		}

	}

	// **************
	// *
	// *Use adjectives and analyze emotions
	// *
	// **************
	public static void AssessEmotion(List<NERElement> AdjList) throws IOException {
		List<EmotionElement> EmoLex;
		EmoLex = ReadLexicon();
		Integer Index;
		int ListPosition = 0;
		int Sections = 10; //aus Testzwecken fest
		double PpS = 100 / Sections; //Percent per Section
		List<SectionElement> SectionEmotion = new ArrayList<>(); // List with Adjectives
		SectionElement SeEl = null;
		
		
		System.out.println(AdjList.size());
		
		for(int i=0; i<Sections; i++){
			SeEl = new SectionElement();
			while (ListPosition < AdjList.size() && AdjList.get(ListPosition).getRelativePosition() < (i+1) * PpS) {
				Index = FindEqual( EmoLex, AdjList.get(ListPosition).getName()); //EmoLex.indexOf(AdjList.get(ListPosition).getName());
				if(Index != null){
					System.out.println(EmoLex.get(Index).getName());
					SeEl = AddEmotion(EmoLex.get(Index), SeEl);
				}
	            //System.out.println(AdjList.get(ListPosition).getRelativePosition());
	            ListPosition++;
	        }
			System.out.println("Section:" + i);
			System.out.println(SeEl);
			SectionEmotion.add(SeEl);
			
       }
		
		//For i = 0 bis Anzahl an Sektionen
		//While rel Position <= i+1* Prozentsatz
		//analysiere Gefühle

		// System.out.println(EmoLex);

	}

	// **************
	// *
	// *Create dictionary from NRC text file
	// *
	// **************
	private static List<EmotionElement> ReadLexicon() throws IOException {
		String mFileLexicon = "NRC_emolex.txt";

		FileReader lexReader = new FileReader(mFileLexicon);
		BufferedReader bufferedLexReader = new BufferedReader(lexReader);
		List<EmotionElement> emotions = new ArrayList<EmotionElement>();
		String line;
		String[] words;
		EmotionElement EmEl = null;

		// Reading NRC lexicon
		while ((line = bufferedLexReader.readLine()) != null) {
			if (!line.isEmpty()) {
				words = line.split("\t");
				if (EmEl == null) {
					EmEl = new EmotionElement();
					EmEl.setName(words[0]);
				}

				if (!words[0].equals(EmEl.getName())) {
					emotions.add(EmEl);
					EmEl = new EmotionElement();
					EmEl.setName(words[0]);
				}

				if (Objects.equals(words[0], EmEl.getName()) && Objects.equals(words[2], "1")) {

					switch (words[1]) {
					case "anger":
						EmEl.setAnger(true);
						break;
					case "anticipation":
						EmEl.setAnticipation(true);
						break;
					case "disgust":
						EmEl.setDisgust(true);
						break;
					case "fear":
						EmEl.setFear(true);
						break;
					case "joy":
						EmEl.setJoy(true);
						break;
					case "sadness":
						EmEl.setSadness(true);
						break;
					case "surprise":
						EmEl.setSurprise(true);
						break;
					case "trust":
						EmEl.setTrust(true);
						break;
					case "positive":
						EmEl.setPositive(true);
						break;
					case "negative":
						EmEl.setNegative(true);
						break;
					}
				}
			}
		}

		// Close readers
		bufferedLexReader.close();
		lexReader.close();
		return emotions;
	}
	
	private static Integer FindEqual(List<EmotionElement> EmoLex, String name) throws IOException {
		for (int i = 0; i< EmoLex.size(); i++) {
			if (EmoLex.get(i).getName().equals(name)) {
				Integer j = new Integer(i);
				return j;
			}
			
		}
		return null;
	
	}
	
	private static SectionElement AddEmotion(EmotionElement EmoLexEntry, SectionElement SeEl) {
		if (EmoLexEntry.getAnger()) {SeEl.setAnger(SeEl.getAnger() +1);}
		if (EmoLexEntry.getAnticipation()) {SeEl.setAnticipation(SeEl.getAnticipation() +1);}
		if (EmoLexEntry.getDisgust()) {SeEl.setDisgust(SeEl.getDisgust() +1);}
		if (EmoLexEntry.getFear()) {SeEl.setFear(SeEl.getFear() +1);}
		if (EmoLexEntry.getJoy()) {SeEl.setJoy(SeEl.getJoy() +1);}
		if (EmoLexEntry.getSadness()) {SeEl.setSadness(SeEl.getSadness() +1);}
		if (EmoLexEntry.getSurprise()) {SeEl.setSurprise(SeEl.getSurprise() +1);}
		if (EmoLexEntry.getTrust()) {SeEl.setTrust(SeEl.getTrust() +1);}
		if (EmoLexEntry.getPositive()) {SeEl.setPositive(SeEl.getPositive() +1);}
		if (EmoLexEntry.getNegative()) {SeEl.setNegative(SeEl.getNegative() +1);}
		return SeEl;
	}

}
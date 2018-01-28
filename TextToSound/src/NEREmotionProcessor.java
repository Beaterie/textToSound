import java.io.BufferedReader;
import java.io.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import edu.stanford.nlp.coref.CorefCoreAnnotations;
import edu.stanford.nlp.coref.data.CorefChain;
import edu.stanford.nlp.io.*;
import edu.stanford.nlp.ling.*;
import edu.stanford.nlp.pipeline.*;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.semgraph.SemanticGraphCoreAnnotations;
import edu.stanford.nlp.sentiment.SentimentCoreAnnotations;
import edu.stanford.nlp.trees.*;
import edu.stanford.nlp.util.*;

public class NEREmotionProcessor {

	// --------------------------------------------------------
	// Members
	// --------------------------------------------------------
	private String mSrcFileName;
	private Integer sections;
	private static Integer TextLength;

	private List<NERElement> NNList = new ArrayList<>();  // List with Nouns
	private List<NERElement> NNPList = new ArrayList<>(); // List with Proper Nouns (Names)
	private List<NERElement> AdjList = new ArrayList<>(); // List with Adjectives

	// --------------------------------------------------------
	// Constructors
	// --------------------------------------------------------

	public NEREmotionProcessor() {
	};

	/**
	 * Constructor.
	 * 
	 * @param src
	 *            source text to be analysed
	 * @param sections
	 *            number of sections
	 */
	public NEREmotionProcessor(String src, Integer sec) {
		mSrcFileName = src;
		sections = sec;
	};

	// --------------------------------------------------------
	// Methods
	// --------------------------------------------------------

	// *Create temp text file of text without punctuation
	public static void prepareText(String story, String tempstory) {

		try {
			// System.out.println(story);
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

	// *Run Stanford Classifier and create XMl-File
	
	public static void create_xml(String story)  throws IOException{
		
		String storyXML = story + ".xml";
		PrintWriter xmlOut =  new PrintWriter(storyXML);
		
		 Properties props = new Properties();
		 props.setProperty("annotators", "tokenize, ssplit, pos, lemma, ner");

		 StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
		 
		 Annotation annotation = new Annotation(IOUtils.slurpFileNoExceptions(story));
		 
		 pipeline.annotate(annotation);
		 
		 pipeline.xmlPrint(annotation, xmlOut);

	}
	
	
	public static void classify(String story) {
		System.out.println(story);// just a test
		//$ java -cp 'data/stanford-corenlp/*' -Xmx2g edu.stanford.nlp.pipeline.StanfordCoreNLP -annotators tokenize,ssplit,pos,lemma,ner -file data/the-fox-and-the-crowtemp.txt -outputDirectory data/
		try {
			Runtime rt = Runtime.getRuntime();
			Process pr = rt.exec(
					"java -cp \"data/stanford-corenlp/*\" -Xmx2g edu.stanford.nlp.pipeline.StanfordCoreNLP -annotators tokenize,ssplit,pos,lemma,ner -file "
							+ story + " -outputDirectory data/");

			int exitVal = pr.waitFor();
			System.out.println("Exited with error code " + exitVal);
		} catch (Exception e) {
			System.out.println("it does not work :(");
			e.printStackTrace();
		}
	}

	// *Get important information from xml-file
	public static void analyze_xml(String story, List<NERElement> NNList, List<NERElement> NNPList, List<NERElement> AdjList) {
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
					} else if (startElement.getName().getLocalPart().equals("word")) {
						event = eventReader.nextEvent();
						TName = String.valueOf(event.asCharacters().getData());

					} else if (startElement.getName().getLocalPart().equals("POS")) {
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
						} else if (Objects.equals(category, ".") || Objects.equals(category, ",")
								|| Objects.equals(category, "``") || Objects.equals(category, "''")
								|| Objects.equals(category, "\"") || Objects.equals(category, "POS")
								|| Objects.equals(category, "'") || Objects.equals(category, ";")
								|| Objects.equals(category, "/") || Objects.equals(category, ":")
								|| Objects.equals(category, "[") || Objects.equals(category, "]")
								|| Objects.equals(category, "(") || Objects.equals(category, ")")
								|| Objects.equals(category, "{") || Objects.equals(category, "}")
								|| Objects.equals(category, "?") || Objects.equals(category, "!")
								|| Objects.equals(category, "`") || Objects.equals(category, "�")
								|| Objects.equals(category, "-") || Objects.equals(category, "_")
								|| Objects.equals(category, "CD") || Objects.equals(category, "SYM")) {
							iToken--; // ignore punctuation for total text length
						}
					}
				}
			}
			TextLength = iToken;
			//System.out.println(TextLength);

			for (NERElement Element : NNList) {
				Element.setRelativePosition(Math.round(Element.getTotalPosition() / TextLength * 10000D) / 100D);
				// NNList.get(Element)
			}

			for (int Element = 0; Element < NNPList.size(); Element++) {
				NNPList.get(Element).setRelativePosition(
						Math.round(NNPList.get(Element).getTotalPosition() / TextLength * 10000D) / 100D);
			}
			
			
			for (int Element = 0; Element < NNPList.size()-1; Element++) {
				int index = 0;
				while(Element + index < NNPList.size()-1 && NNPList.get(Element + index).getSentenceID() == NNPList.get(Element + index + 1).getSentenceID()
						&& NNPList.get(Element + index).getTokenID() == NNPList.get(Element + index + 1).getTokenID() - 1) {
					NNPList.get(Element).setName(NNPList.get(Element).getName() +" " + NNPList.get(Element+index + 1).getName());
					index++;
					
				}
				for (int i = 0; i < index; i++) {
					NNPList.remove(Element+1);
				}

			}

			for (NERElement Element : AdjList) {
				Element.setRelativePosition(Math.round(Element.getTotalPosition() / TextLength * 10000D) / 100D);
				// NNList.get(Element)
			}

			// System.out.println(NNList);
			//System.out.println(NNPList);
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
	public static EmotionResult AssessEmotion(List<NERElement> AdjList, Integer sections) throws IOException {

		List<EmotionElement> EmoLex;
		EmoLex = ReadLexicon();
		Integer Index;
		int ListPosition = 0; // Z�hlvariable
		int DensityPosition = 0; // Z�hlvariable
		int EmotionAmount;
		double PpS = 100 / sections; // Percent per Section
		int PosSum = 0;
		int NegSum = 0;
		List<List<Double>> SectionEmotion = new ArrayList<>(); // List with Adjectives
		List<Double> SeEl = null;

		List<List<Double>> AllDensities = new ArrayList<>(); // Densities for all Sections
		List<Double> Density = null; // 16 Densities for one Section

		EmotionResult EmotionResult = new EmotionResult();
		for (int i = 0; i < sections; i++) { // Interation �ber jede Textsektion
			SeEl = Arrays.asList(0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0);
			while (ListPosition < AdjList.size() && AdjList.get(ListPosition).getRelativePosition() < (i + 1) * PpS) {
				// solang nicht an alle Elemente aus AdjList und Prozentsatz des Textes pro
				// Sektion abgearbeitet
				Index = FindEqual(EmoLex, AdjList.get(ListPosition).getName());
				if (Index != null) {
					SeEl = AddEmotion(EmoLex.get(Index), SeEl);
				}
				ListPosition++;
			}

			SectionEmotion.add(SeEl);
		}

		for (int i = 0; i < sections; i++) {
			Density = Arrays.asList(0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0);
			for (int j = 0; j < 16; j++) {
				EmotionAmount = 0;

				while (DensityPosition < AdjList.size()
						&& AdjList.get(DensityPosition).getRelativePosition() < ((i) * PpS + (j + 1) * PpS / 16)) {
					// rel. Position des Adj. in gsm Text <(IndexSektion* 100%/SummeSektionen
					Index = FindEqual(EmoLex, AdjList.get(DensityPosition).getName());
					if (Index != null) {
						EmotionAmount++;
					}

					DensityPosition++;
				}
				double D = EmotionAmount / (TextLength / (16D * sections));
				Density.set(j, D);
			}
			AllDensities.add(Density);
		}

		System.out.println("Number of Sections: " + sections);

		EmotionResult.setSectionEmotion(SectionEmotion);
		EmotionResult.setDensity(AllDensities);

		for (List<Double> Element : SectionEmotion) {
			PosSum = PosSum + Element.get(8).intValue();
			NegSum = NegSum + Element.get(9).intValue();
		}

		EmotionResult.setNegSum(NegSum);
		EmotionResult.setPosSum(PosSum);
		return EmotionResult;

	}

	// *Create dictionary from NRC text file
	private static List<EmotionElement> ReadLexicon() throws IOException {
		String mFileLexicon = "data/NRC_emolex.txt";

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
		for (int i = 0; i < EmoLex.size(); i++) {
			if (EmoLex.get(i).getName().equals(name)) {
				Integer j = new Integer(i);
				return j;
			}

		}
		return null;

	}

	private static List<Double> AddEmotion(EmotionElement EmoLexEntry, List<Double> SeEl) {
		if (EmoLexEntry.getAnger()) {
			SeEl.set(0, SeEl.get(0) + 1);
		}
		if (EmoLexEntry.getAnticipation()) {
			SeEl.set(1, SeEl.get(1) + 1);
		}
		if (EmoLexEntry.getDisgust()) {
			SeEl.set(2, SeEl.get(2) + 1);
		}
		if (EmoLexEntry.getFear()) {
			SeEl.set(3, SeEl.get(3) + 1);
		}
		if (EmoLexEntry.getJoy()) {
			SeEl.set(4, SeEl.get(4) + 1);
		}
		if (EmoLexEntry.getSadness()) {
			SeEl.set(5, SeEl.get(5) + 1);
		}
		if (EmoLexEntry.getSurprise()) {
			SeEl.set(6, SeEl.get(6) + 1);
		}
		if (EmoLexEntry.getTrust()) {
			SeEl.set(7, SeEl.get(7) + 1);
		}
		if (EmoLexEntry.getPositive()) {
			SeEl.set(8, SeEl.get(8) + 1);
		}
		if (EmoLexEntry.getNegative()) {
			SeEl.set(9, SeEl.get(9) + 1);
			;
		}
		return SeEl;
	}
	
	public List<String> nameDetection() {
		create_xml(mSrcFileName);
		analyze_xml(mSrcFileName, NNList, NNPList, AdjList);
		
		// Put the names to map
		Map<String, Integer> map = new HashMap<String, Integer>(); 
		for (NERElement nerElement : NNPList) {
			String name = nerElement.getName();
			if (map.containsKey(name)) {
				int count = map.get(name);
				map.put(name, count+1);
			} else {
				map.put(name, 1);
			}
		}
		
		// Filter the map and delete names that occured less than 4 times
		map.entrySet().removeIf(entry -> entry.getValue()<4);
		
		// Store the reduced names to a new list
		List<String> names = new ArrayList<String>(map.keySet());
		System.out.println(names.toString());
		
		return names;
	}

	public EmotionResult main(String[] args) throws IOException {

		NEREmotionProcessor NERprocessor1 = new NEREmotionProcessor("data/little-red-riding-hood.txt", 10);

		String tempSrc = mSrcFileName;
		// String tempSrc = mSrcFileName.substring(0, mSrcFileName.length() - 4) +
		// "temp.txt";
		File Ftempstory = new File(tempSrc);

		// prepareText(mSrcFileName, tempSrc);
		System.out.println("prepare Text done");
		create_xml(tempSrc);
		//classify(tempSrc);
		System.out.println("classify done");
		analyze_xml(tempSrc, NNList, NNPList, AdjList);
		System.out.println("xml done");

		EmotionResult EmotionResults = AssessEmotion(AdjList, sections);
		System.out.println("emotion done");
		EmotionResults.printResult();

		try {
			// Ftempstory.delete();
		} catch (Exception e) {

			e.printStackTrace();
		}

		return EmotionResults;

	}

}
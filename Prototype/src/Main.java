import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.File;
import java.util.List;
import java.util.ArrayList;
import java.util.Objects;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Iterator;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import javax.xml.namespace.QName;

class Main {

	public static void main(String[] args) {
		String story = args[0]; // specifying a text that just be analyzed
		
		
		classify(story); // classifies provided story
		xml(story); // takes the xml-file and makes something useful out of it
		
		
		//for(NERElement NEREle : NNPList){
         //   System.out.println(NEREle.toString());
       // }

		// things 2 do:
		// take the xml-doc and parse it
		// find Proper Nouns and "normal" Nouns and save their position
		// also get total length of document (for declaring segments and stuff)
	}

	public static void classify(String story) {
		// this little thing runs the stanford classifier and creates a xml-file for the
		// text
		System.out.println(story);// just a test
		try {
			Runtime rt = Runtime.getRuntime();
			Process pr = rt.exec(
					"java -cp \"stanford-corenlp/*\" -Xmx2g edu.stanford.nlp.pipeline.StanfordCoreNLP -annotators tokenize,ssplit,pos,lemma,ner,parse,dcoref -file example.txt");

			BufferedReader input = new BufferedReader(new InputStreamReader(pr.getInputStream()));

			String line = null;

			while ((line = input.readLine()) != null) {
				System.out.println(line);
			}

			int exitVal = pr.waitFor();
			System.out.println("Exited with error code " + exitVal);

		} catch (Exception e) {
			System.out.println("it does not work :(");
			e.printStackTrace();
		}
	}

	// get the important information from the generated xml data
	public static void xml(String story) {
		Integer iSentence = 0; // number of sentences
		Integer iToken = 0; // number of words
		Attribute SidAttr = null; // Sentence ID
		Attribute TidAttr = null; // Word ID
		String TName = null; // Word Name
		String category; // temporary variable to classify token for the two lists
		NERElement NEREle = null;
		List<NERElement> NNList = new ArrayList<>();// two Lists for Named Nouns (NN) and Proper Named Nouns (NNP)
		List<NERElement> NNPList = new ArrayList<>(); // both contain the word in question and its position


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
						//System.out.println(category);
						
						if (Objects.equals(category, "NN")) {
							NNList.add(NEREle);

						} else if (Objects.equals(category, "NNP")) {
							NNPList.add(NEREle);
						}
						
						
					}

				}

			}
			
		        System.out.println(NNList);
		        System.out.println(NNPList);
			
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (XMLStreamException e) {
			e.printStackTrace();
		}

	}
	
}
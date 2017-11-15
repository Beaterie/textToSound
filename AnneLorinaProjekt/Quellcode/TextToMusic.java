import java.io.*;
import java.util.*;
import java.lang.Runtime;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.sound.midi.Sequence;
import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiUnavailableException;
import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import org.jfugue.player.Player;
import org.jfugue.player.ManagedPlayer;
import org.jfugue.pattern.Pattern;
import org.jfugue.midi.MidiFileManager;

/**
 * This is a GUI which can transform Text into Music.
 *
 * @author Anne Peter, Norina Marie Grosch
 * @version 1.0
 */

public class TextToMusic extends JFrame {
  // Anfang Attribute
  private JTextField jTextField1 = new JTextField();
  private JComboBox<String> jComboBox1 = new JComboBox<String>();
  private DefaultComboBoxModel<String> jComboBox1Model = 
          new DefaultComboBoxModel<String>();
  private JLabel jLabel1 = new JLabel();
  private JLabel jLabel2 = new JLabel();
  private JLabel jLabel4 = new JLabel();     
  private JComboBox<String> jComboBox2 = new JComboBox<String>();
  private DefaultComboBoxModel<String> jComboBox2Model = 
          new DefaultComboBoxModel<String>();
  private JLabel jLabel5 = new JLabel();
  private JLabel jLabel6 = new JLabel();
  private JComboBox<String> jComboBox3 = new JComboBox<String>();
  private DefaultComboBoxModel<String> jComboBox3Model = 
          new DefaultComboBoxModel<String>();
  private JComboBox<String> jComboBox4 = new JComboBox<String>();
  private DefaultComboBoxModel<String> jComboBox4Model = 
          new DefaultComboBoxModel<String>();  
  private JComboBox<String> jComboBox5 = new JComboBox<String>();
  private DefaultComboBoxModel<String> jComboBox5Model = 
          new DefaultComboBoxModel<String>();
  private JButton jButton1 = new JButton();
  private JButton jButton2 = new JButton();
  private JButton jButton3 = new JButton();
  private JLabel jLabel7 = new JLabel();
  private JLabel jLabel8 = new JLabel();
  private JLabel jLabel9 = new JLabel();
  private JLabel jLabel10 = new JLabel();
  private JLabel jLabel11 = new JLabel();
  private JLabel jLabel12 = new JLabel();
  private boolean start = true;
  private boolean pause = true;
  private String music = "";
  // Ende Attribute
  
  public TextToMusic(String title) { 
    // Frame-Initialisierung
    super(title);
    setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    int frameWidth = 440; 
    int frameHeight = 460;
    setSize(frameWidth, frameHeight);
    Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
    int x = (d.width - getSize().width) / 2;
    int y = (d.height - getSize().height) / 2;
    setLocation(x, y);
    setResizable(false);
    Container cp = getContentPane();
    cp.setLayout(null);
    // Anfang Komponenten
    // heading
    jLabel7.setBounds(40, 30, 355, 33);
    jLabel7.setText("Transform Text into Music");
    jLabel7.setFont(new Font("Sans_Serif", Font.BOLD, 18));
    cp.add(jLabel7);

    jLabel8.setBounds(40, 350, 355, 33);
    cp.add(jLabel8);

    jLabel9.setBounds(40, 370, 355, 33);
    cp.add(jLabel9);

    jLabel10.setBounds(40, 390, 355, 33);
    cp.add(jLabel10);

    jLabel11.setBounds(40, 410, 355, 33);
    cp.add(jLabel11);

    jLabel1.setBounds(40, 75, 171, 25);
    jLabel1.setText("Text file (.txt):");
    cp.add(jLabel1);

    jTextField1.setBounds(40, 98, 161, 27);
    jTextField1.setText("data/Alice.txt");
    cp.add(jTextField1);

    jLabel2.setBounds(240, 75, 171, 25);
    jLabel2.setText("Lexicon:");        
    cp.add(jLabel2);

    jComboBox1.setModel(jComboBox1Model);
    jComboBox1.setBounds(240, 98, 161, 25);
    cp.add(jComboBox1);

    jLabel12.setBounds(40, 136, 171, 25);
    jLabel12.setText("Text Analyse Method:");        
    cp.add(jLabel12);

    jComboBox5.setModel(jComboBox5Model);
    jComboBox5.setBounds(40, 160, 361, 25);
    cp.add(jComboBox5);

    jLabel4.setBounds(40, 207, 155, 25);
    jLabel4.setText("Instrument 1:");
    cp.add(jLabel4);

    jComboBox2.setModel(jComboBox2Model);
    jComboBox2.setBounds(240, 207, 161, 25);
    cp.add(jComboBox2);

    jLabel5.setBounds(40, 247, 155, 25);
    jLabel5.setText("Instrument 2:");
    cp.add(jLabel5);

    jComboBox3.setModel(jComboBox3Model);
    jComboBox3.setBounds(240, 247, 161, 25);
    cp.add(jComboBox3);

    jLabel6.setBounds(40, 287, 155, 25);
    jLabel6.setText("Instrument 3:");
    cp.add(jLabel6);

    jComboBox4.setModel(jComboBox4Model);
    jComboBox4.setBounds(240, 287, 161, 25);
    cp.add(jComboBox4);
    // own button:
    // JButton jb = new JButton(new ImageIcon("pic.png"));

    // Play/Pause button
    jButton1.setBounds(230, 330, 93, 25);
    jButton1.setText("Play/Pause");
    jButton1.setMargin(new Insets(2, 2, 2, 2));
    // set as default button
    getRootPane().setDefaultButton(jButton1);
    Player player = new Player();
    ManagedPlayer mPlayer = player.getManagedPlayer();
    jButton1.addActionListener(new ActionListener() { 
      public void actionPerformed(ActionEvent evt) { 
        jButton1_ActionPerformed(evt);
        if(start == true) {
          int method = getMethod(jComboBox5.getSelectedItem().toString());

          try {
            if(jTextField1.getText().equals("")){
              jLabel9.setText("No file, please restart and try with a file.");
            }
            else if(!jTextField1.getText().contains(".txt")){
              jLabel9.setText("No txt-file, please restart and try with a "+
                              "txt-file.");
            }
            File tmpFile = new File(jTextField1.getText());
            if(!tmpFile.exists()){
              jLabel9.setText("No file found, please restart and try again.");
            }
          
            TextProcessor t = new TextProcessor(jTextField1.getText(),
                                        jComboBox1.getSelectedItem().toString(),
                                        jComboBox2.getSelectedItem().toString(),
                                        jComboBox3.getSelectedItem().toString(),
                                        jComboBox4.getSelectedItem().toString(),
                                        method);
            music = "";
            music = t.process();
            System.out.println("\n" + "whole music:" + "\n" + music);
            jLabel8.setText("Key: " + t.getKey());
            jLabel9.setText("Highest Emotion: " + t.getDominant());
            jLabel10.setText("Second Highest Emotion: " + t.getDominantTwo());
            jLabel11.setText("Tempo: " + t.getTempo() + " bpm");
            Sequence m = player.getSequence(music);
            mPlayer.start(m);
            jButton3.setBounds(330, 410, 73, 25);
            jButton3.setText("Save");
            jButton3.setMargin(new Insets(2, 2, 2, 2));
            //play with the last player in list
            //mPlayers.get(mPlayers.size() - 1).start(m);

          } catch (FileNotFoundException e) {
            e.printStackTrace();
          } catch (InvalidMidiDataException e) {
            e.printStackTrace();
          } catch (MidiUnavailableException e) {
            e.printStackTrace();
          } catch (IOException e){
            e.printStackTrace();
          }
          start = false;
        }
        else {
          if(pause == true){
            mPlayer.pause();
            pause = false;
          }
          else {
            mPlayer.resume();
            pause = true;
          }
        }
      }
    });
    cp.add(jButton1);
    // Stop Button
    jButton2.setBounds(330, 330, 73, 25);
    jButton2.setText("Restart");
    jButton2.setMargin(new Insets(2, 2, 2, 2));
    jButton2.addActionListener(new ActionListener() { 
      public void actionPerformed(ActionEvent evt) { 

        // restart application
        // because there's no otherway to use the player a second time, 
        // it always just plays one melodie if it is used twice
        try{
            Runtime.getRuntime().exec("java -cp .:../jfugue-5.0.7.jar TextToMusic");
            System.exit(0);
        } catch(SecurityException e){
            e.printStackTrace();
        } catch(IOException e){
            e.printStackTrace();
        } catch(NullPointerException e){
            e.printStackTrace();
        } catch(IllegalArgumentException e){
            e.printStackTrace();
        }   
      }
    });
    cp.add(jButton2);

    jButton3.addActionListener(new ActionListener() { 
      public void actionPerformed(ActionEvent evt) { 

        // Saves Music in Midi-Filw
        try{
          if(!music.equals("")){
            Pattern pattern = new Pattern(music);
            MidiFileManager.savePatternToMidi(pattern, 
                            new File(jTextField1.getText().substring(0, 
                                   jTextField1.getText().length() - 4)+".mid"));
          }
        } 
        catch (IOException e) {
          e.printStackTrace();
        }  
      }
    });
    cp.add(jButton3);

    // Ende Komponenten   
    
    // initialize Lexicon
    jComboBox1Model.addElement("German");
    jComboBox1Model.addElement("English");

    // initialize Text analysis methods
    jComboBox5Model.addElement("NRC Lexicon simple");
    jComboBox5Model.addElement("NRC Lexicon advanced");
    jComboBox5Model.addElement("NRC Lexicon advanced with negation");
    
    // initialize Instruments
    jComboBox2Model.addElement("Piano");
    jComboBox3Model.addElement("Piano");
    jComboBox4Model.addElement("Piano");

    String[] instrumentList = getInstruments();

    for(String s : instrumentList){
      jComboBox2Model.addElement(s);
      jComboBox3Model.addElement(s);
      jComboBox4Model.addElement(s);
    } 
    
    setVisible(true);
  } 
  
  // Anfang Methoden
  
  public static void main(String[] args) throws FileNotFoundException{
    new TextToMusic("TextToMusic");
  } // end of main

  public void jButton1_ActionPerformed(ActionEvent evt) {
    
  } 

  public void jButton2_ActionPerformed(ActionEvent evt) {
  
  }

  public int getMethod(String m){
    if(m.equals("NRC Lexicon advanced"))
      return 1;
    else if (m.equals("NRC Lexicon advanced with negation"))
      return 2;
    else
      return 0;
  }

  public String[] getInstruments(){
    String[] instruments = new String[] {
        "Bright_Acoustic",
        "Electric_Grand",
        "Honkey_Tonk",
        "Electric_Piano",
        "Electric_Piano_2",
        //"Harpischord", // doesnt work, causes error
        "Clavinet",
        "Celesta",
        "Glockenspiel",

        "Music_Box",
        "Vibraphone",
        "Marimba",
        "Xylophone",
        "Tubular_Bells",
        "Dulcimer",
        "Drawbar_Organ",
        "Percussive_Organ",
        "Rock_Organ",
        "Church_Organ",

        "Reed_Organ",
        "Accordian",
        "Harmonica",
        "Tango_Accordian",
        "Guitar",
        "Steel_String_Guitar",
        "Electric_Jazz_Guitar",
        "Electric_Clean_Guitar",
        "Electric_muted_Guitar",
        "Overdriven_Guitar",
        "Distortion_Guitar",

        "Guitar_Harmonics",
        "Acoustic_Bass",
        "Electric_Bass_Finger",
        "Electric_Bass_Pick",
        "Fretless_Bass",
        "Slap_Bass_1",
        "Slap_Bass_2",
        "Synth_Bass_1",
        "Synth_Bass_2",

        "Violin",
        "Viola",
        "Cello",
        "Contrabass",
        "Tremolo_Strings",
        "Pizzicato_Strings",
        "Orchestral_Strings",
        "Timpani",
        "String_Ensemble_1",
        "String_Ensemble_2",

        "Synth_strings_1",
        "Synth_strings_2",
        "Choir_Aahs",
        "Voice_Oohs",
        "Synth_Voice",
        "Orchestra_Hit",
        "Trumpet",
        "Trombone",
        "Tuba",
        "Muted_Trumpet",

        "French_Horn",
        "Brass_Section",
        "Synth_brass_1",
        "Synth_brass_2",
        "Soprano_Sax",
        "Alto_Sax",
        "Tenor_Sax",
        "Baritone_Sax",
        "Oboe",
        "English_Horn",

        "Bassoon",
        "Clarinet",
        "Piccolo",
        "Flute",
        "Recorder",
        "Pan_Flute",
        "Blown_Bottle",
        "Skakuhachi",
        "Whistle",
        "Ocarina",

        "Square",
        "Sawtooth",
        "Calliope",
        "Chiff",
        "Charang",
        "Voice",
        "Fifths",
        //"Basslead", //causes error
        "New_Age",
        "Warm",

        //"Polysynth", //causes error
        "Choir",
        "Bowed",
        "Metallic",
        "Halo",
        "Sweep",
        "Rain",
        "Soundtrack",
        "Crystal",
        "Atmosphere",

        "Brightness",
        "Goblins",
        "Echoes",
        //"Sci-fi", //causes error
        "Sitar",
        "Banjo",
        "Shamisen",
        "Koto",
        "Kalimba",
        "Bagpipe",

        "Fiddle",
        "Shanai",
        "Tinkle_Bell",
        "Agogo",
        "Steel_Drums",
        "Woodblock",
        "Taiko_Drum",
        "Melodic_Tom",
        "Synth_Drum",
        "Reverse_Cymbal",

        "Guitar_Fret_Noise",
        "Breath_Noise",
        "Seashore",
        "Bird_Tweet",
        "Telephone_Ring",
        "Helicopter",
        "Applause",
        "Gunshot"};
    return instruments;
  }


}
//package com.ociweb.jnb.jfugue;
import org.jfugue.player.Player;
import org.jfugue.pattern.Pattern;
import org.jfugue.midi.MidiFileManager;
import java.io.*;

/**
 * This is an example how to use our Text to Audio
 *
 * @author Anne Peter, Norina Marie Grosch
 * @version 1.0
 */

public class Main {
  public static void main(String[] args) throws FileNotFoundException, IOException{
    // create a TextProcessor with a File, a text analyse method (0=simple) and a Lexicon
    TextProcessor t = new TextProcessor("data/Alice.txt", 0, "Deutsch");
    // process the file
    String music = t.process();
    // play music
  	Player player = new Player();
  	player.play(music);
  }
}


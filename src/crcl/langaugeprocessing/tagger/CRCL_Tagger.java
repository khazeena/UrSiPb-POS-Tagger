package crcl.langaugeprocessing.tagger;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author MS Vaswani
 */
public class CRCL_Tagger {

    /**
     * @param args the command line arguments
     */
    private static String language = "sindhi";

    public static void main(String[] args) throws IOException {
        try {
            HashMap<String, String> parameters = new HashMap<>();
            for (int i = 0; i < args.length; i += 2) {
                parameters.put(args[i], args[i + 1]);
            }
            ObjectInputStream in = new ObjectInputStream(new FileInputStream("data.ser"));
            
            POSTagger tagger = (POSTagger) in.readObject();
           
            if(parameters.get("-l")!=null){
                tagger.language=parameters.get("-l");
            }
            else{
                tagger.language="urdu";
            }
            
            if (parameters.containsKey("-di")) {
                tagger.tagFolder(parameters.get("-di"), parameters.get("-do"), parameters.get("-d"));
            } else if (parameters.containsKey("-fi")) {
                tagger.tagFile(parameters.get("-fi"), parameters.get("-fo"), parameters.get("-d"));
            }
            tagger.close();
            

        } catch (ClassNotFoundException ex) {
            Logger.getLogger(CRCL_Tagger.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("Data file Exception");
        }
        
//        POSTagger tagger = new POSTagger(null);
//        ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(new File("data.ser")));
//
//        // do the magic  
//        oos.writeObject(tagger);
//        // close the writing.
//        oos.close();
    }

}

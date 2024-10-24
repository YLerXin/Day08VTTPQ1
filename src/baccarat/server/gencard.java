package baccarat.server;

import java.util.*;
import java.io.*;
import java.net.*;

public class gencard {

    // public static void main(String[] args) throws IOException {

    //     List<String> deck = new ArrayList<String>();
    //     int numdeck = 4;
    //     String[] singleDeck = 
    //     {"1.1","2.1","3.1","4.1","5.1","6.1","7.1","8.1","9.1","10.1","11.1","12.1","13.1",
    //     "1.2","2.2","3.2","4.2","5.2","6.2","7.2","8.2","9.2","10.2","11.2","12.2","13.2",
    //     "1.3","2.3","3.3","4.3","5.3","6.3","7.3","8.3","9.3","10.3","11.3","12.3","13.3",
    //     "1.4","2.4","3.4","4.4","5.4","6.4","7.4","8.4","9.4","10.4","11.4","12.4","13.4"};

    //         for(int i=0;i<numdeck;i++){
    //             deck.addAll(Arrays.asList(singleDeck));
    //         }

    //         String[] fulldeck = new String[deck.size()];
    //         fulldeck = deck.toArray(fulldeck);

    //         Random rand = new Random();
    //         for (int i=0; i<fulldeck.length;i++){
    //             int randomIndexToSwap = rand.nextInt(numdeck*52);
    //             String temp = fulldeck[randomIndexToSwap];
    //             fulldeck[randomIndexToSwap] = fulldeck[i];
    //             fulldeck[i] = temp;
    //         }

    //         File dbDirectory = new File("C:\\Users\\yongl\\VTTP\\Day08RevVTTP\\src\\baccarat\\server\\cards.db");
    //         FileWriter fw = new FileWriter(dbDirectory);
    //         BufferedWriter bw = new BufferedWriter(fw);
    //         for (int i = 0;i<fulldeck.length;i++){
    //             bw.write(fulldeck[i]+" ");
    //             bw.newLine();
    //         }
    //         bw.flush();
    //         bw.close();
            
    //     }


    public File genDeck(int numdeck) throws IOException{

        //Create number of decks and shuffle them into an array
        List<String> deck = new ArrayList<String>();

        String[] singleDeck = 
        {"1.1","2.1","3.1","4.1","5.1","6.1","7.1","8.1","9.1","10.1","11.1","12.1","13.1",
        "1.2","2.2","3.2","4.2","5.2","6.2","7.2","8.2","9.2","10.2","11.2","12.2","13.2",
        "1.3","2.3","3.3","4.3","5.3","6.3","7.3","8.3","9.3","10.3","11.3","12.3","13.3",
        "1.4","2.4","3.4","4.4","5.4","6.4","7.4","8.4","9.4","10.4","11.4","12.4","13.4"};

        for(int i=0;i<numdeck;i++){
            deck.addAll(Arrays.asList(singleDeck));
        }

        String[] fulldeck = new String[deck.size()];
        fulldeck = deck.toArray(fulldeck);

        Random rand = new Random();
        for (int i=0; i<fulldeck.length;i++){
            int randomIndexToSwap = rand.nextInt(numdeck*52);
            String temp = fulldeck[randomIndexToSwap];
            fulldeck[randomIndexToSwap] = fulldeck[i];
            fulldeck[i] = temp;
        }

        File dbDirectory = new File("cards.db");
        FileWriter fw = new FileWriter(dbDirectory);
        BufferedWriter bw = new BufferedWriter(fw);
        for (int i = 0;i<fulldeck.length;i++){
            bw.write(fulldeck[i]+" ");
            bw.newLine();
        }
        bw.flush();
        bw.close();

        return dbDirectory;
    }


}

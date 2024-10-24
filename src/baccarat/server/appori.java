package baccarat.server;

import java.io.*;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.net.*;

public class appori {

    public static void main(String[] args) throws IOException {

        int portnum = 1000;
        int numdeck = 1;
        
        if (args.length >= 2){
            portnum = Integer.parseInt(args[0]);
            numdeck = Integer.parseInt(args[1]);
        }
        else{
            System.err.println("Please input port and decks");
        }

        gencard gc = new gencard();
        File cardf = gc.genDeck(numdeck);

        ServerSocket sock = new ServerSocket(portnum);
        System.out.println("Waiting for connection");

        //for threads
        // BaccaratEngine be = new BaccaratEngine();
        ExecutorService es = Executors.newFixedThreadPool(10);
        try{
        while (true){
            Socket clientsoc = sock.accept();
            System.out.println("Client connected.");
            es.execute(new BaccaratEngineori(clientsoc));

        }
        }
        catch(IOException e){
            e.printStackTrace();
        }
        finally{
            es.shutdown();
        }
    }

    
}

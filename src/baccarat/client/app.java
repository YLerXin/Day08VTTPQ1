package baccarat.client;

import java.util.*;
import java.io.*;
import java.net.*;


public class app {

    public static void main(String[] args) throws IOException {

if (args.length < 1) {
            System.out.println("Usage: java -cp classes client.ClientApp <server_address>:<port>");
            return;
        }

        String[] addressParts = args[0].split(":");
        String serverAddress = addressParts[0];
        int port;

        try {
            port = Integer.parseInt(addressParts[1]);
        } catch (NumberFormatException e) {
            System.out.println("Invalid port number.");
            return;
        }

        try (Socket socket = new Socket(serverAddress, port);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             Scanner scanner = new Scanner(System.in)) {

            System.out.println("Connected to server at " + serverAddress + ":" + port);
            
            while (true) {
                System.out.print("Enter command: ");
                String userCommand = scanner.nextLine();
                if(userCommand == "exit"){
                    System.out.println("exiting..");
                    break;
                }
                out.println(userCommand);
                
                String response = in.readLine();
                System.out.println("Server response: " + response);
            }
        } catch (IOException e) {
            System.out.println("Error connecting to server: " + e.getMessage());
        }

        // while(login == false){
        //     String input1 = cons.readLine("Hello there, Please login: ");
            
        //     if(input1.startsWith("Login ")){
        //         try {
        //             Integer.parseInt((input1.split(" "))[2]);
        //             //System.out.println((input1.split(" "))[2] + " is a valid integer");
        //             valueadd = Integer.parseInt((input1.split(" "))[2]);

        //             String name = (input1.split(" "))[1];



        //             login = true;

        //             //dos.writeUTF("login|"+name+"|"+String.valueOf(valueadd));

        //             //return;//need to check
        //         } catch (NumberFormatException e) {
        //             System.out.println((input1.split(" "))[2] + " is not a valid integer please try again or type break to quit");

        //         }

        //     }else if(input1.equals("break")){
        //         System.out.println("Goodbye");
        //         return;
        //     }
        //     else{
        //         System.out.println("Please Login first and input a valid value to deposit");
        //     }
        // }

        // while(true){
        // String input = cons.readLine("Hello there, Bet or deal");{
        // }
        // }   
    }
}

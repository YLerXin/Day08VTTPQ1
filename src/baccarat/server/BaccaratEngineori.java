package baccarat.server;

import java.io.*;
import java.math.BigInteger;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BaccaratEngineori implements Runnable {

    public static void resetGameHistory(){
        try {
            FileWriter fw = new FileWriter("game_history.csv", false);
            fw.write("");
            System.out.println("Game has been Reset");
        }catch(IOException e){
            e.printStackTrace();
        }
    }
    private List<String> cards;
    private Socket clientsoc;
    private volatile List<String> gameHistory = new ArrayList<>();

    public BaccaratEngineori(Socket clientsoc){
        this.clientsoc = clientsoc;
        this.cards = loadCards();

    }

    @Override
    public void run(){
        try{
            InputStreamReader isr = new InputStreamReader(clientsoc.getInputStream());
            BufferedReader br = new BufferedReader(isr);

            PrintWriter out = new PrintWriter(clientsoc.getOutputStream(),true);

            String inputLine;
            BigInteger betAmount = BigInteger.ZERO;
            File dir = null;

            while ((inputLine = br.readLine()) != null){
                String[] commandParts = inputLine.split("\\|");
                String command = commandParts[0].toLowerCase();
                switch(command){
                    case "login":
                        String username = commandParts[1];
                        BigInteger balance = new BigInteger(commandParts[2]);
                        try (FileWriter writer = new FileWriter(username + ".db")) {
                            writer.write(String.valueOf(balance));
                        }
                        out.println("User " + username + " logged in with balance: " + balance);
                    break;


                    case "bet":
                        System.out.println(commandParts);
                        username = commandParts[2];
                        betAmount = new BigInteger(commandParts[1]);
                        balance = getBalance(username);
                        if (balance.compareTo(betAmount) < 0) {
                            out.println("insufficient amount");
                        } else {
                            out.println(username +" - Bet of " + betAmount + " placed.");
                        }
                        break;

                    case "deal":
                    username = commandParts[2];
                    String side = commandParts[1];
                    balance = getBalance(username);
                    String result = dealCards(side);
                    System.out.println(result);
                        if (result.contains("wins")) {
                            if ((side.equals("B") && result.contains("Banker wins")) 
                                || (side.equals("P") && result.contains("Player wins"))) {
                                //balance += betAmount * 2;
                                balance = balance.add(betAmount);
                                System.out.println(balance);
                                updateBalance(username, balance);
                                out.println("Bet won. Balance updated: " + balance);
                            } else {
                                balance = balance.subtract(betAmount);
                                out.println("Bet lost. Balance remains: " + balance);
                                System.out.println(balance);
                                updateBalance(username, balance);
                            }
                        } else {
                            out.println("It's a draw. Bet refunded.");
                            System.out.println(balance);
                        }
                    break;

                    case "exit":
                    break;

                    default:
                        out.println("Unknown Command: " + command);
                        break;
                }
            }
        }catch(IOException e){
            e.printStackTrace();
        }



    }
    private synchronized List<String> loadCards() {
        List<String> cards = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader("cards.db"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                cards.add(line);
            }
        } catch (IOException e) {
            System.out.println("Error loading cards: " + e.getMessage());
        }
        return cards;
    }
    
    private String dealCards(String side) {
        
        if (cards.size() < 4) {
            return "Not enough cards to deal.";
        }

        int playerSum = 0;
        int bankerSum = 0;
        List<String> playerCards = new ArrayList<>();
        List<String> bankerCards = new ArrayList<>();

        // Draw initial two cards for Player and Banker
        for (int i = 0; i < 2; i++) {
            String playerCard = cards.remove(0);
            playerCards.add(playerCard);
            playerSum += getCardValue(playerCard);

            String bankerCard = cards.remove(0);
            bankerCards.add(bankerCard);
            bankerSum += getCardValue(bankerCard);
        }

        // Draw third card if necessary (sum <= 15)
        if (playerSum <= 15) {
            String playerCard = cards.remove(0);
            playerCards.add(playerCard);
            playerSum += getCardValue(playerCard);
        }

        if (bankerSum <= 15) {
            String bankerCard = cards.remove(0);
            bankerCards.add(bankerCard);
            bankerSum += getCardValue(bankerCard);
        }

        // Determine result
        String result;
        System.out.println("playerSum > " + playerSum);
        System.out.println("bankerSum > " + bankerSum);
        int[] milestones = {10, 20}; // Array to store milestone values

        if(playerSum >= 10 && playerSum <= 20){
            System.out.println("P more than 10 and less than 20");
            playerSum = playerSum - milestones[0];
        }

        if(playerSum > 20){
            System.out.println("P more than 20");
            playerSum = playerSum - milestones[1];
        }

        if(bankerSum  >= 10 && bankerSum  <= 20){
            System.out.println("B more than 10 and less than 20");
            bankerSum = bankerSum - milestones[0];
        }

        if(bankerSum > 20){
            System.out.println("B more than 20");
            bankerSum = bankerSum - milestones[1];
        } 
        System.out.println("aft playerSum > " + playerSum);
        System.out.println("aft bankerSum > " + bankerSum);

        if (playerSum > bankerSum) {
            result = "Player wins with " + playerSum + " points.";
        } else if (bankerSum > playerSum) {
            result = "Banker wins with " + bankerSum + " points.";
        } else {
            result = "Draw";
        }

        synchronized (gameHistory) {
            if (result.contains("Banker wins")) {
                gameHistory.add("B");
            } if(result.contains("Player wins")) {
                gameHistory.add("P");
            }else if(result.contains("Draw")){
                gameHistory.add("D");
            }

            // Write to history if game count reaches 6
            if (gameHistory.size() == 6) {
                writeGameHistory(new ArrayList<>(gameHistory));
                gameHistory.clear();
            }
            
            // writeGameHistory(new ArrayList<>(gameHistory));
            // if(gameHistory.size() == 6)
            //     gameHistory = new ArrayList<>();
            System.out.println(">>>> " + gameHistory);
        }

        
        // Save the updated cards list back to "cards.db"
        saveCards();

        // Construct response string
        String playerCardsString = "P|" + String.join("|", playerCards);
        String bankerCardsString = "B|" + String.join("|", bankerCards);

        return playerCardsString + "," + bankerCardsString + " - " + result;
    }

    private int getCardValue(String card) {
        int value = Integer.parseInt(card.split("\\.")[0]);
        return value;
    }

    private synchronized void saveCards() {
        // Save the shuffled cards to a data file named "cards.db"
        try (FileWriter writer = new FileWriter("cards.db")) {
            for (String card : cards) {
                writer.write(card + "\n");
            }
            System.out.println("Shuffled cards saved to cards.db");
        } catch (IOException e) {
            System.out.println("Error writing to cards.db: " + e.getMessage());
        }
    }

    private BigInteger getBalance(String username) {
        try (BufferedReader reader = new BufferedReader(new FileReader(username + ".db"))) {
            return new BigInteger(reader.readLine());
        } catch (IOException e) {
            System.out.println("Error reading balance for user " + username + ": " + e.getMessage());
            return BigInteger.ZERO;
        }
    }

    private void updateBalance(String username, BigInteger balance) {
        try (FileWriter writer = new FileWriter(username + ".db")) {
            writer.write(String.valueOf(balance));
        } catch (IOException e) {
            System.out.println("Error updating balance for user " + username + ": " + e.getMessage());
        }
    }
    private static synchronized void writeGameHistory(List<String> gameHistorySnapshot) {
        try (FileWriter csvWriter = new FileWriter("game_history.csv", true)) {
            synchronized (gameHistorySnapshot) {
                if (!gameHistorySnapshot.isEmpty()) {
                    csvWriter.append(String.join(",", gameHistorySnapshot)).append("\n");
                }
            }
        } catch (IOException e) {
            System.out.println("Error writing game history: " + e.getMessage());
        }
    }


    
    
}

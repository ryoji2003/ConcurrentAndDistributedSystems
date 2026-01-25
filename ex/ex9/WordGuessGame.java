import java.io.*;
import java.net.*;
import java.util.*;

public class WordGuessGame {
    static final int PORT = 12345;
    static final String WORD_FILE = "words_for_task_3_9.txt";

    // --- Server Side ---
    static void runServer() {
        List<String> words = new ArrayList<>();
        
        // 1. Load words from file
        try (BufferedReader br = new BufferedReader(new FileReader(WORD_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                if(!line.trim().isEmpty()) words.add(line.trim());
            }
        } catch (IOException e) {
            System.out.println("Error reading word file: " + e.getMessage());
            return;
        }

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Word Guess Server started on port " + PORT);

            while (true) {
                System.out.println("Waiting for client...");
                // 2. Wait for connection
                try (Socket client = serverSocket.accept();
                     PrintWriter out = new PrintWriter(client.getOutputStream(), true);
                     BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()))) {
                    
                    System.out.println("Client connected.");
                    
                    // 3. Select random word
                    String targetWord = words.get(new Random().nextInt(words.size()));
                    char[] currentGuess = new char[targetWord.length()];
                    Arrays.fill(currentGuess, '*');
                    
                    boolean finished = false;

                    // 4. Game Loop
                    while (!finished) {
                        String status = new String(currentGuess);
                        out.println(status); // Send status (e.g. "**o*")

                        // Check win condition
                        if (status.equals(targetWord)) {
                            finished = true;
                            break; // Exit loop, closing connection
                        }

                        // Read character from client
                        String input = in.readLine();
                        if (input == null) break; // Client disconnected
                        
                        if (!input.isEmpty()) {
                            char guessChar = input.charAt(0);
                            // Update masked word
                            for (int i = 0; i < targetWord.length(); i++) {
                                if (targetWord.charAt(i) == guessChar) {
                                    currentGuess[i] = guessChar;
                                }
                            }
                        }
                    }
                    System.out.println("Game over for this client.");
                } catch (IOException e) {
                    System.out.println("Connection error: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.out.println("Server error: " + e.getMessage());
        }
    }

    // --- Client Side ---
    static void runClient(String host) {
        try (Socket socket = new Socket(host, PORT);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in))) {

            System.out.println("Connected to server.");

            String fromServer;
            while ((fromServer = in.readLine()) != null) {
                System.out.print("server> " + fromServer);
                
                // Check if game is won (no '*' left)
                if (!fromServer.contains("*")) {
                    System.out.println("\nYou won! The word was " + fromServer);
                    break;
                }
                System.out.println();

                // User input
                System.out.print("client> ");
                String userInput = stdIn.readLine();
                if (userInput != null) {
                    out.println(userInput);
                } else {
                    break;
                }
            }
        } catch (IOException e) {
            System.out.println("Client error: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        if (args.length > 0 && args[0].equals("client")) {
            String host = (args.length > 1) ? args[1] : "127.0.0.1";
            runClient(host);
        } else {
            runServer();
        }
    }
}
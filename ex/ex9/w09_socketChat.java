import java.net.*;
import java.io.*;
import java.util.Vector;

public class  w09_socketChat{
    static final int port = 12345;

    static class AllSockets {
        static private Vector<Socket> allSockets = new Vector<Socket>();

        synchronized static public void Add(Socket client) { allSockets.add(client); }
        
        synchronized static public void Remove(Socket client) {
            allSockets.remove(client);
            try { client.close(); } catch (IOException e) { }
        }
        
        synchronized static public void Broadcast(Socket from, String str) throws Exception {
            for(Socket s : allSockets) {
                if(s != from && !s.isClosed()) {
                    BufferedWriter out = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()));
                    out.write(str);
                    out.newLine();
                    out.flush();
                }
            }
        }
    }

    static class BroadcastTask implements Runnable {
        Socket client;
        public BroadcastTask(Socket c) { client = c; }
        public void run() {
            try (BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()))) {
                String msg;
                while ((msg = in.readLine()) != null) {
                    AllSockets.Broadcast(client, msg);
                }
            } catch (Exception e) {
            } finally {
                AllSockets.Remove(client);
            }
        }
    }

    static void Server() throws Exception {
        ServerSocket s = new ServerSocket(port);
        System.out.println("Server started...");
        while(true) {
            Socket client = s.accept();
            AllSockets.Add(client);
            new Thread(new BroadcastTask(client)).start();
        }
    }

    static void Client(String host, String nickname) throws Exception {
        Socket s = new Socket(host, port);
        
        new Thread(() -> {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(s.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) System.out.println(line);
            } catch (IOException e) { }
        }).start();

        try (BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
             BufferedWriter out = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()))) {
            while (true) {
                String msg = in.readLine();
                if (msg == null || msg.equals("*quit")) {
                    break;
                }                out.write(nickname + "> " + msg);
                out.newLine();
                out.flush();
            }
        } finally {
            s.close();
            System.exit(0);
        }
    }

    public static void main(String args[]) {
        try {
            if(args.length > 0 && args[0].equals("client")) {
                String host = args[1];
                String nickname = (args.length > 2) ? args[2] : "Guest";
                Client(host, nickname);
            } else {
                Server();
            }
        } catch(Exception e) {
            System.out.println("Usage: java ImprovedChat client <IP> <Nickname> OR java ImprovedChat server");
        }
    }
}
import java.net.*;
import java.io.*;
import java.util.Vector;

public class ImprovedChat
{
    static final int port = 12345;
    
    static BufferedReader getReader(InputStream is) {
        return new BufferedReader(new InputStreamReader(is));
    }
    
    static BufferedWriter getWriter(OutputStream os) {
        return new BufferedWriter(new OutputStreamWriter(os));
    }

    static void writeString(String str, BufferedWriter out) throws Exception {
        out.write(str, 0, str.length());
        out.newLine();
        out.flush();
    }

    static class AllSockets {
        static private Vector<Socket> allSockets = new Vector<Socket>();

        synchronized static public void Add(Socket client) { allSockets.add(client); }
        
        synchronized static public void Remove(Socket client) {
            if (allSockets.remove(client)) {
                System.out.println("Client disconnected. Total clients: " + allSockets.size());
                try {
                    client.close();
                } catch (IOException e) {

                }
            }
        }
        
        synchronized static public void Broadcast(Socket from, String str) throws Exception {
            for(Socket s : allSockets) {
                if(s != from && !s.isClosed()) {
                    try {
                        writeString(str, getWriter(s.getOutputStream()));
                    } catch (Exception e) {
                    }
                }
            }
        }
    }
    
    static class EchoIncoming implements Runnable {
        BufferedReader reader;
        Socket socket;

        public EchoIncoming(BufferedReader r, Socket s) { 
            reader = r; 
            socket = s;
        } 

        public void run() {
            try {
                String line;
                while(!socket.isClosed() && (line = reader.readLine()) != null)
                    System.out.println(line);
            }
            catch(Exception e) {
            }
        }
    }

    static void Client(String host, String nickname) throws Exception {
        System.out.println("Connecting to " + host + " as " + nickname + "...");
        Socket s = new Socket(host, port);
        
        new Thread(new EchoIncoming(getReader(s.getInputStream()), s)).start();
        
        BufferedReader in = getReader(System.in);
        BufferedWriter out = getWriter(s.getOutputStream());
        
        for(;;) {
            String msg = in.readLine();
            if(msg == null) break;

            if (msg.equals("*quit")) {
                System.out.println("Quitting chat...");
                s.close();
                System.exit(0);
            }

            writeString(nickname + "> " + msg, out);
        }
    }
    
    static class Broadcast implements Runnable {
        Socket client;
        
        public Broadcast(Socket c) { client = c; }
        
        public void run() {
            try {
                BufferedReader in = getReader(client.getInputStream());
                
                for(;;) {
                    String msg = in.readLine();
                    if (msg == null) break;
                    AllSockets.Broadcast(client, msg);
                }
            }
            catch(Exception e) {
            } 
            finally {
                AllSockets.Remove(client);
            }
        }
    }
        
    static void Server() throws Exception {
        ServerSocket s = new ServerSocket(port);
        System.out.println("Server started on port " + port);
        
        for(;;) {
            Socket client = s.accept();
            System.out.println("New client connected");
            AllSockets.Add(client);
            new Thread(new Broadcast(client)).start();
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
        }       
        catch(Exception e) {
            e.printStackTrace();
            System.out.println("Usage:");
            System.out.println("  Server: java ImprovedChat server");
            System.out.println("  Client: java ImprovedChat client <IP> <Nickname>");
        }
    }
}
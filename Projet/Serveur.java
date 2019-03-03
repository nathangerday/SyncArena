
import java.io.*;
import java.net.*;
 
class Serveur {
    public static void main(String args[]) throws Exception {
        String fromClient;
        String toClient;
        int port = 8080;
        ServerSocket server = new ServerSocket(port);
        System.out.println("wait for connection on port 8080");
 
        boolean run = true;
        while(run) {
            Socket client = server.accept();
            System.out.println("got connection on port 8080");
            BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
            PrintWriter out = new PrintWriter(client.getOutputStream(),true);
 
            fromClient = in.readLine();
            System.out.println("received: " + fromClient);
            Thread.sleep(2000);
            System.out.println("Sending data");
            out.println(3.32423);
            Thread.sleep(5000);
            out.println("Truc123");
            out.println("Byebye");
 
        }
        System.exit(0);
    }
}
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Server {
    static ArrayList<PrintWriter> connections = new ArrayList<>();

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(7777);
        while (true) {
            Socket socket = serverSocket.accept();
            connections.add(new PrintWriter(socket.getOutputStream()));
            Thread thread = new Thread(new ServerListener(socket));
            thread.start();
        }
    }

    static class ServerSender implements Runnable {
        String message;

        public ServerSender(String val) {
            message = val;
        }

        @Override
        public void run() {
            System.out.println("I am ServerSender thread. Hi!");
            final int cnt = connections.size();
            for (int i = 0; i < cnt; ++i) {
                connections.get(i).write(message + "\n");
                connections.get(i).flush();
            }
        }
    }

    static class ServerListener implements Runnable {
        BufferedReader br;

        public ServerListener(Socket socket) throws IOException {
            br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        }

        @Override
        public void run() {
            System.out.println("I am Listener thread. Hi!");
            String msg;
            try {
                while ((msg = br.readLine()) != null) {
                    System.out.println("Listener listens: " + msg.length() + "bytes");
                    Thread senderThread = new Thread(new ServerSender(msg));
                    senderThread.start();
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {


            }
        }
    }
}


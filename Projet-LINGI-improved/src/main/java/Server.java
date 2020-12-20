import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

public class Server {

    private ServerSocket server;
    private Socket client;
    private ReadFile dbData;
    private int port;


    public Server(int port) {
        this.port = port;
        dbData = new ReadFile("data/dbdata.txt");
    }

    public void launchServer() {
        Thread thread = new Thread(new Runnable() {
            public void run() {
                try {
                    server = new ServerSocket(port, 2);
                    server.setReuseAddress(true);
                    while (true) {
                        try {
                            client = server.accept();

                            System.out.println("The client " + client.getInetAddress().getHostAddress() + " is connected");

                            Thread t = new Thread(new ClientHandler(client));
                            t.start();

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (server != null) {
                        try {
                            server.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });
        thread.start();
    }

    private class ClientHandler implements Runnable {
        private ObjectOutputStream oos;
        private ObjectInputStream ois;
        private int nbRequest;
        private Socket clientSocket;

        public ClientHandler(Socket clientSocket) {
            this.clientSocket = clientSocket;
        }

        public void run() {
            while (!clientSocket.isClosed()) {
                try {
                    oos = new ObjectOutputStream(clientSocket.getOutputStream());
                    ois = new ObjectInputStream(clientSocket.getInputStream());

                    String fromReader = (String) ois.readObject();
                    System.out.println(fromReader);
                    List<String> response = dbData.readIt(fromReader);

                    oos.writeObject(response);
                    oos.flush();
                    System.out.println(++nbRequest);

                } catch (IOException | ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void main(String[] args) throws IOException {
        Server server = new Server(Integer.parseInt(args[0]));
        server.launchServer();
    }
}

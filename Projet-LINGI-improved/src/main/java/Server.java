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
        Thread thread = new Thread(() -> {
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
        });
        thread.start();
    }

    private class ClientHandler implements Runnable {
        private ObjectOutputStream oos;
        private ObjectInputStream ois;
        private int nbRequest;
        private Socket clientSocket;
        private String idClient;

        public ClientHandler(Socket clientSocket) {
            this.clientSocket = clientSocket;
            this.idClient = clientSocket.getInetAddress().getHostAddress();
        }

        public void run() {
            try {
                while (!clientSocket.isClosed()) {
                    ois = new ObjectInputStream(clientSocket.getInputStream());
                    String fromReader = (String) ois.readObject();
                    System.out.println(fromReader);
                    String response = dbData.readIt(fromReader);
                    oos = new ObjectOutputStream(clientSocket.getOutputStream());
                    oos.writeObject(response);
                    oos.flush();
                    System.out.println(++nbRequest);
                }
            }
            catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                System.out.println("Connexion with client " + idClient + " is closed.");
            } finally {
                try {
                    ois.close();
                    oos.close();
                    clientSocket.close();
                }
                catch (IOException e) {
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

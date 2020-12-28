import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Server implements Runnable {

    private ServerSocket server;
    private ReadFile dbData;
    private int port;
    protected boolean isStopped = false;
    private ExecutorService threadPool = Executors.newFixedThreadPool(3);

    public Server(int port) {
        this.port = port;
        dbData = new ReadFile("data/dbdata.txt");
    }

    private synchronized boolean isStopped() {
        return isStopped;
    }

    private void openServerSocket() {
        try {
            server = new ServerSocket(port, 5);
            server.setReuseAddress(true);
        } catch (IOException e) {
            throw new RuntimeException("Cannot open port " + port, e);
        }
    }

    public synchronized void stop(){
        this.isStopped = true;
        try {
            if (server != null)
                server.close();
        } catch (IOException e) {
            throw new RuntimeException("Error closing server", e);
        }
    }

    public void run() {
        openServerSocket();

        try {
            while (true){
                Socket socket = server.accept();
                this.threadPool.submit(
                        new ClientHandler(socket));
            }
        } catch (IOException e) {
            if (isStopped()) {
                System.out.println("Server stopped.");
            }
            throw new RuntimeException("Error accepting client connection", e);
        }
        finally {
            threadPool.shutdown();
            try {
                if (!threadPool.awaitTermination(60, TimeUnit.MILLISECONDS)) {
                    threadPool.shutdownNow();
                }
            } catch (InterruptedException e) {
                threadPool.shutdownNow();
            }
            System.out.println("Server stopped.");
        }
    }


    private class ClientHandler implements Runnable {
        private ObjectOutputStream oos;
        private ObjectInputStream ois;
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
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                System.out.println("Connexion with client " + idClient + " is closed.");
            } finally {
                try {
                    ois.close();
                    oos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void main(String[] args) throws IOException {
        Server server = new Server(Integer.parseInt(args[0]));
        new Thread(server).start();
    }
}

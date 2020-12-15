import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

public class Server {

    private boolean isRunning;
    private ServerSocket ss;
    private Socket socket;
    ReadFile dbData;


    public Server(int port) {
        try {
            ss = new ServerSocket(port, 2);
            dbData = new ReadFile("src/main/data/dbdata.txt");
        } catch (IOException e) {
            e.printStackTrace();
        }
        isRunning = true;
    }

    public void close(){
        isRunning = false;
    }

    public void launchServer() {
        Thread thread = new Thread(new Runnable() {
            public void run() {
                while (isRunning) {
                    try {
                        socket = ss.accept();

                        System.out.println("The client is connected");

                        Thread t = new Thread(new SocketProcess());
                        t.start();

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                try {
                    ss.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    ss = null;
                }
            }
        });
        thread.start();
    }

    private class SocketProcess implements Runnable {
        private ObjectOutputStream oos;
        private ObjectInputStream ois;
        private int nbRequest;

        public void run() {
            while (!socket.isClosed()) {
                try {
                    oos = new ObjectOutputStream(socket.getOutputStream());
                    ois = new ObjectInputStream(socket.getInputStream());

                    String fromReader = (String) ois.readObject();
                    System.out.println(fromReader);
                    List<String> response = dbData.readIt(fromReader);

                    oos.writeObject(response);
                    oos.flush();
                    System.out.println(++nbRequest);

                } catch (IOException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
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

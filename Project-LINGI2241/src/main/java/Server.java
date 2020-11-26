import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

public class Server {

    private boolean isRunning;
    private ServerSocket ss;
    private Socket socket;

    public Server(int port) {
        try {
            ss = new ServerSocket(port);
        } catch (UnknownHostException e) {
            e.printStackTrace();
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

                        Thread t = new Thread(new SocketProcess(socket));
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

    public static void main(String[] args) throws IOException {
        Server server = new Server(4999);
        server.launchServer();
    }
}

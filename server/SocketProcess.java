import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class SocketProcess implements Runnable {
    private Socket socket;
    private PrintWriter pr;
    private InputStreamReader in;
    private BufferedReader bf ;

    public SocketProcess(Socket socket) {
        this.socket = socket;
    }

    public void run() {
        boolean isClose = false;
        while (!socket.isClosed()) {
            try {
                pr = new PrintWriter(socket.getOutputStream());
                in = new InputStreamReader(socket.getInputStream());
                bf = new BufferedReader(in);

                String fromReader = bf.readLine();
                System.out.println(fromReader);

                String response = "Bonjourjour!!!\n";
                pr.println(response);
                pr.flush();

                if (isClose) {
                    pr = null;
                    in = null;
                    socket.close();
                    break;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

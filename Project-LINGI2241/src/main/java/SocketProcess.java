import java.io.*;
import java.net.Socket;

public class SocketProcess implements Runnable {
    private Socket socket;
    private PrintWriter pr;
    private ObjectOutputStream outputStream;
    private ObjectInputStream inputStream;

    public SocketProcess(Socket socket) {
        this.socket = socket;
    }

    public void run() {
        ReadFile dbData = new ReadFile("src/main/data/dbdata.txt");
        boolean isClose = false;
        while (!socket.isClosed()) {
            try {
                pr = new PrintWriter(socket.getOutputStream());
                outputStream = new ObjectOutputStream(socket.getOutputStream());
                inputStream = new ObjectInputStream(socket.getInputStream());

                String fromReader = (String) inputStream.readObject();
                System.out.println(fromReader);
                String response = dbData.readIt(fromReader);
                /*
                if (response.equals("close")){
                    socket.close();
                    return;
                }
                */
                outputStream.writeObject(response);
                outputStream.flush();

            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }
}

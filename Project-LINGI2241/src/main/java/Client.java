import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class Client implements Runnable{
    private Socket socket;
    private PrintWriter pr;
    private InputStreamReader in;
    private BufferedReader bf ;

    public Client(int port) {
        try {
            socket = new Socket("localhost", port);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        for (int i = 0; i < 10; i++) {
            try {
                Thread.currentThread().sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            try {
                pr = new PrintWriter(socket.getOutputStream());
                in = new InputStreamReader(socket.getInputStream());
                bf = new BufferedReader(in);

                sendRequest();

                String response = bf.readLine();
                System.out.println(response);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public String generateRandomRequest() {
        ThreadLocalRandom random = ThreadLocalRandom.current();

        int randomLength = random.nextInt(1, 11);

        String requestToSend = "";
        for (int j = 0; j < randomLength; j++) {
            requestToSend += String.valueOf(random.nextInt(0, 10));
        }

        String[] regex = new String[]{"\\*", "\\,", "[0-9]", "\\#", "\\W", "\\^", "\\s", "\\?", "[a-z]"};

        return requestToSend + regex[randomLength];
    }

    public void sendRequest() {
        Random rand = new Random();
        pr.println(Double.toString(rand.nextDouble()));
        pr.flush();
    }

    public static void main(String[] args) throws IOException {
        Client client = new Client(4999);
        client.run();
    }
}

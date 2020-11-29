import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.ThreadLocalRandom;

public class Client implements Runnable{
    private Socket socket;
    private PrintWriter pr;
    private InputStreamReader in;
    private BufferedReader bf ;
    private ThreadLocalRandom random;
    private String[] regex;
    private int nbRequest;

    public Client(int port) {
        regex = new String[]{"\\*", "\\,", "\\[", "\\#", "\\W", "\\^", "\\s", "\\?", "\\!", "\\]", "\\("};
        try {
            socket = new Socket("localhost", port);
        } catch (IOException e) {
            e.printStackTrace();
        }
        nbRequest = 0;
        random = ThreadLocalRandom.current();
    }

    public void run() {
        try {
            pr = new PrintWriter(socket.getOutputStream());
            in = new InputStreamReader(socket.getInputStream());
            bf = new BufferedReader(in);
        } catch (IOException e) {
            e.printStackTrace();
        }
        int i = 0;
        while (true) {
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            try {

                sendRequest();
                String response = bf.readLine();
                System.out.println(response);


            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public String generateRandomRequest() {
        int randomLength = random.nextInt(1, 10);
        StringBuilder requestToSend = new StringBuilder();
        for (int j = 0; j < randomLength; j++) {
            int item = random.nextInt(1, 9);
            if (requestToSend.indexOf(String.valueOf(item)) == -1)
                requestToSend.append(item + ",");
        }
        nbRequest++;

        requestToSend.append(";");
        requestToSend.append(regex[randomLength]);

        return requestToSend.toString();
    }

    public void sendRequest() {
        if (nbRequest == 11){
            pr.println("close");
        }
        else
            pr.println(generateRandomRequest());
        pr.flush();
    }

    public static void main(String[] args) {
        Client client = new Client(4999);
        client.run();
    }
}

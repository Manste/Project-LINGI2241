import java.io.*;
import java.net.Socket;
import java.util.concurrent.ThreadLocalRandom;

public class Client implements Runnable{
    private Socket socket;
    private ThreadLocalRandom random;
    private String[] regex;
    private int nbRequest;
    private OutputStream outStream;
    private InputStream inputStream;
    private ObjectOutputStream oos;
    private ObjectInputStream ois;
    private int idClient;
    private boolean toClose;
    private Long[][] rows;

    public Client(int port) {
        regex = new String[]{"\\*", "\\,", "\\[", "\\#", "\\^", "\\?", "\\!", "\\]", "\\("};
        nbRequest = 5;
        toClose = false;
        random = ThreadLocalRandom.current();
        try {
            socket = new Socket("localhost", port);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    public void run() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Thread sendingRequest = new Thread(new Sender());
        Thread receivingResponse = new Thread(new Reader());

        sendingRequest.start();
        receivingResponse.start();

        try {
            sendingRequest.join();
            receivingResponse.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    class Sender implements Runnable {
        public void run() {
            try {
                while (true){
                    inputStream = socket.getInputStream();
                    ois = new ObjectInputStream(inputStream);
                    String response = (String) ois.readObject();
                    System.out.println(response);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    class Reader implements Runnable {
        public void run() {
            try {
                while (nbRequest != 0){
                    outStream = socket.getOutputStream();
                    oos = new ObjectOutputStream(outStream);
                    oos.writeObject(generateRandomRequest());
                    oos.flush();
                    nbRequest--;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public String generateRandomRequest() {
        int randomLength = random.nextInt(1, 6);
        StringBuilder requestToSend = new StringBuilder();
        for (int j = 0; j < randomLength; j++) {
            int item = random.nextInt(0, 6);
            if (requestToSend.indexOf(String.valueOf(item)) == -1)
                requestToSend.append(item).append(",");
        }

        requestToSend.append(";");
        requestToSend.append(regex[randomLength]);
        requestToSend.append("\n");

        return requestToSend.toString();
    }

    public static void main(String[] args) {
        Client client = new Client(Integer.parseInt(args[0]));
        client.run();
    }
}

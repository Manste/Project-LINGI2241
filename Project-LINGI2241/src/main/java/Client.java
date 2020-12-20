import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
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
    private String idClient;
    int nbResponse;

    public Client(int port) throws IOException {
        regex = new String[]{"\\*", "(to)+", "\\#", "[0-9]{4}", "\\?", "a{2}", "\\]", "[0-9&&[^123]]", "a{2,4}"};
        nbRequest = 10;
        random = ThreadLocalRandom.current();
        nbResponse = 0;
        socket = new Socket("localhost", port);
        idClient = socket.getInetAddress().getHostAddress();
        inputStream = socket.getInputStream();
        outStream = socket.getOutputStream();
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

    class Reader implements Runnable {
        public void run() {
            try {
                while (true){
                    ois = new ObjectInputStream(inputStream);
                    ArrayList<String> response = (ArrayList<String>) ois.readObject();
                    System.out.println(printArray(response.toArray(new String[0])));
                    System.out.println(++nbResponse);
                }
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    class Sender implements Runnable {
        public void send(String txt) throws IOException {
            oos.writeObject(txt);
            oos.flush();
        }
        public void run() {
            try {
                while (nbRequest != 0){
                    oos = new ObjectOutputStream(outStream);
                    send(generateRandomRequest());
                    nbRequest--;
                    try {
                        Thread.sleep(100L *random.nextInt(1, 10));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                send("Client " + idClient + " has finished.");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public String generateRandomRequest() {
        int randomLength = random.nextInt(1, 6); // Fix the random length of the request list
        int randomDataTypeChoice = random.nextInt(0, 2);
        StringBuilder requestToSend = new StringBuilder();
        if (randomDataTypeChoice == 1){
            for (int j = 0; j < randomLength; j++) {
                int item = random.nextInt(0, 6);
                if (requestToSend.indexOf(String.valueOf(item)) == -1)
                    requestToSend.append(item).append(",");
            }

        }
        requestToSend.append(";");
        requestToSend.append(regex[randomLength]);
        requestToSend.append("\n");

        return requestToSend.toString();
    }

    private String printArray(String[] data){
        StringBuilder str = new StringBuilder();
        for (String s : data)
            str.append(s);
        return str.toString();
    }

    public static void main(String[] args) {
        Client client = null;
        try {
            client = new Client(Integer.parseInt(args[0]));
            client.run();
        } catch (IOException e) {
            System.err.println("Cannot Launch the client.");
            e.printStackTrace();
        }
    }
}

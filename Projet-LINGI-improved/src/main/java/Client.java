import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

public class Client implements Runnable{
    private Socket socket;
    private ThreadLocalRandom random;
    private String[] regex;
    private int nbRequestSended;
    private OutputStream outStream;
    private InputStream inputStream;
    private ObjectOutputStream oos;
    private ObjectInputStream ois;
    private String idClient;
    private FileWriter csvWriter;
    private int nbResponse;
    private Instant[][] rows;
    private int fixedNbRequests;


    public Client(int port) throws IOException {
        regex = loadRegex("data/regex.txt");
        random = ThreadLocalRandom.current();
        socket = new Socket("localhost", port);
        idClient = socket.getInetAddress().getHostAddress();
        inputStream = socket.getInputStream();
        outStream = socket.getOutputStream();
        csvWriter = new FileWriter("data/dataTime.csv");
        setCsvWriter("Departed,Arrived,Difference");
        nbRequestSended = 0;
        fixedNbRequests = 5;
        nbResponse = 0;
        rows = new Instant[fixedNbRequests][];
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
        } finally {
            writeRowIntoCsv(rows);
        }

    }

    class Reader implements Runnable {
        public void run() {
            try {
                while (true){
                    ois = new ObjectInputStream(inputStream);
                    ArrayList<String> response = (ArrayList<String>) ois.readObject();
                    rows[nbResponse++][1] = Instant.now();
                    System.out.println(printArray(response.toArray(new String[0])));
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            catch (IOException e){
                try {
                    inputStream.close();
                    ois.close();
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
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
                while (nbRequestSended < fixedNbRequests){
                    oos = new ObjectOutputStream(outStream);
                    rows[nbRequestSended] = new Instant[2];
                    send(generateRandomRequest());
                    rows[nbRequestSended++][0] = Instant.now();
                    Thread.sleep(100L *random.nextInt(1, 10));
                }
                send("Client " + idClient + " has finished.");
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (IOException e) {
                try {
                    outStream.close();
                    oos.close();
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }

            }
        }
    }

    private String[] loadRegex(String filename) {
        ArrayList<String> arrayList = new ArrayList<>();
        try {
            Files.lines(Paths.get(filename)).forEach(arrayList::add);

        }catch (IOException e) {
            e.printStackTrace();
        }
        return arrayList.toArray(new String[0]);
    }

    public String generateRandomRequest() {
        int randomLength = random.nextInt(1, regex.length); // Fix the random length of the request list
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

    public void setCsvWriter(String str) throws IOException {
        csvWriter.append(str);
        csvWriter.append("\n");
        csvWriter.flush();
    }

    public void writeRowIntoCsv(Instant[][] rows){
        try {
            for (Instant[] instants : rows) {
                StringBuilder str = new StringBuilder();
                if (instants[0] == null || instants[1] == null) continue;
                str.append(instants[0].toString()).append(",").append(instants[1])
                        .append(",").append(Duration.between(instants[0], instants[1]).toMillis());
                setCsvWriter(str.toString());
            }
            csvWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Thread client = null;
        try {
            client = new Thread(new Client(Integer.parseInt(args[0])));
            client.start();
            client.join();
        } catch (IOException | InterruptedException e) {
            System.err.println("Cannot Launch the client.");
            e.printStackTrace();
        }
    }
}
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
    private BufferedReader bf;
    private InputStreamReader in;
    private String idServer;
    private FileWriter csvWriter;
    private int nbResponse;
    private Instant[][] rows;
    private int fixedNbRequests;
    private PrintWriter pr;


    public Client(String serverIp, String port, String dataFilePath) throws IOException {
        regex = loadRegex("data/regex.txt");
        random = ThreadLocalRandom.current();
        socket = new Socket(serverIp, Integer.parseInt(port));
        idServer = socket.getInetAddress().getHostAddress();
        fixedNbRequests = 5;
        rows = new Instant[fixedNbRequests][];
        csvWriter = new FileWriter(dataFilePath);
        setCsvWriter("Id;Response Time");
        pr = new PrintWriter(socket.getOutputStream());
        in = new InputStreamReader(socket.getInputStream());
        bf = new BufferedReader(in);
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
            socket.close();
            writeRowIntoCsv(rows);
        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
        }

    }

    class Reader implements Runnable {
        public void run() {
            try {
                String prev = "null";
                while (!socket.isClosed()){
                    String str = bf.readLine();
                    System.out.println(str);
                    if (prev.equals("") && prev.equals(str)) {
                        rows[nbResponse++][1] = Instant.now();
                    }
                    prev = str;
                    if (nbResponse == fixedNbRequests) break;
                }
            }catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    bf.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    class Sender implements Runnable {
        public void send(String txt) throws IOException {
            if (!txt.contains(";")) return;
            pr.println(txt);
            pr.flush();
        }
        public void run() {
            try {
                while (nbRequestSended < fixedNbRequests) {
                    rows[nbRequestSended] = new Instant[2];
                    send(generateRandomRequest());
                    rows[nbRequestSended++][0] = Instant.now();
                    Thread.sleep(100);
                }
            }catch (IOException | InterruptedException e) {
                if (pr != null)
                    pr.close();
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
        int randomLength = random.nextInt(0, 5); // Fix the random length of the request list
        StringBuilder requestToSend = new StringBuilder();
        if (randomLength != 0){
            for (int j = 0; j < randomLength; j++) {
                int item = random.nextInt(0, 6);
                if (requestToSend.indexOf(String.valueOf(item)) == -1)
                    requestToSend.append(item).append(",");
            }
        }
        requestToSend.append(";");
        requestToSend.append(regex[random.nextInt(0, regex.length)]);
        requestToSend.append("\n");

        return requestToSend.toString();
    }

    public void setCsvWriter(String str){
        try {
            csvWriter.append(str);
            csvWriter.append("\n");
            csvWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void writeRowIntoCsv(Instant[][] rows){
        int idThread = 0;
        try {
            for (Instant[] instants : rows) {
                if (instants[0] == null || instants[1] == null) return;
                setCsvWriter(++idThread + ";" + Duration.between(instants[0], instants[1]).toMillis());
            }
            csvWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Thread client = null;
        try {
            client = new Thread(new Client(args[0], args[1], args[2]));
            client.start();
            client.join();
        } catch (IOException | InterruptedException e) {
            System.err.println("Cannot Launch the client.");
            e.printStackTrace();
        }
    }
}
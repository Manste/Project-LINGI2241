import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

public class ClientWaitingResponse implements Runnable{
    private final Socket socket;
    private final ThreadLocalRandom random;
    private final String[] regex;
    private BufferedWriter oos;
    private BufferedReader ois;
    private final String idClient;
    private FileWriter csvWriter;
    private int nbResponse;
    private long[][] rows;
    private int fixedNbRequests;


    public ClientWaitingResponse(String serverIp, String port) throws IOException {
        regex = loadRegex("data/regex.txt");
        csvWriter = new FileWriter("data/dataTime.csv");
        setCsvWriter("Id;Response Time");
        random = ThreadLocalRandom.current();
        socket = new Socket(serverIp, Integer.parseInt(port));
        idClient = socket.getInetAddress().getHostAddress();
        fixedNbRequests = 5;
        rows = new long[fixedNbRequests][];
        ois = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        oos = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
    }

    public void run() {
        try {
            while (nbResponse < fixedNbRequests) {
                String request = generateRandomRequest();
                oos.write(request, 0, request.length());
                oos.flush();
                Instant depart = Instant.now();
                rows[nbResponse] = new long[2];
                rows[nbResponse][0] = nbResponse+1;

                rows[nbResponse++][1] = Duration.between(depart, Instant.now()).toMillis();
                ois.lines().forEach(System.out::println);
            }
        } catch (IOException e) {
            System.out.println("Connexion with server " + idClient + " is closed.");
        } finally {
            try {
                ois.close();
                oos.close();
                socket.close();
                writeRowIntoCsv(rows);
            }
            catch (IOException e) {
                e.printStackTrace();
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

    public void setCsvWriter(String str) throws IOException {
        csvWriter.append(str);
        csvWriter.append("\n");
        csvWriter.flush();
    }

    public void writeRowIntoCsv(long[][] rows){
        if (rows[0] == null) return;
        try {
            for (long[] data : rows) {
                if (data == null) return;
                setCsvWriter(data[0] + ";" + data[1]);
            }
            csvWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Thread client = null;
        try {
            client = new Thread(new ClientWaitingResponse(args[0], args[1]));
            client.start();
            client.join();
        } catch (IOException | InterruptedException e) {
            System.err.println("Cannot Launch the client.");
            e.printStackTrace();
        }
    }
}


import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
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
    private ObjectOutputStream oos;
    private ObjectInputStream ois;
    private final String idClient;
    private FileWriter csvWriter;
    private int nbResponse;
    private long[][] rows;
    private int fixedNbRequests;


    public ClientWaitingResponse(int port) throws IOException {
        regex = loadRegex("data/regex.txt");
        csvWriter = new FileWriter("data/dataTime.csv");
        setCsvWriter("Id;Response Time");
        random = ThreadLocalRandom.current();
        socket = new Socket("localhost", port);
        idClient = socket.getInetAddress().getHostAddress();
        fixedNbRequests = 10;
        rows = new long[fixedNbRequests][];
    }

    public void run() {
        try {
            while (nbResponse < fixedNbRequests) {
                oos = new ObjectOutputStream(socket.getOutputStream());
                oos.writeObject(generateRandomRequest());
                Instant depart = Instant.now();
                rows[nbResponse] = new long[2];
                rows[nbResponse][0] = nbResponse+1;

                ois = new ObjectInputStream(socket.getInputStream());
                rows[nbResponse++][1] = Duration.between(depart, Instant.now()).toMillis();
                String fromReader = (String) ois.readObject();
                System.out.println(fromReader);
            }
        }
        catch (ClassNotFoundException e) {
            e.printStackTrace();
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
                StringBuilder str = new StringBuilder();
                str.append(data[0]).append(";").append(data[1]);
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
            client = new Thread(new ClientWaitingResponse(Integer.parseInt(args[0])));
            client.start();
            client.join();
        } catch (IOException | InterruptedException e) {
            System.err.println("Cannot Launch the client.");
            e.printStackTrace();
        }
    }
}


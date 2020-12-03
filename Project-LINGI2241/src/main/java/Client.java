import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.concurrent.ThreadLocalRandom;

public class Client implements Runnable{
    private Socket socket;
    private ThreadLocalRandom random;
    private String[] regex;
    private int nbRequest;
    private ObjectOutputStream outStream;
    private ObjectInputStream inputStream;
    private FileWriter csvWriter;
    private Long[][] rows;

    public Client(int port) {
        regex = new String[]{"\\*", "\\,", "\\[", "\\#", "\\W", "\\^", "\\s", "\\?", "\\!", "\\]", "\\("};
        try {
            csvWriter = new FileWriter("src/main/data/dataTime.csv");
            rows = new Long[500][3];
            socket = new Socket("localhost", port);
        } catch (IOException e) {
            e.printStackTrace();
        }
        nbRequest = 0;
        random = ThreadLocalRandom.current();
    }

    public void run() {

        int i = 0;
        while (true) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            try {
                outStream = new ObjectOutputStream(socket.getOutputStream());
                inputStream = new ObjectInputStream(socket.getInputStream());

                sendRequest();
                String response = (String) inputStream.readObject();
                /*if (response.equals("close")) {
                    socket.close();
                    return;
                }*/
                System.out.println(response);


            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
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
                requestToSend.append(item).append(",");
        }
        nbRequest++;

        requestToSend.append(";");
        requestToSend.append(regex[randomLength]);

        return requestToSend.toString();
    }

    public void sendRequest() throws IOException {
        if (nbRequest == 200){
            outStream.writeObject("close");
        }
        else
            outStream.writeObject(generateRandomRequest());
        outStream.flush();
    }

    public void setCsvWriter() throws IOException {
        csvWriter.append("Departed");
        csvWriter.append(",");
        csvWriter.append("Arrived");
        csvWriter.append(",");
        csvWriter.append("Topic");
        csvWriter.append("\n");

        /*
        for (List<String> rowData : rows) {
            csvWriter.append(String.join(",", rowData));
            csvWriter.append("\n");
        }*/

        csvWriter.flush();
        csvWriter.close();
    }

    public static void main(String[] args) {
        Client client = new Client(4999);
        client.run();
    }
}

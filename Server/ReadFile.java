import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.locks.ReentrantLock;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ReadFile {
    private String[][] dbData;
    private ArrayList<String> dataToSend;
    private final ReentrantLock lock = new ReentrantLock();
    private BufferedReader in;

    public ReadFile(String filename) {
        loadData(filename);
        in = null;
    }

    private void initDb(ArrayList[] tab) {
        for (int i = 0; i < tab.length; i++) {
             tab[i] = new ArrayList<String>();
        }
    }

    private void completeDbData(ArrayList[] lists){
        dbData = new String[lists.length][];
        for (int i = 0; i < lists.length; i++) {
            dbData[i] = (String[]) lists[i].toArray(new String[lists.length]);
        }
        System.out.println("Database Loaded!!!");
    }

    private void loadData(String filename) {
        if (filename == null)
            return;

        ArrayList<String>[] temp = new ArrayList[6];
        initDb(temp);
        try {
            in = new BufferedReader(new InputStreamReader(new FileInputStream(filename), StandardCharsets.UTF_8));
            String line;
            while ((line = in.readLine()) != null) {
                for (int i = 0; i < 6; i++) {
                    if (line.startsWith(String.valueOf(i))) {
                        line = line.split("@@@")[1];
                        temp[i].add(line);
                        break;
                    }
                }
            }
        }catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        completeDbData(temp);
    }

    private synchronized ArrayList<String> toSend(String[] types, String regex) {
        lock.lock();
        dataToSend = new ArrayList<String>();
        for (String s : types) {
            int i = Integer.parseInt(s);
            if (!(i >= 0 && i < 6)){
                System.err.println("Wrong data's types format: " + i);
                continue;
            }

            String[] dataPerType = dbData[i];
            Pattern checkRegex = Pattern.compile(regex);
            for (String str : dataPerType) {
                Matcher matcher = checkRegex.matcher(str);
                StringBuilder toSend = new StringBuilder();
                if (matcher.find()) {
                    toSend.append(i).append("@@@").append(str).append("\n");
                }
                String temp = toSend.toString();
                dataToSend.add(temp);
            }
        }
        lock.unlock();
        return dataToSend;
    }

    public ArrayList<String> readIt(String request) {
        if (request.contains("Client"))
            return new ArrayList<String>();
        String[] requestData = request.split(";");
        if (requestData.length != 2) {
            System.err.println("Wrong request format: " + Arrays.toString(requestData));
            return null;
        }

        return toSend(requestData[0].split(","), requestData[1].split("\n")[0]);
    }
}

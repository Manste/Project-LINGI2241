import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.locks.ReentrantLock;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ReadFile {
    private ArrayList<String>[] dbData;
    private StringBuilder dataToSend;
    private final ReentrantLock lock = new ReentrantLock();

    public ReadFile(String filename) {
        dbData = new ArrayList[6];
        loadData(filename);
    }

    private void initDb(ArrayList[] tab) {
        for (int i = 0; i < tab.length; i++) {
            tab[i] = new ArrayList<String>();
        }
    }

    private void loadData(String filename) {
        if (filename == null)
            return;
        initDb(dbData);
        try {
            Files.lines(Paths.get(filename)).forEach(line -> {
                String[] lineArr = line.split("@@@");
                dbData[Integer.parseInt(lineArr[0])].add(lineArr[1]);
            });

        }catch (IOException e) {
            e.printStackTrace();
        } finally {
            System.out.println("Database Loaded!!!");
        }
    }

    private String toSend(String[] types, String regex) {
        lock.lock();
        dataToSend = new StringBuilder();
        if (types[0].equals("")){ //if the request type is empty
            for (int i = 0; i < dbData.length; i++) {
                checkPattern(dataToSend, i, regex);
            }
        }
        else {
            for (String s : types) {
                int i = Integer.parseInt(s);
                if (!(i >= 0 && i < 6)){
                    System.err.println("Wrong data's types format: " + i);
                    continue;
                }
                checkPattern(dataToSend, i, regex);
            }
        }
        lock.unlock();
        return dataToSend.toString();
    }

    private void checkPattern(StringBuilder dataToSend, int dataType, String regex) {
        ArrayList<String> dataPerType = dbData[dataType];
        Pattern checkRegex = Pattern.compile(regex);
        for (String str : dataPerType) {
            Matcher matcher = checkRegex.matcher(str);
            if (matcher.find()) {
                dataToSend.append(dataType).append("@@@").append(str).append("\n");
            }
        }
    }

    public String readIt(String request) {
        if (request.contains("Client"))
            return null;
        String[] requestData = request.split(";");
        if (requestData.length != 2) {
            System.err.println("Wrong request format: " + Arrays.toString(requestData));
            return null;
        }
        if (requestData[0].equals("")) return toSend(new String[]{""}, requestData[1].split("\n")[0]);
        return toSend(requestData[0].split(","), requestData[1].split("\n")[0]);
    }


}

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ReadFile {
    private HashMap<Integer, ArrayList<String>> dbData;

    public ReadFile(String filename) {
        this.dbData = new HashMap<Integer, ArrayList<String>>();
        initDb();
        loadData(filename);
    }

    private void initDb() {
        for (int i = 0; i < 6; i++) {
            dbData.put(i, new ArrayList<String>());
        }
    }

    private void loadData(String filename) {
        if (filename == null)
            return;

        FileInputStream inputStream = null;
        Scanner sc = null;
        try {
            inputStream = new FileInputStream(filename);
            sc = new Scanner(inputStream, "UTF-8");
            while (sc.hasNextLine()) {
                String str = sc.nextLine();
                for (int i = 0; i < 6; i++) {
                    if (str.startsWith(String.valueOf(i))) {
                        ArrayList<String> dataToSave = dbData.get(i);
                        str = str.split("@@@")[1];
                        dataToSave.add(str);
                        break;
                    }
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (sc != null)
                sc.close();
        }
    }

    private String toSend(String[] types, String regex) {
        StringBuilder toSend = new StringBuilder();

        for (String s : types) {
            int i = Integer.parseInt(s);
            if (!(i >= 0 && i < 10)){
                System.err.println("Wrong data's types format: " + i);
                continue;
            }

            ArrayList<String> dataForType = dbData.get(i);
            Pattern checkRegex = Pattern.compile(regex);
            for (String str : dataForType) {
                Matcher matcher = checkRegex.matcher(str);
                if (matcher.find()) {
                    toSend.append(i).append("@@@").append(str).append("\n");
                }
            }
        }
        return toSend.toString();
    }

    public String readIt(String request) {
        if (request.equals("close"))
            return "close";
        String[] requestData = request.split(";");
        if (requestData.length != 2) {
            System.err.println("Wrong request format: " + Arrays.toString(requestData));
            return null;
        }

        return toSend(requestData[0].split(","), requestData[1].split("\n")[0]);
    }
}

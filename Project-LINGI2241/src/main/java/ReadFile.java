import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ReadFile {
    private String[][] dbData;
    private ArrayList<String> dataToSend;

    public ReadFile(String filename) {
        loadData(filename);
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
    }

    private void loadData(String filename) {
        if (filename == null)
            return;

        FileInputStream inputStream = null;
        Scanner sc = null;
        ArrayList<String>[] temp = new ArrayList[6];
        initDb(temp);
        try {
            inputStream = new FileInputStream(filename);
            sc = new Scanner(inputStream, "UTF-8");
            while (sc.hasNextLine()) {
                String str = sc.nextLine();
                for (int i = 0; i < 6; i++) {
                    if (str.startsWith(String.valueOf(i))) {
                        str = str.split("@@@")[1];
                        temp[i].add(str);
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
        completeDbData(temp);
    }

    private String[] toSend(String[] types, String regex) {
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
                dataToSend.add(toSend.toString());
            }
        }
        return dataToSend.toArray(new String[0]);
    }

    public String[] readIt(String request) {
        if (request.equals("close"))
            return new String[]{};
        String[] requestData = request.split(";");
        if (requestData.length != 2) {
            System.err.println("Wrong request format: " + Arrays.toString(requestData));
            return null;
        }

        return toSend(requestData[0].split(","), requestData[1].split("\n")[0]);
    }
}

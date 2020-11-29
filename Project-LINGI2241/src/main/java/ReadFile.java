import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ReadFile {
    private HashMap<Integer, ArrayList<String>> dbData;

    public ReadFile(String filename) {
        this.dbData = new HashMap<Integer, ArrayList<String>>();
        initDb();
        try {
            loadData(filename);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initDb() {
        for (int i = 0; i < 10; i++) {
            dbData.put(i, new ArrayList<String>());
        }
    }

    private void loadData(String filename) throws IOException {
        if (filename == null)
            return;

        File file = new File(filename);
        BufferedReader br = new BufferedReader(new FileReader(file));
        String str;
        while ((str = br.readLine()) != null) {
            for (int i = 0; i < 10; i++) {
                if (str.startsWith(String.valueOf(i))) {
                    ArrayList<String> dataToSave = dbData.get(i);
                    str = str.split("@@@")[1];
                    dataToSave.add(str);
                    break;
                }
            }
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
        String[] requestData = request.split(";");
        if (requestData.length != 2) {
            System.err.println("Wrong request format: " + Arrays.toString(requestData));
            return null;
        }

        return toSend(requestData[0].split(","), requestData[1]);
    }
}

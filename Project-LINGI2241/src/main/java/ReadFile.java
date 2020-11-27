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

    public String[] readIt(int[] types, String regex) {
        ArrayList<String> toSend = new ArrayList<String>();

        for (int i : types) {
            if (!(i >= 0 && i < 10)) return null;

            ArrayList<String> dataForType = dbData.get(i);
            Pattern checkRegex = Pattern.compile(regex);
            for (String str : dataForType) {
                Matcher matcher = checkRegex.matcher(str);
                if (matcher.find()) {
                    toSend.add(i + "@@@" + str + "\n");
                }
            }
        }
        return toSend.toArray(new String[toSend.size()]);
    }

    public static void main(String args[]){
        ReadFile reader = new ReadFile("src/main/data/dbdata.txt");
        String[] toSend = reader.readIt(new int[]{3}, "\\*");
        System.out.println(Arrays.toString(toSend));
    }
}

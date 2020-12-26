import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.File;
import java.io.FilenameFilter;

import java.time.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class DataExtractor {

    public File[] getCSVFile(){
        File dir = new File("data");
        FilenameFilter filter = new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.endsWith(".csv");
            }
        };
        return dir.listFiles(filter);
    }

    public void extract(String filepath){
        String pathToData = filepath;
        BufferedReader br;
        String line = "";
        String cvsSplitBy = ",";

        List<List<String>> records = new ArrayList<>();

        Instant first_mesure = null;
        Instant last_mesure = null;

        int shortest_duration = 0;
        int longuest_duration = 0;

        double average_duration = 0;

        double arrival_rate;

        try {
            FileReader fileReader = new FileReader(pathToData);
            br = new BufferedReader(fileReader);
            while ((line = br.readLine()) !=null){
                String[] values = line.split(cvsSplitBy);
                records.add(Arrays.asList(values));
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        for(int i = 1; i<records.size(); i++){
            int current_duration = Integer.parseInt(records.get(i).get(2));
            //on recupere la première valeur d'arrivée
            if(first_mesure == null){
                first_mesure = Instant.parse(records.get(i).get(1));
            }
            //on récupère la plus petite durée du fichier
            if(shortest_duration == 0 || current_duration<shortest_duration){
                shortest_duration = current_duration;
            }
            //on récupère la plus grande durée du fichier
            if(longuest_duration == 0 || current_duration> longuest_duration){
                longuest_duration = current_duration;
            }
            //on ecrase l'ancienne mesure a chaque iteration pour obtenir la dernière valeur d'arrivée
            last_mesure = Instant.parse(records.get(i).get(1));
            //System.out.println(last_mesure);

            average_duration+= current_duration;
        }
        //System.out.println("Total duration : "+average_duration);
        int request_number = records.size()-1;
        double duration = (double) Duration.between(first_mesure, last_mesure).toMillis();
        arrival_rate =  (request_number*1000/duration);
        average_duration = average_duration/request_number;


        System.out.println("File name : "+filepath);
        System.out.println("number of request :"+(records.size()-1));
        System.out.println("duration : "+duration+" millis");
        System.out.println("shortest duration : "+shortest_duration+" millis");
        System.out.println("longuest duration : "+longuest_duration+" millis");
        System.out.println("average duration : "+average_duration+" millis");
        System.out.println("arrival rate : " + arrival_rate+" req/s");
        System.out.println("\n");

        //String data_to_write = request_number+" "+shortest_duration+" "+average_duration+" "+longuest_duration+"\n";
        String data_to_write = arrival_rate+" "+average_duration+" "+shortest_duration+" "+longuest_duration+"\n";
        try {
            FileWriter totxt = new FileWriter("src/main/data/data.txt", true);
            totxt.write(data_to_write);
            totxt.close();
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        DataExtractor dataExtractor = new DataExtractor();
        for(File file : dataExtractor.getCSVFile()){
            dataExtractor.extract(file.toString());
        }
    }
}

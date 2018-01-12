import net.bbridge.core.text.processing.TextProcessor;
import net.bbridge.feature.extractor.text.TextTopicsFeatureExtractor;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class Main {
    public static void main(String[] args) {
        if (args[0].equals("train"))
            train(args);
        else
            extract(args);

    }

    public static void train(String args[]) {

//      args[0] = "/Users/ksburaya/Documents/NUS/lda-librusec/books/";
//        args[1] = "/Users/ksburaya/Documents/NUS/lda-librusec/librusec.lda";
        String booksPath = args[1];
        String modelPath = args[2];
        int documentSize = 500;

        TextTopicsFeatureExtractor extractor = new TextTopicsFeatureExtractor(500, 0.5, 0.1,
                100, 1000,
                modelPath, 20);


        // read each book and generate documents with N symbols size
        List<TextProcessor.Result> trainData = new ArrayList<>();
        File folder = new File(booksPath);
        File[] listOfFiles = folder.listFiles();
        System.out.println(String.format("Number of book i directory is [%d]", listOfFiles.length));


        for (File file : listOfFiles) {
            if (file.isFile() && !file.getName().equals(".DS_Store")) {
                try {
                    String fullTextStr = new String(Files.readAllBytes(Paths.get(file.getPath())));
                    for (String paragraph: fullTextStr.split("\n")) {
                        JSONArray text = new JSONArray(paragraph);
                        List<String> words = new ArrayList<>();
                        text.forEach(word ->  {
                            try {
                                JSONObject wordJson = (JSONObject) word;
                                JSONArray analysisArray = (JSONArray) wordJson.get("analysis");
                                JSONObject analysis = (JSONObject) analysisArray.get(0);
                                String pos = (String)analysis.get("gr");
                                if (pos.contains("S") && !pos.contains("SPRO"))
                                    words.add((String)analysis.get("lex"));
                            } catch (JSONException e) {
                                return;
                            }
                            if (words.size() > documentSize) {
                                TextProcessor.Result result = new TextProcessor.Result(words, new HashMap<>());
                                trainData.add(result);
                                words.clear();
                            }
                        });
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        extractor.train(trainData);
    }

    public static void extract(String[] args) {
        //        List<String> testList = new ArrayList<>();
//        testList.add("привет");
//        testList.add("как");
//        testList.add("дела");
//        TextProcessor.Result test = new TextProcessor.Result(testList, new HashMap<>());
//        HashMap <String, Double> ldaResult = (HashMap<String, Double>) extractor.extractFeatures(test);
//
//        int i = 0;
    }
}


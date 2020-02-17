/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package crcl.langaugeprocessing.tagger;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.Normalizer;
import java.text.Normalizer.Form;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import javax.swing.JOptionPane;

/**
 *
 * @author MS Vaswani
 */
public class DataAccess implements java.io.Serializable{

    public  HashMap<String, Double> emmisionProbMap = new HashMap<>();
    public  HashMap<String, Double> taggedEmissionMap = new HashMap<>();
    public  HashMap<String, Double> unTaggedEmissionMap = new HashMap<>();
    public  HashMap<String, Double> tagCount = new HashMap<>();

    public  HashMap<String, Double> urduWordWeight = new HashMap<>();
    public  HashMap<String, Double> sindhiWordWeight = new HashMap<>();
    public  HashMap<String, Double> punjabiWordWeight = new HashMap<>();
    public  Double minimumWordWeight = 0.000001;
    public  Double maxEmissionProb = 4.0;
    public  HashMap<String, String> sindhiTaggedWords = new HashMap<>();
    public  HashMap<String, String> punjabiTaggedWords = new HashMap<>();
    public  HashMap<String, String> ruleMap = new HashMap<>();
    public  HashMap<String, Double> urduWordCount = new HashMap<>();
    public  HashMap<String, String> sindhiWordCluster = new HashMap<>();
    public  HashMap<String, String> sindhiClusterTags = new HashMap<>();

    public String[] getTagset(String string) throws FileNotFoundException, IOException {
        String str;
        ArrayList<String> temp = new ArrayList<>();
        BufferedReader br = new BufferedReader(new FileReader(new File(string)));

        while ((str = br.readLine()) != null) {
            temp.add(str);

        }
        br.close();
        String[] tags = temp.toArray(new String[temp.size()]);
        return tags;
    }

    double[][] getTransitionProb(int size, String string) throws FileNotFoundException, IOException {
        BufferedReader br = new BufferedReader(new FileReader(new File(string)));

        double[][] transPro = new double[size][size];
        String str;

        for (int i = 0; i < size; i++) {

            for (int j = 0; j < size; j++) {
                str = br.readLine();
                transPro[i][j] = Double.parseDouble(str.split(" ")[2]);
            }
        }

        return transPro;
    }

    HashMap<String, Double> getAllUrduEmissionProb(String string) throws FileNotFoundException, IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(string), "UTF8"));
        String str;
        String[] arr;
        while ((str = br.readLine()) != null) {
            try {
                arr = str.split(" ");
                emmisionProbMap.put(arr[0] + " " + arr[1], Double.parseDouble(arr[2]));
            } catch (Exception e) {
                System.out.println("Exception");
            }
        }
        return emmisionProbMap;
    }

     HashMap<String, String> getTaggedWords(String language, String path) throws FileNotFoundException, IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(path), "UTF8"));
        String str;
        String[] arr;
        switch (language.toLowerCase()) {

            case "sindhi":
                if (sindhiTaggedWords.isEmpty()) {
                    while ((str = br.readLine()) != null) {
                        try {
                            arr = str.split(" ");
                            sindhiTaggedWords.put(arr[0], arr[1]);
                        } catch (Exception e) {
                            System.out.println("Sindhi Data File Exception");
                        }
                    }
                }
                return sindhiTaggedWords;
            case "punjabi":
                if (punjabiTaggedWords.isEmpty()) {
                    while ((str = br.readLine()) != null) {
                        try {
                            arr = str.split(" ");
                            punjabiTaggedWords.put(arr[0], arr[1]);
                        } catch (Exception e) {
                            System.out.println("Sindhi Data File Exception");
                        }
                    }
                    return punjabiTaggedWords;
                }
            default:
                return null;

        }

    }

    HashMap<String, Double> getWordWeight(String language, String string) throws FileNotFoundException, IOException {

        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(string), "UTF8"));
        String str;
        String[] arr;
        switch (language.toLowerCase()) {

            case "urdu":
                while ((str = br.readLine()) != null) {
                    arr = str.split(" ");
                    urduWordWeight.put(arr[0], Double.parseDouble(arr[1]));
                }
                return urduWordWeight;

            case "sindhi":
                while ((str = br.readLine()) != null) {
                    arr = str.split(" ");
                    sindhiWordWeight.put(arr[0], Double.parseDouble(arr[1]));
                }
                return sindhiWordWeight;
            case "punjabi":
                while ((str = br.readLine()) != null) {
                    arr = str.split(" ");
                    punjabiWordWeight.put(arr[0], Double.parseDouble(arr[1]));
                }
                return punjabiWordWeight;
            default:
                return null;

        }
    }

     HashMap<String, Double> getTaggedEmissionProb(String unkownEmmisionFile) throws FileNotFoundException, IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(unkownEmmisionFile), "UTF8"));
        String str;
        String[] arr;
        while ((str = br.readLine()) != null) {
            arr = str.split(" ");
            taggedEmissionMap.put(arr[0], Double.parseDouble(arr[1]));
        }
        return taggedEmissionMap;
    }

    HashMap<String, Double> getUnTaggedEmissionProb(String string) throws FileNotFoundException, IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(string), "UTF8"));

        String str;
        String[] arr;
        while ((str = br.readLine()) != null) {
            arr = str.split(" ");
            unTaggedEmissionMap.put(arr[0], Double.parseDouble(arr[1]));
        }
        return unTaggedEmissionMap;
    }

     double[][] getUrduEmmissionProb(String[] words, String[] tags) {

        double[][] emission_probability = new double[tags.length][words.length];
        for (int i = 0; i < tags.length; i++) {
            for (int j = 0; j < words.length; j++) {
                if (emmisionProbMap.get(tags[i] + " " + words[j]) != null) {
                    try {
                        emission_probability[i][j] = emmisionProbMap.get(tags[i] + " " + words[j]);
                    } catch (Exception e) {
                        System.out.println("Data File Excecption");
                    }
                } else {
                    emission_probability[i][j] = unTaggedEmissionMap.get(tags[i]);

                }

            }
        }

        return emission_probability;
    }

     double[][] getSindhiEmmissionProb(String[] words, String[] tags) {
        int max = 15;
        int s = 0;
        double[][] emission_probability = new double[tags.length][words.length];
        for (int i = 0; i < tags.length; i++) {
            for (int j = 0; j < words.length; j++) {
                if (sindhiTaggedWords.get(words[j]) != null) {
                    if (sindhiWordWeight.get(words[j]) != null) {
                        if (sindhiTaggedWords.get(words[j]).equals(tags[i])) {
                            emission_probability[i][j] = max * sindhiWordWeight.get(words[j]) / tagCount.get(tags[i]);
                            s++;
                        } else {
                            emission_probability[i][j] = unTaggedEmissionMap.get(tags[i]) * sindhiWordWeight.get(words[j]) / tagCount.get(tags[i]);
                        }
                    } else {
                        if (sindhiTaggedWords.get(words[j]).equals(tags[i])) {
                            emission_probability[i][j] = max * minimumWordWeight;
                            s++;
                        } else {
                            emission_probability[i][j] = unTaggedEmissionMap.get(tags[i]) * minimumWordWeight;
                        }
                    }
                } else if (sindhiWordWeight.get(words[j]) != null) {
                    emission_probability[i][j] = unTaggedEmissionMap.get(tags[i]) * sindhiWordWeight.get(words[j]) / tagCount.get(tags[i]);
                } else {
                    try {
                        emission_probability[i][j] = unTaggedEmissionMap.get(tags[i]) * minimumWordWeight;
                    } catch (Exception e) {
                        System.out.println("Data File Exception");
                    }
                }

            }
        }
        System.out.println("Total: " + words.length);
        System.out.println("Found: " + s);
        return emission_probability;
    }

     double[][] getTransliteratedSindhiEmmissionProb(String[] words, String[] tags) {
        int max = 1;

        double[][] emission_probability = new double[tags.length][words.length];
        for (int i = 0; i < tags.length; i++) {
            for (int j = 0; j < words.length; j++) {
                if (sindhiTaggedWords.get(words[j]) != null) {
                    if (sindhiWordWeight.get(words[j]) != null) {
                        if (sindhiTaggedWords.get(words[j]).equals(tags[i])) {
                            emission_probability[i][j] = max * sindhiWordWeight.get(words[j]) / tagCount.get(tags[i]);
                        } else {
                            emission_probability[i][j] = unTaggedEmissionMap.get(tags[i]) * sindhiWordWeight.get(words[j]) / tagCount.get(tags[i]);
                        }
                    } else {
                        if (sindhiTaggedWords.get(words[j]).equals(tags[i])) {
                            emission_probability[i][j] = max * minimumWordWeight;

                        } else {
                            emission_probability[i][j] = unTaggedEmissionMap.get(tags[i]) * minimumWordWeight;
                        }
                    }
                } else if (sindhiWordWeight.get(words[j]) != null) {
                    String str = transliterate(words[j]);
                    if (emmisionProbMap.get(tags[i] + " " + str) != null) {
                        try {
                            emission_probability[i][j] = emmisionProbMap.get(tags[i] + " " + str);
                        } catch (Exception e) {
                            System.out.println("");
                        }
                    } else {
                        emission_probability[i][j] = unTaggedEmissionMap.get(tags[i]) * sindhiWordWeight.get(words[j]) / tagCount.get(tags[i]);

                    }

                } else {
                    try {
                        String str = transliterate(words[j]);
                        if (emmisionProbMap.get(tags[i] + " " + str) != null) {
                            try {
                                emission_probability[i][j] = emmisionProbMap.get(tags[i] + " " + str);
                            } catch (Exception e) {
                                System.out.println("");
                            }
                        } else {
                            emission_probability[i][j] = unTaggedEmissionMap.get(tags[i]) * minimumWordWeight;

                        }
                    } catch (Exception e) {
                        System.out.println("");
                    }
                }

            }
        }
        return emission_probability;
    }

     HashMap<String, String> getTransliterationRules(String path) throws FileNotFoundException, IOException {
        if (ruleMap.size() == 0) {
            BufferedReader rules = new BufferedReader(new FileReader(path));
            ruleMap = new HashMap<>();
            String str;
            String[] arr;
            while ((str = rules.readLine()) != null) {
                try {
                    arr = str.split("،");
                    ruleMap.put(arr[0], arr[1]);
                } catch (Exception e) {
                    System.out.println(str);
                }
            }
            rules.close();
        }
        return ruleMap;
    }

     String transliterate(String word) {
        String out = "";
        ArrayList<String> temp = new ArrayList<>();
        String[] arr;
        temp.add(out);
        int n;
        for (int i = 0; i < word.length(); i++) {
            if (ruleMap.get(word.charAt(i) + "") != null) {
                arr = ruleMap.get(word.charAt(i) + "").split("/");

                if (arr.length > 1) {
                    n = temp.size();
                    for (int j = 0; j < n; j++) {
                        temp.add(temp.get(j));
                    }
                    n = temp.size();
                    for (int j = 0; j < n / 2; j++) {
                        temp.set(j, temp.get(j) + arr[0]);
                    }
                    for (int j = n / 2; j < n; j++) {
                        temp.set(j, temp.get(j) + arr[1]);
                    }
                } else {
                    for (int j = 0; j < temp.size(); j++) {
                        temp.set(j, temp.get(j) + arr[0]);
                    }
                }

            } else {
                for (int j = 0; j < temp.size(); j++) {
                    temp.set(j, temp.get(j) + word.charAt(i));
                }
            }
        }
        String str = temp.get(0);
        if (temp.size() > 1) {
            Double maxCount = -1.0, count;
            for (String s : temp) {
                if (urduWordCount.get(s) != null) {
                    count = urduWordCount.get(s);
                    if (count > maxCount) {
                        maxCount = count;
                        str = s;
                    }
                }
            }
        }
        return str;

    }

     double[][] getPunjabiEmmissionProb(String[] words, String[] tags) {
        int max = 1;

        double[][] emission_probability = new double[tags.length][words.length];
        for (int i = 0; i < tags.length; i++) {
            for (int j = 0; j < words.length; j++) {
                if (punjabiTaggedWords.get(words[j]) != null) {
                    if (punjabiWordWeight.get(words[j]) != null) {
                        if (punjabiTaggedWords.get(words[j]).equals(tags[i])) {
                            emission_probability[i][j] = max * punjabiWordWeight.get(words[j]) / tagCount.get(tags[i]);
                        } else {
                            emission_probability[i][j] = unTaggedEmissionMap.get(tags[i]) * punjabiWordWeight.get(words[j]) / tagCount.get(tags[i]);
                        }
                    } else {
                        if (punjabiTaggedWords.get(words[j]).equals(tags[i])) {
                            emission_probability[i][j] = max * minimumWordWeight;

                        } else {
                            emission_probability[i][j] = unTaggedEmissionMap.get(tags[i]) * minimumWordWeight;
                        }
                    }
                } else if (punjabiWordWeight.get(words[j]) != null) {
                    emission_probability[i][j] = unTaggedEmissionMap.get(tags[i]) * punjabiWordWeight.get(words[j]) / tagCount.get(tags[i]);
                } else {
                    try {
                        emission_probability[i][j] = unTaggedEmissionMap.get(tags[i]) * minimumWordWeight;
                    } catch (Exception e) {
                        System.out.println("");
                    }
                }

            }
        }
        return emission_probability;
    }

    double[] getStartProb(int size, String string) throws FileNotFoundException, IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(string), "UTF8"));

        String str;
        double[] temp = new double[size];
        int i = 0;
        while ((str = br.readLine()) != null) {
            temp[i] = Double.parseDouble(str.split(" ")[1]);
            i++;
        }

        return temp;

    }

     double[][] getClusteredSindhiEmmissionProb(String[] words, String[] tags) {
        int max = 1;

        double[][] emission_probability = new double[tags.length][words.length];
        for (int i = 0; i < tags.length; i++) {
            for (int j = 0; j < words.length; j++) {
                if (sindhiTaggedWords.get(words[j]) != null) {
                    if (sindhiWordWeight.get(words[j]) != null) {
                        if (sindhiTaggedWords.get(words[j]).equals(tags[i])) {
                            emission_probability[i][j] = max * sindhiWordWeight.get(words[j]) / tagCount.get(tags[i]);
                        } else {
                            emission_probability[i][j] = unTaggedEmissionMap.get(tags[i]) * sindhiWordWeight.get(words[j]) / tagCount.get(tags[i]);
                        }
                    } else {
                        if (sindhiTaggedWords.get(words[j]).equals(tags[i])) {
                            emission_probability[i][j] = max * minimumWordWeight;

                        } else {
                            emission_probability[i][j] = unTaggedEmissionMap.get(tags[i]) * minimumWordWeight;
                        }
                    }
                } else if (sindhiWordWeight.get(words[j]) != null) {
                    String cluster;
                    String tag;
                    if ((cluster = sindhiWordCluster.get(words[j])) != null && (tag = sindhiClusterTags.get(cluster)) != null) {
                        if (tag.equals(tags[i])) {
                            emission_probability[i][j] = max * sindhiWordWeight.get(words[j]) / tagCount.get(tags[i]);
                        } else {
                            emission_probability[i][j] = unTaggedEmissionMap.get(tags[i]) * sindhiWordWeight.get(words[j]) / tagCount.get(tags[i]);
                        }

                    } else {
                        emission_probability[i][j] = unTaggedEmissionMap.get(tags[i]) * sindhiWordWeight.get(words[j]) / tagCount.get(tags[i]);

                    }

                } else {
                    String cluster;
                    String tag;
                    if ((cluster = sindhiWordCluster.get(words[j])) != null && (tag = sindhiClusterTags.get(cluster)) != null) {
                        if (tag.equals(tags[i])) {
                            emission_probability[i][j] = max * minimumWordWeight;
                        } else {
                            emission_probability[i][j] = unTaggedEmissionMap.get(tags[i]) * minimumWordWeight;
                        }

                    } else {
                        emission_probability[i][j] = unTaggedEmissionMap.get(tags[i]) * minimumWordWeight;

                    }
                }

            }
        }
        return emission_probability;
    }

}

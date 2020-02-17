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
import java.io.Serializable;

import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

/**
 *
 * @author MS Vaswani
 */
public class POSTagger implements java.io.Serializable {

    public String language;
    private ArrayList<String[]> sentences = new ArrayList<>();
    private ArrayList<String[]> output = new ArrayList<>();
    private String[] tagSet;
    private double[] start_probability;
    private double[][] transition_probability;
    private double[][] emission_probability;
    private DataAccess da;

    public POSTagger(String lang) throws IOException {
        language = lang;
        if (language == null) {
            language = "urdu";
        }
        da = new DataAccess();
        da.getAllUrduEmissionProb("data\\urdu\\emission.dat");
        tagSet = da.getTagset("data\\tagset.dat");
        transition_probability = da.getTransitionProb(tagSet.length, "data\\transition.dat");
        da.getUnTaggedEmissionProb("data\\untaggedEmission.dat");
        start_probability = da.getStartProb(tagSet.length, "data\\start.dat");

        da.getWordWeight("urdu", "data\\" + "sindhi" + "\\weights.dat");
        da.getTaggedWords(language, "data\\" + "sindhi" + "\\taggedwords.dat");
        da.getWordWeight("urdu", "data\\" + "punjabi" + "\\weights.dat");
        da.getTaggedWords(language, "data\\" + "punjabi" + "\\taggedwords.dat");

//        ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(new File("tagger.ser")));
//
//        // do the magic  
//        oos.writeObject(da);
//        // close the writing.
//        oos.close();
//
//        if (!language.equals("urdu")) {
//            da.getWordWeight("urdu", "data\\" + language + "\\weights.dat");
//            da.getTaggedWords(language, "data\\" + language + "\\taggedwords.dat");
//        }
    }

    private static ArrayList<String[]> fetchSentences(String datatxt) throws FileNotFoundException, IOException {
        String str;
        ArrayList<String[]> sentences = new ArrayList<>();
//        BufferedReader br = new BufferedReader(new FileReader(datatxt));
        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(datatxt), "UTF8"));

        while ((str = br.readLine()) != null) {
            if (str.length() > 1) {
                sentences.add(str.split("[؟\\\\.\\|[ \u0640\u06D4\u060C\u2018\u2019\u201D\u0650\"\\(:\\)\\'\\-\\!\\]\\[\\\\]]"));
            }
        }
        br.close();
        return sentences;
    }

    void close() {
        sentences.clear();
        tagSet = null;
        start_probability = null;
        transition_probability = null;
        emission_probability = null;
        System.gc();
    }

    public void tagFolder(String path, String out, String del) {
        File folder = new File(path);
        String[] temp;
        File[] list = folder.listFiles();
        int[] args;
        if (del == null) {
            del = "/";
        }
        if (out != null) {
            new File("tagged" + path).mkdir();
        } else {
            new File(out).mkdir();
        }
        for (File file : list) {
            try {
                sentences = new ArrayList<>();
                sentences = fetchSentences(file.getPath());
//                BufferedWriter wr = new BufferedWriter(new FileWriter("tagged" + path + "\\" + file.getName()));
                BufferedWriter wr = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("tagged" + path + "\\" + file.getName()), "UTF-8"));
                switch (language) {
                    case "urdu":
                        for (String[] arr : sentences) {
                            emission_probability = da.getUrduEmmissionProb(arr, tagSet);
                            Viterbi v = new Viterbi(arr, tagSet, start_probability, transition_probability, emission_probability);
                            args = Viterbi.start();
                            temp = new String[args.length];
                            for (int i = 0; i < args.length - 1; i++) {
                                temp[i] = arr[i] + del + tagSet[args[i]] + " ";
                                wr.write(temp[i]);
                            }
                            wr.newLine();

                        }
                        break;
                    case "sindhi":
                        for (String[] arr : sentences) {
                            emission_probability = da.getClusteredSindhiEmmissionProb(arr, tagSet);
                            Viterbi v = new Viterbi(arr, tagSet, start_probability, transition_probability, emission_probability);
                            args = Viterbi.start();
                            temp = new String[args.length];
                            for (int i = 0; i < args.length - 1; i++) {
                                temp[i] = arr[i] + del + tagSet[args[i]] + " ";
                                wr.write(temp[i]);
                            }
                            wr.newLine();
                        }
                        break;
                    case "punjabi":
                        for (String[] arr : sentences) {

                            emission_probability = da.getPunjabiEmmissionProb(arr, tagSet);
                            Viterbi v = new Viterbi(arr, tagSet, start_probability, transition_probability, emission_probability);
                            args = Viterbi.start();
                            temp = new String[args.length];
                            for (int i = 0; i < args.length - 1; i++) {
                                temp[i] = arr[i] + del + tagSet[args[i]] + " ";
                                wr.write(temp[i]);
                            }
                            wr.newLine();

                        }
                        break;
                }

                wr.close();
            } catch (IOException ex) {
            }
        }

    }

    public void tagFile(String path, String out, String del) {
        File file = new File(path);
        String[] temp;

        int[] args;

        try {
            sentences = new ArrayList<>();
            sentences = fetchSentences(file.getPath());
            BufferedWriter wr;
            if (del == null) {
                del = "/";
            }
            if (out == null) {
                wr = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("tagged" + file.getName()), "UTF-8"));

            } else {
                wr = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(out), "UTF-8"));
            }
            switch (language) {
                case "urdu":
                    System.out.println("sentences " + sentences.size());
                    for (String[] arr : sentences) {
                        emission_probability = da.getUrduEmmissionProb(arr, tagSet);
                        System.out.println("emission " + emission_probability.length + " " + emission_probability[0].length);
                        System.out.println("arr " + arr.length);

//                        for(String s: arr){
//                            System.out.println(s);
//                        }
//                       for(String s: tagSet){
//                            System.out.println(s);
//                        }
                        Viterbi v = new Viterbi(arr, tagSet, start_probability, transition_probability, emission_probability);
                        args = Viterbi.start();
                        System.out.println("args " + args.length);
                        for (int s : args) {
                            System.out.println(s);
                        }
                        temp = new String[args.length];
                        for (int i = 0; i < args.length - 1; i++) {
                            temp[i] = arr[i] + del + tagSet[args[i]] + " ";
                            wr.write(temp[i]);
                        }
                        wr.newLine();

                    }
                    break;
                case "sindhi":
                    for (String[] arr : sentences) {
                        emission_probability = da.getClusteredSindhiEmmissionProb(arr, tagSet);
                        Viterbi v = new Viterbi(arr, tagSet, start_probability, transition_probability, emission_probability);
                        args = Viterbi.start();
                        temp = new String[args.length];
                        for (int i = 0; i < args.length - 1; i++) {
                            temp[i] = arr[i] + del + tagSet[args[i]] + " ";
                            wr.write(temp[i]);
                        }
                        wr.newLine();
                    }
                    break;
                case "punjabi":
                    for (String[] arr : sentences) {

                        emission_probability = da.getPunjabiEmmissionProb(arr, tagSet);
                        Viterbi v = new Viterbi(arr, tagSet, start_probability, transition_probability, emission_probability);
                        args = Viterbi.start();
                        temp = new String[args.length];
                        for (int i = 0; i < args.length - 1; i++) {
                            temp[i] = arr[i] + del + tagSet[args[i]] + " ";
                            wr.write(temp[i]);
                        }
                        wr.newLine();

                    }
                    break;
            }

            wr.close();
        } catch (IOException ex) {
        }

    }
}

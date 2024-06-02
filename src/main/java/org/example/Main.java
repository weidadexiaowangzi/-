package org.example;

import javax.swing.*;
import java.io.*;


public class Main {
    public static void main(String[] args) {
        String filePath = "E:\\study\\sofr-project\\lab1\\input.txt"; // 替换为你的文件路径
        GraphV graphV = new GraphV();
        try {
            BufferedReader br = new BufferedReader(new FileReader(filePath));
            String line;
            String previousWord = null;

            while ((line = br.readLine()) != null) {
                String[] words = line.toLowerCase().replaceAll("[^a-z\\s]", "").split("\\s+");
                for (String word : words) {
                    if (!word.isEmpty()) {
                        if (previousWord != null) {
                            graphV.addEdge(previousWord, word);
                        }
                        previousWord = word;
                    }
                }
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }


        graphV.showDirectedGraph();

        System.out.println(graphV.queryBridgeWords("explore", "new"));
//        System.out.println(graphV.queryBridgeWords("to", "strange"));
//        System.out.println(graphV.queryBridgeWords("new", "and"));

        System.out.println(graphV.generateNewText("seek to explore new and exciting synergies"));
        System.out.println(graphV.generateNewText("to explore new worlds"));
        System.out.println(graphV.generateNewText("new and civilizations"	));

        System.out.println(graphV.calcShortestPath("to", "and"));
        System.out.println(graphV.calcShortestPath("seek", "new"));
        System.out.println(graphV.calcShortestPath(	"explore", "civilizations"));

        System.out.println(graphV.randomWalk());
        System.out.println(graphV.randomWalk());
        System.out.println(graphV.randomWalk());
    }


}

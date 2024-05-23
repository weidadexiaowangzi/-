package org.example;

import java.io.*;
import java.util.*;


public class Main {
    public static void main(String[] args) {
        String filePath = "E:\\study\\sofr-project\\lab1\\input.txt"; // 替换为你的文件路径
        Graph graph = new Graph();
        try {
            BufferedReader br = new BufferedReader(new FileReader(filePath));
            String line;
            String previousWord = null;

            while ((line = br.readLine()) != null) {
                String[] words = line.toLowerCase().replaceAll("[^a-z\\s]", "").split("\\s+");
                for (String word : words) {
                    if (!word.isEmpty()) {
                        if (previousWord != null) {
                            graph.addEdge(previousWord, word);
                        }
                        previousWord = word;
                    }
                }
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 调用各个功能进行测试
        graph.showDirectedGraph();
        System.out.println(graph.queryBridgeWords("explore", "new"));
        System.out.println(graph.generateNewText("seek to explore new and exciting synergies"));
        System.out.println(graph.calcShortestPath("to", "and"));
        System.out.println(graph.randomWalk());
    }

}

package org.example;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;

import org.jgrapht.nio.Attribute;
import org.jgrapht.nio.DefaultAttribute;
import org.jgrapht.nio.dot.DOTExporter;

import javax.imageio.ImageIO;
import javax.swing.*;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.List;



class GraphV {
    private Map<String, Map<String, Integer>> adjList = new HashMap<>();
    private Graph<String, DefaultEdge> graphV = new DefaultDirectedGraph<>(DefaultEdge.class);

    public void addEdge(String word1, String word2) {
        adjList.putIfAbsent(word1, new HashMap<>());
        adjList.get(word1).put(word2, adjList.get(word1).getOrDefault(word2, 0) + 1);
        graphV.addVertex(word1);
        graphV.addVertex(word2);
        graphV.addEdge(word1, word2);
    }

    public Map<String, Map<String, Integer>> getAdjList() {
        return adjList;
    }

    public void saveGraphAsDOT(String filePath) {
        DOTExporter<String, DefaultEdge> exporter = new DOTExporter<>(v -> v);
        exporter.setVertexAttributeProvider(v -> {
            Map<String, Attribute> map = new LinkedHashMap<>();
            map.put("label", DefaultAttribute.createAttribute(v));
            return map;
        });
        exporter.setEdgeAttributeProvider(e -> {
            Map<String, Attribute> map = new LinkedHashMap<>();
            map.put("label", DefaultAttribute.createAttribute(graphV.getEdgeWeight(e)));
            return map;
        });

        try (FileWriter writer = new FileWriter(filePath)) {
            exporter.exportGraph(graphV, writer);
            System.out.println("Graph saved as DOT to " + filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void convertDotToPng(String dotFilePath, String pngFilePath) {
        try {
            ProcessBuilder pb = new ProcessBuilder("C:\\Program Files\\Graphviz\\bin\\dot.exe", "-Tpng", dotFilePath, "-o", pngFilePath);
            pb.inheritIO();
            Process process = pb.start();
            process.waitFor();
            System.out.println("Graph converted to PNG: " + pngFilePath);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void showDirectedGraph() {
        String dotFilePath = "graph.dot";
        String pngFilePath = "graph.png";

        saveGraphAsDOT(dotFilePath);
        convertDotToPng(dotFilePath, pngFilePath);

        // 显示PNG图片
        JFrame frame = new JFrame("Directed Graph Visualization");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);
        frame.setLocationRelativeTo(null);

        try {
            BufferedImage img = ImageIO.read(new File(pngFilePath));
            ImageIcon icon = new ImageIcon(img);
            JLabel label = new JLabel(icon);
            frame.getContentPane().add(label);
        } catch (IOException e) {
            e.printStackTrace();
        }

        frame.setVisible(true);
    }

    public String queryBridgeWords(String word1, String word2) {
        if (!adjList.containsKey(word1) || !adjList.containsKey(word2)) {
            return "No " + word1 + " or " + word2 + " in the graph!";
        }

        Set<String> bridgeWords = new HashSet<>();
        for (String bridge : adjList.get(word1).keySet()) {
            if (adjList.get(bridge).containsKey(word2)) {
                bridgeWords.add(bridge);
            }
        }

        if (bridgeWords.isEmpty()) {
            return "No bridge words from " + word1 + " to " + word2 + "!";
        } else {
            return "The bridge words from " + word1 + " to " + word2 + " are: " + String.join(", ", bridgeWords);
        }
    }
    // 生成新文本
    public String generateNewText(String inputText) {
        String[] words = inputText.toLowerCase().replaceAll("[^a-z\\s]", "").split("\\s+");
        StringBuilder newText = new StringBuilder();
        for (int i = 0; i < words.length - 1; i++) {
            newText.append(words[i]).append(" ");
            String bridgeWords = queryBridgeWords(words[i], words[i + 1]);//查询桥接词
            if (!bridgeWords.contains("No bridge words")&&!bridgeWords.contains(" in the graph!")){
                String[] bridges = bridgeWords.split(": ")[1].split(", ");
                newText.append(bridges[new Random().nextInt(bridges.length)]).append(" ");
            }
        }
        newText.append(words[words.length - 1]);
        return newText.toString();
    }

    public String calcShortestPath(String word1, String word2) {
        if (!adjList.containsKey(word1) || !adjList.containsKey(word2)) {
            return "No " + word1 + " or " + word2 + " in the graph!";
        }

        Map<String, Integer> distances = new HashMap<>();
        Map<String, String> previous = new HashMap<>();
        PriorityQueue<String> pq = new PriorityQueue<>(Comparator.comparingInt(distances::get));

        for (String word : adjList.keySet()) {
            distances.put(word, Integer.MAX_VALUE);
            previous.put(word, null);
        }
        distances.put(word1, 0);
        pq.add(word1);

        while (!pq.isEmpty()) {
            String current = pq.poll();
            if (current.equals(word2)) break;

            for (Map.Entry<String, Integer> neighbor : adjList.get(current).entrySet()) {
                int newDist = distances.get(current) + neighbor.getValue();
                if (newDist < distances.get(neighbor.getKey())) {
                    distances.put(neighbor.getKey(), newDist);
                    previous.put(neighbor.getKey(), current);
                    pq.add(neighbor.getKey());
                }
            }
        }

        if (distances.get(word2) == Integer.MAX_VALUE) {
            return "No path from " + word1 + " to " + word2 + "!";
        }

        List<String> path = new LinkedList<>();
        for (String at = word2; at != null; at = previous.get(at)) {
            path.add(at);
        }
        Collections.reverse(path);

        return "The shortest path from " + word1 + " to " + word2 + " is: " + String.join(" -> ", path);
    }

    public String randomWalk() {
        List<String> nodes = new ArrayList<>(adjList.keySet());
        if (nodes.isEmpty()) return "Graph is empty!";

        StringBuilder walk = new StringBuilder();
        String current = nodes.get(new Random().nextInt(nodes.size()));
        Set<String> visitedEdges = new HashSet<>();

        while (true) {
            walk.append(current).append(" ");
            Map<String, Integer> neighbors = adjList.get(current);
            if (neighbors.isEmpty()) break;

            List<String> possibleNextNodes = new ArrayList<>();
            for (String neighbor : neighbors.keySet()) {
                String edge = current + "->" + neighbor;
                if (!visitedEdges.contains(edge)) {
                    possibleNextNodes.add(neighbor);
                }
            }

            if (possibleNextNodes.isEmpty()) break;
            String next = possibleNextNodes.get(new Random().nextInt(possibleNextNodes.size()));
            visitedEdges.add(current + "->" + next);
            current = next;
        }

        return walk.toString().trim();
    }
}
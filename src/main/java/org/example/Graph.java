package org.example;

import java.util.*;

class Graph {
    private Map<String, Map<String, Integer>> adjList = new HashMap<>();

    public void addEdge(String word1, String word2) {
        adjList.putIfAbsent(word1, new HashMap<>());
        adjList.get(word1).put(word2, adjList.get(word1).getOrDefault(word2, 0) + 1);
    }

    public Map<String, Map<String, Integer>> getAdjList() {
        return adjList;
    }

    public void showDirectedGraph() {
        for (String word : adjList.keySet()) {
            System.out.print(word + " -> ");
            for (String neighbor : adjList.get(word).keySet()) {
                System.out.print(neighbor + "(" + adjList.get(word).get(neighbor) + ") ");
            }
            System.out.println();
        }
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
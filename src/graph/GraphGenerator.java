package graph;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class GraphGenerator {

    public static Graph undirectedConnected(int V, int E, long seed) {
        Random rnd = new Random(seed);
        Graph g = new Graph(V);
        Set<Long> used = new HashSet<>();

        for (int i = 1; i < V; i++) {
            int j = rnd.nextInt(i);
            used.add((long) Math.min(i, j) * V + Math.max(i, j));
            g.addUndirectedEdge(i, j, rnd.nextInt(100) + 1);
        }

        while (g.edgeCount() < E) {
            int u = rnd.nextInt(V);
            int v = rnd.nextInt(V);
            if (u == v) continue;
            long key = (long) Math.min(u, v) * V + Math.max(u, v);
            if (!used.add(key)) continue;
            g.addUndirectedEdge(u, v, rnd.nextInt(100) + 1);
        }
        return g;
    }

    public static Graph directed(int V, int E, long seed) {
        Random rnd = new Random(seed);
        Graph g = new Graph(V);
        Set<Long> used = new HashSet<>();

        while (g.edgeCount() < E) {
            int u = rnd.nextInt(V);
            int v = rnd.nextInt(V);
            if (u == v) continue;
            if (!used.add((long) u * V + v)) continue;
            g.addDirectedEdge(u, v, rnd.nextInt(100) + 1);
        }
        return g;
    }

    public static Graph dag(int V, int E, long seed) {
        Random rnd = new Random(seed);
        Graph g = new Graph(V);
        Set<Long> used = new HashSet<>();

        while (g.edgeCount() < E) {
            int a = rnd.nextInt(V);
            int b = rnd.nextInt(V);
            if (a == b) continue;
            int u = Math.min(a, b);
            int v = Math.max(a, b);
            if (!used.add((long) u * V + v)) continue;
            g.addDirectedEdge(u, v, rnd.nextInt(100) + 1);
        }
        return g;
    }
}
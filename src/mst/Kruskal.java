package mst;

import dsu.DSU;
import graph.Edge;
import graph.Graph;

import java.util.ArrayList;
import java.util.List;

public class Kruskal {
    public long totalWeight;
    public int edgesUsed;
    public boolean connected;

    public long comparisons;
    public long edgeExaminations;
    public DSU dsu;

    public void run(Graph g) {
        totalWeight = 0;
        edgesUsed = 0;
        comparisons = 0;
        edgeExaminations = 0;

        List<Edge> sorted = new ArrayList<>(g.edges);
        sorted.sort((a, b) -> {
            comparisons++;
            return Integer.compare(a.weight, b.weight);
        });

        dsu = new DSU(g.V);
        for (Edge e : sorted) {
            edgeExaminations++;
            if (dsu.union(e.from, e.to)) {
                totalWeight += e.weight;
                edgesUsed++;
                if (edgesUsed == g.V - 1) break;
            }
        }

        connected = (edgesUsed == g.V - 1);
    }
}

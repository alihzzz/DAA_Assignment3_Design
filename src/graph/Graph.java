package graph;

import java.util.ArrayList;
import java.util.List;

public class Graph {
    public int V;
    public List<List<Edge>> adj = new ArrayList<>();
    public List<Edge> edges = new ArrayList<>();

    public Graph(int V) {
        this.V = V;
        for (int i = 0; i < V; i++) {
            adj.add(new ArrayList<>());
        }
    }

    public void addDirectedEdge(int u, int v, int w) {
        Edge e = new Edge(u, v, w);
        adj.get(u).add(e);
        edges.add(e);
    }

    public void addUndirectedEdge(int u, int v, int w) {
        adj.get(u).add(new Edge(u, v, w));
        adj.get(v).add(new Edge(v, u, w));
        edges.add(new Edge(u, v, w));
    }

    public int edgeCount() {
        return edges.size();
    }
}
package connectivity;

import graph.Edge;
import graph.Graph;

import java.util.ArrayList;
import java.util.List;

public class SCC {
    public List<List<Integer>> components;
    public long edgeExaminations;

    private boolean[] visited;

    public void run(Graph g) {
        edgeExaminations = 0;

        visited = new boolean[g.V];
        List<Integer> finishOrder = new ArrayList<>();
        for (int v = 0; v < g.V; v++) {
            if (!visited[v]) dfs1(g, v, finishOrder);
        }

        List<List<Integer>> reversed = new ArrayList<>();
        for (int i = 0; i < g.V; i++) reversed.add(new ArrayList<>());
        for (Edge e : g.edges) {
            reversed.get(e.to).add(e.from);
        }

        visited = new boolean[g.V];
        components = new ArrayList<>();
        for (int i = g.V - 1; i >= 0; i--) {
            int v = finishOrder.get(i);
            if (!visited[v]) {
                List<Integer> component = new ArrayList<>();
                dfs2(reversed, v, component);
                components.add(component);
            }
        }
    }

    private void dfs1(Graph g, int u, List<Integer> finishOrder) {
        visited[u] = true;
        for (Edge e : g.adj.get(u)) {
            edgeExaminations++;
            if (!visited[e.to]) dfs1(g, e.to, finishOrder);
        }
        finishOrder.add(u);
    }

    private void dfs2(List<List<Integer>> reversed, int u, List<Integer> component) {
        visited[u] = true;
        component.add(u);
        for (int next : reversed.get(u)) {
            edgeExaminations++;
            if (!visited[next]) dfs2(reversed, next, component);
        }
    }
}

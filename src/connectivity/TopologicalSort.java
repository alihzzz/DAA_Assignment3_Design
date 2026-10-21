package connectivity;

import graph.Edge;
import graph.Graph;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

public class TopologicalSort {
    public List<Integer> order;
    public boolean hasCycle;
    public long edgeExaminations;

    public void run(Graph g) {
        edgeExaminations = 0;

        int[] indegree = new int[g.V];
        for (Edge e : g.edges) {
            indegree[e.to]++;
            edgeExaminations++;
        }

        Queue<Integer> queue = new ArrayDeque<>();
        for (int v = 0; v < g.V; v++) {
            if (indegree[v] == 0) queue.add(v);
        }

        order = new ArrayList<>();
        while (!queue.isEmpty()) {
            int u = queue.poll();
            order.add(u);
            for (Edge e : g.adj.get(u)) {
                edgeExaminations++;
                indegree[e.to]--;
                if (indegree[e.to] == 0) queue.add(e.to);
            }
        }

        hasCycle = (order.size() < g.V);
    }
}

package mst;

import graph.Edge;
import graph.Graph;

import java.util.PriorityQueue;

public class Prim {
    public long totalWeight;
    public int components;
    public boolean connected;

    public long edgeExaminations;
    public long heapPushes;
    public long heapPolls;

    public void run(Graph g) {
        totalWeight = 0;
        components = 0;
        edgeExaminations = 0;
        heapPushes = 0;
        heapPolls = 0;

        boolean[] visited = new boolean[g.V];

        for (int start = 0; start < g.V; start++) {
            if (visited[start]) continue;
            components++;

            PriorityQueue<Edge> pq = new PriorityQueue<>((a, b) -> Integer.compare(a.weight, b.weight));
            visited[start] = true;
            for (Edge e : g.adj.get(start)) {
                edgeExaminations++;
                pq.add(e);
                heapPushes++;
            }

            while (!pq.isEmpty()) {
                Edge e = pq.poll();
                heapPolls++;
                if (visited[e.to]) continue;

                visited[e.to] = true;
                totalWeight += e.weight;

                for (Edge next : g.adj.get(e.to)) {
                    edgeExaminations++;
                    if (!visited[next.to]) {
                        pq.add(next);
                        heapPushes++;
                    }
                }
            }
        }

        connected = (components == 1);
    }
}

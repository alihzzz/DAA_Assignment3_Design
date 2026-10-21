package shortestpath;

import graph.Edge;
import graph.Graph;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.PriorityQueue;

public class Dijkstra {
    public static final int INF = Integer.MAX_VALUE;

    public int[] dist;
    public int[] prev;

    public long edgeExaminations;
    public long heapPushes;
    public long heapPolls;

    public void run(Graph g, int source) {
        edgeExaminations = 0;
        heapPushes = 0;
        heapPolls = 0;

        dist = new int[g.V];
        prev = new int[g.V];
        for (int i = 0; i < g.V; i++) {
            dist[i] = INF;
            prev[i] = -1;
        }
        dist[source] = 0;

        PriorityQueue<int[]> pq = new PriorityQueue<>((a, b) -> Integer.compare(a[1], b[1]));
        pq.add(new int[]{source, 0});
        heapPushes++;

        while (!pq.isEmpty()) {
            int[] cur = pq.poll();
            heapPolls++;
            int u = cur[0];
            if (cur[1] > dist[u]) continue;

            for (Edge e : g.adj.get(u)) {
                edgeExaminations++;
                if (e.weight < 0) {
                    throw new IllegalArgumentException("Dijkstra does not support negative weights");
                }
                int newDist = dist[u] + e.weight;
                if (newDist < dist[e.to]) {
                    dist[e.to] = newDist;
                    prev[e.to] = u;
                    pq.add(new int[]{e.to, newDist});
                    heapPushes++;
                }
            }
        }
    }

    public List<Integer> getPath(int target) {
        List<Integer> path = new ArrayList<>();
        if (dist[target] == INF) return path;
        for (int v = target; v != -1; v = prev[v]) {
            path.add(v);
        }
        Collections.reverse(path);
        return path;
    }
}

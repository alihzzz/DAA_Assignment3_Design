import connectivity.SCC;
import connectivity.TopologicalSort;
import graph.Graph;
import graph.GraphGenerator;
import mst.Kruskal;
import mst.Prim;
import shortestpath.Dijkstra;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Locale;

public class Main {
    static final long SEED = 42;
    static final int REPEATS = 5;

    static final String[] NAMES = {"Small", "Medium", "Large"};
    static final int[] VS = {10, 100, 1000};
    static final int[] ES = {30, 500, 5000};

    static StringBuilder csv = new StringBuilder("algorithm,graph,V,E,avg_time_us,operations\n");

    public static void main(String[] args) throws IOException {
        demo();

        System.out.println();
        System.out.println("| Algorithm | Graph | V | E | Avg time (us) | Operations |");
        System.out.println("|---|---|---|---|---|---|");

        for (int i = 0; i < NAMES.length; i++) {
            String name = NAMES[i];
            int V = VS[i];
            int E = ES[i];

            Graph undirected = GraphGenerator.undirectedConnected(V, E, SEED);
            Graph cyclic = GraphGenerator.directed(V, E, SEED);
            Graph dag = GraphGenerator.dag(V, E, SEED);

            Kruskal kruskal = new Kruskal();
            double t = timeIt(() -> kruskal.run(undirected));
            record("Kruskal", name, V, E, t,
                    "weight=" + kruskal.totalWeight + "; connected=" + kruskal.connected
                            + "; sortComparisons=" + kruskal.comparisons
                            + "; edgeExams=" + kruskal.edgeExaminations
                            + "; dsuFinds=" + kruskal.dsu.findCalls
                            + "; dsuUnions=" + kruskal.dsu.unionCalls
                            + "; dsuParentSteps=" + kruskal.dsu.parentSteps);

            Prim prim = new Prim();
            t = timeIt(() -> prim.run(undirected));
            record("Prim", name, V, E, t,
                    "weight=" + prim.totalWeight + "; connected=" + prim.connected
                            + "; edgeExams=" + prim.edgeExaminations
                            + "; heapPushes=" + prim.heapPushes
                            + "; heapPolls=" + prim.heapPolls);

            TopologicalSort topoDag = new TopologicalSort();
            t = timeIt(() -> topoDag.run(dag));
            record("TopoSort (DAG)", name, V, E, t,
                    "hasCycle=" + topoDag.hasCycle + "; edgeExams=" + topoDag.edgeExaminations);

            TopologicalSort topoCyclic = new TopologicalSort();
            t = timeIt(() -> topoCyclic.run(cyclic));
            record("TopoSort (cyclic)", name, V, E, t,
                    "hasCycle=" + topoCyclic.hasCycle + "; edgeExams=" + topoCyclic.edgeExaminations);

            SCC scc = new SCC();
            t = timeIt(() -> scc.run(cyclic));
            record("SCC (Kosaraju)", name, V, E, t,
                    "components=" + scc.components.size() + "; edgeExams=" + scc.edgeExaminations);

            Dijkstra dijkstra = new Dijkstra();
            t = timeIt(() -> dijkstra.run(cyclic, 0));
            int reachable = 0;
            for (int d : dijkstra.dist) {
                if (d != Dijkstra.INF) reachable++;
            }
            record("Dijkstra", name, V, E, t,
                    "reachable=" + reachable + "; edgeExams=" + dijkstra.edgeExaminations
                            + "; heapPushes=" + dijkstra.heapPushes
                            + "; heapPolls=" + dijkstra.heapPolls);
        }

        new File("results/tables").mkdirs();
        Files.write(Paths.get("results/tables/results.csv"), csv.toString().getBytes());
        System.out.println();
        System.out.println("Saved: results/tables/results.csv");
    }

    static double timeIt(Runnable task) {
        task.run();
        long total = 0;
        for (int i = 0; i < REPEATS; i++) {
            long start = System.nanoTime();
            task.run();
            total += System.nanoTime() - start;
        }
        return total / (double) REPEATS / 1000.0;
    }

    static void record(String algorithm, String graphName, int V, int E, double timeUs, String operations) {
        String time = String.format(Locale.US, "%.1f", timeUs);
        System.out.println("| " + algorithm + " | " + graphName + " | " + V + " | " + E
                + " | " + time + " | " + operations + " |");
        csv.append(algorithm).append(',').append(graphName).append(',').append(V).append(',')
                .append(E).append(',').append(time).append(',').append(operations).append('\n');
    }

    static void demo() {
        Graph cyclic = GraphGenerator.directed(10, 30, SEED);
        Graph dag = GraphGenerator.dag(10, 30, SEED);

        System.out.println("=== Demo: Small graph (V=10, E=30), seed=" + SEED + " ===");

        Dijkstra dijkstra = new Dijkstra();
        dijkstra.run(cyclic, 0);
        System.out.println("Dijkstra from district 0:");
        for (int v = 0; v < cyclic.V; v++) {
            if (dijkstra.dist[v] == Dijkstra.INF) {
                System.out.println("  to " + v + ": unreachable");
            } else {
                System.out.println("  to " + v + ": distance " + dijkstra.dist[v] + ", path " + dijkstra.getPath(v));
            }
        }

        SCC scc = new SCC();
        scc.run(cyclic);
        System.out.println("Strongly connected components: " + scc.components);

        TopologicalSort topo = new TopologicalSort();
        topo.run(dag);
        System.out.println("Topological order of DAG: " + topo.order + " (hasCycle=" + topo.hasCycle + ")");
        topo.run(cyclic);
        System.out.println("Topological sort of cyclic graph: hasCycle=" + topo.hasCycle);
    }
}
import connectivity.SCC;
import connectivity.TopologicalSort;
import graph.Edge;
import graph.Graph;
import graph.GraphGenerator;
import mst.Kruskal;
import mst.Prim;
import shortestpath.Dijkstra;

import java.util.Arrays;
import java.util.List;

public class Tests {
    static int passed = 0;
    static int failed = 0;

    static void check(String name, boolean ok) {
        if (ok) {
            passed++;
            System.out.println("PASS: " + name);
        } else {
            failed++;
            System.out.println("FAIL: " + name);
        }
    }

    public static void main(String[] args) {
        testMstSmall();
        testMstDisconnected();
        testMstDuplicateWeights();
        testSingleVertex();
        testTopologicalSort();
        testScc();
        testDijkstra();
        testRandomGraphs();

        System.out.println();
        System.out.println("Passed: " + passed + ", Failed: " + failed);
    }

    static void testMstSmall() {
        Graph g = new Graph(4);
        g.addUndirectedEdge(0, 1, 1);
        g.addUndirectedEdge(1, 2, 2);
        g.addUndirectedEdge(2, 3, 3);
        g.addUndirectedEdge(0, 3, 10);
        g.addUndirectedEdge(0, 2, 4);

        Kruskal k = new Kruskal();
        k.run(g);
        Prim p = new Prim();
        p.run(g);

        check("MST small: Kruskal weight = 6", k.totalWeight == 6);
        check("MST small: Prim weight = 6", p.totalWeight == 6);
        check("MST small: both connected", k.connected && p.connected);
    }

    static void testMstDisconnected() {
        Graph g = new Graph(5);
        g.addUndirectedEdge(0, 1, 3);
        g.addUndirectedEdge(1, 2, 4);
        g.addUndirectedEdge(0, 2, 1);
        g.addUndirectedEdge(3, 4, 7);

        Kruskal k = new Kruskal();
        k.run(g);
        Prim p = new Prim();
        p.run(g);

        check("MST disconnected: Kruskal says not connected", !k.connected);
        check("MST disconnected: Prim says not connected", !p.connected);
        check("MST disconnected: same forest weight = 11", k.totalWeight == 11 && p.totalWeight == 11);
        check("MST disconnected: Prim found 2 components", p.components == 2);
    }

    static void testMstDuplicateWeights() {
        Graph g = new Graph(4);
        g.addUndirectedEdge(0, 1, 2);
        g.addUndirectedEdge(1, 2, 2);
        g.addUndirectedEdge(2, 3, 2);
        g.addUndirectedEdge(3, 0, 2);
        g.addUndirectedEdge(0, 2, 2);

        Kruskal k = new Kruskal();
        k.run(g);
        Prim p = new Prim();
        p.run(g);

        check("MST duplicate weights: Kruskal = 6", k.totalWeight == 6);
        check("MST duplicate weights: Prim = 6", p.totalWeight == 6);
    }

    static void testSingleVertex() {
        Graph g = new Graph(1);

        Kruskal k = new Kruskal();
        k.run(g);
        Prim p = new Prim();
        p.run(g);
        check("Single vertex: MST weight 0 and connected", k.totalWeight == 0 && p.totalWeight == 0 && k.connected && p.connected);

        TopologicalSort t = new TopologicalSort();
        t.run(g);
        check("Single vertex: topological order [0]", !t.hasCycle && t.order.equals(Arrays.asList(0)));

        SCC s = new SCC();
        s.run(g);
        check("Single vertex: one SCC", s.components.size() == 1);

        Dijkstra d = new Dijkstra();
        d.run(g, 0);
        check("Single vertex: Dijkstra distance 0", d.dist[0] == 0 && d.getPath(0).equals(Arrays.asList(0)));
    }

    static boolean isValidOrder(Graph g, List<Integer> order) {
        if (order.size() != g.V) return false;
        int[] position = new int[g.V];
        for (int i = 0; i < order.size(); i++) {
            position[order.get(i)] = i;
        }
        for (Edge e : g.edges) {
            if (position[e.from] >= position[e.to]) return false;
        }
        return true;
    }

    static void testTopologicalSort() {
        Graph dag = new Graph(6);
        dag.addDirectedEdge(5, 2, 1);
        dag.addDirectedEdge(5, 0, 1);
        dag.addDirectedEdge(4, 0, 1);
        dag.addDirectedEdge(4, 1, 1);
        dag.addDirectedEdge(2, 3, 1);
        dag.addDirectedEdge(3, 1, 1);

        TopologicalSort t = new TopologicalSort();
        t.run(dag);
        check("Topological sort: DAG has no cycle", !t.hasCycle);
        check("Topological sort: DAG order is valid", isValidOrder(dag, t.order));

        Graph cyclic = new Graph(3);
        cyclic.addDirectedEdge(0, 1, 1);
        cyclic.addDirectedEdge(1, 2, 1);
        cyclic.addDirectedEdge(2, 0, 1);

        t.run(cyclic);
        check("Topological sort: cycle detected", t.hasCycle);
    }

    static boolean sameComponent(SCC s, int a, int b) {
        for (List<Integer> comp : s.components) {
            if (comp.contains(a)) return comp.contains(b);
        }
        return false;
    }

    static boolean eachVertexOnce(Graph g, SCC s) {
        int[] count = new int[g.V];
        for (List<Integer> comp : s.components) {
            for (int v : comp) count[v]++;
        }
        for (int c : count) {
            if (c != 1) return false;
        }
        return true;
    }

    static void testScc() {
        Graph g = new Graph(5);
        g.addDirectedEdge(0, 1, 1);
        g.addDirectedEdge(1, 2, 1);
        g.addDirectedEdge(2, 0, 1);
        g.addDirectedEdge(2, 3, 1);
        g.addDirectedEdge(3, 4, 1);
        g.addDirectedEdge(4, 3, 1);

        SCC s = new SCC();
        s.run(g);
        check("SCC: 2 components", s.components.size() == 2);
        check("SCC: 0,1,2 together", sameComponent(s, 0, 1) && sameComponent(s, 1, 2));
        check("SCC: 3,4 together", sameComponent(s, 3, 4));
        check("SCC: 0 and 3 separate", !sameComponent(s, 0, 3));
        check("SCC: every vertex in exactly one component", eachVertexOnce(g, s));

        Graph dag = new Graph(3);
        dag.addDirectedEdge(0, 1, 1);
        dag.addDirectedEdge(1, 2, 1);
        s.run(dag);
        check("SCC: DAG has V components", s.components.size() == 3);
    }

    static void testDijkstra() {
        Graph g = new Graph(5);
        g.addDirectedEdge(0, 1, 4);
        g.addDirectedEdge(0, 2, 1);
        g.addDirectedEdge(2, 1, 2);
        g.addDirectedEdge(1, 3, 1);

        Dijkstra d = new Dijkstra();
        d.run(g, 0);
        check("Dijkstra: distances correct", Arrays.equals(d.dist, new int[]{0, 3, 1, 4, Dijkstra.INF}));
        check("Dijkstra: path to 3 is 0,2,1,3", d.getPath(3).equals(Arrays.asList(0, 2, 1, 3)));
        check("Dijkstra: unreachable vertex has empty path", d.getPath(4).isEmpty());

        Graph negative = new Graph(2);
        negative.addDirectedEdge(0, 1, -5);
        boolean thrown = false;
        try {
            d.run(negative, 0);
        } catch (IllegalArgumentException e) {
            thrown = true;
        }
        check("Dijkstra: negative weight is rejected", thrown);
    }

    static void testRandomGraphs() {
        boolean mstOk = true;
        boolean topoOk = true;
        boolean sccOk = true;
        boolean dijkstraOk = true;

        for (long seed = 1; seed <= 20; seed++) {
            Graph u = GraphGenerator.undirectedConnected(50, 200, seed);
            Kruskal k = new Kruskal();
            k.run(u);
            Prim p = new Prim();
            p.run(u);
            if (k.totalWeight != p.totalWeight || !k.connected || !p.connected || u.edgeCount() != 200) mstOk = false;

            Graph dag = GraphGenerator.dag(50, 200, seed);
            TopologicalSort t = new TopologicalSort();
            t.run(dag);
            if (t.hasCycle || !isValidOrder(dag, t.order)) topoOk = false;

            Graph d = GraphGenerator.directed(50, 100, seed);
            SCC s = new SCC();
            s.run(d);
            if (!eachVertexOnce(d, s)) sccOk = false;

            Dijkstra dj = new Dijkstra();
            dj.run(d, 0);
            for (Edge e : d.edges) {
                if (dj.dist[e.from] != Dijkstra.INF && dj.dist[e.to] > dj.dist[e.from] + e.weight) dijkstraOk = false;
            }
        }

        check("Random: Kruskal weight == Prim weight (20 graphs)", mstOk);
        check("Random: topological order valid on DAGs (20 graphs)", topoOk);
        check("Random: every vertex in exactly one SCC (20 graphs)", sccOk);
        check("Random: Dijkstra distances satisfy edge condition (20 graphs)", dijkstraOk);
    }
}
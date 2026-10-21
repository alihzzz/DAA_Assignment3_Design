package dsu;

public class DSU {
    private int[] parent;
    private int[] rank;

    public long findCalls = 0;
    public long unionCalls = 0;
    public long parentSteps = 0;

    public DSU(int n) {
        parent = new int[n];
        rank = new int[n];
        for (int i = 0; i < n; i++) {
            parent[i] = i;
        }
    }

    public int find(int x) {
        findCalls++;
        return findRoot(x);
    }

    private int findRoot(int x) {
        if (parent[x] != x) {
            parentSteps++;
            parent[x] = findRoot(parent[x]);
        }
        return parent[x];
    }

    public boolean union(int a, int b) {
        unionCalls++;
        int ra = find(a);
        int rb = find(b);
        if (ra == rb) return false;

        if (rank[ra] < rank[rb]) {
            parent[ra] = rb;
        } else if (rank[ra] > rank[rb]) {
            parent[rb] = ra;
        } else {
            parent[rb] = ra;
            rank[ra]++;
        }
        return true;
    }
}

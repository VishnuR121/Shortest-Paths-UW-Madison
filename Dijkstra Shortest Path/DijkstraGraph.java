// === CS400 File Header Information ===
// Name: Vishnu Rallapalli
// Email: vrallapalli2@wisc.edu
// Group and Team: P2.3703
// Group TA: <name of your group's ta>
// Lecturer: Florian Heimerl
// Notes to Grader: <optional extra notes>

import java.util.PriorityQueue;
import java.util.List;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

/**
 * This class extends the BaseGraph data structure with additional methods for
 * computing the total cost and list of node data along the shortest path
 * connecting a provided starting to ending nodes. This class makes use of
 * Dijkstra's shortest path algorithm.
 */
public class DijkstraGraph<NodeType, EdgeType extends Number>
        extends BaseGraph<NodeType, EdgeType>
        implements GraphADT<NodeType, EdgeType> {

    /**
     * While searching for the shortest path between two nodes, a SearchNode
     * contains data about one specific path between the start node and another
     * node in the graph. The final node in this path is stored in its node
     * field. The total cost of this path is stored in its cost field. And the
     * predecessor SearchNode within this path is referened by the predecessor
     * field (this field is null within the SearchNode containing the starting
     * node in its node field).
     *
     * SearchNodes are Comparable and are sorted by cost so that the lowest cost
     * SearchNode has the highest priority within a java.util.PriorityQueue.
     */
    protected class SearchNode implements Comparable<SearchNode> {
        public Node node;
        public double cost;
        public SearchNode predecessor;

        public SearchNode(Node node, double cost, SearchNode predecessor) {
            this.node = node;
            this.cost = cost;
            this.predecessor = predecessor;
        }

        public int compareTo(SearchNode other) {
            if (cost > other.cost)
                return +1;
            if (cost < other.cost)
                return -1;
            return 0;
        }
    }

    /**
     * Constructor that sets the map that the graph uses.
     */
    public DijkstraGraph() {
        super(new HashtableMap<>());
    }

    /**
     * This helper method creates a network of SearchNodes while computing the
     * shortest path between the provided start and end locations. The
     * SearchNode that is returned by this method is represents the end of the
     * shortest path that is found: it's cost is the cost of that shortest path,
     * and the nodes linked together through predecessor references represent
     * all of the nodes along that shortest path (ordered from end to start).
     *
     * @param start the data item in the starting node for the path
     * @param end   the data item in the destination node for the path
     * @return SearchNode for the final end node within the shortest path
     * @throws NoSuchElementException when no path from start to end is found
     *                                or when either start or end data do not
     *                                correspond to a graph node
     */
    @SuppressWarnings("unchecked")
    protected SearchNode computeShortestPath(NodeType start, NodeType end) {
        // Check if start and end exist in the graph
        if (!containsNode(start) || !containsNode(end)) {
            throw new NoSuchElementException("Start or end node not found in the graph");
        }

        // Priority queue for Dijkstra's algorithm
        PriorityQueue<SearchNode> pq = new PriorityQueue<>();

        // Map to store the best cost and visited nodes
        HashtableMap<NodeType, SearchNode> visited = new HashtableMap<>();

        Node startNode = nodes.get(start);
        Node endNode = nodes.get(end);

        SearchNode searchStartNode = new SearchNode(startNode, 0.0, null);

        // add instance of SeachNode to the queue
        pq.add(searchStartNode);

        while (!pq.isEmpty()) {
            // Get the node with the lowest cost
            SearchNode currentNode = pq.poll();

            // if the current node is already visted we skip it
            if (visited.containsKey(currentNode.node.data)) {
                continue;
            }

            visited.put(currentNode.node.data, currentNode);

            // If we've reached the end node, return the SearchNode for it
            if (currentNode.node.equals(endNode)) {
                return currentNode;
            }

            // Edge leaving current node
            for (Edge edge : currentNode.node.edgesLeaving) {
                Node succ = edge.successor;

                if (visited.containsKey(succ.data)) {
                    continue;
                }

                // calculate the new cost
                double newCost = currentNode.cost + edge.data.doubleValue();

                SearchNode succNode = new SearchNode(succ, newCost, currentNode);

                pq.add(succNode);
            }
        }

        // If no path exists from start to end, throw an exception
        throw new NoSuchElementException("No path found from start to end.");
    }

    /**
     * Returns the list of data values from nodes along the shortest path
     * from the node with the provided start value through the node with the
     * provided end value. This list of data values starts with the start
     * value, ends with the end value, and contains intermediary values in the
     * order they are encountered while traversing this shorteset path. This
     * method uses Dijkstra's shortest path algorithm to find this solution.
     *
     * @param start the data item in the starting node for the path
     * @param end   the data item in the destination node for the path
     * @return list of data item from node along this shortest path
     */
    public List<NodeType> shortestPathData(NodeType start, NodeType end) {
        LinkedList<NodeType> nodePath = new LinkedList<>();

        SearchNode shortestPathNode = computeShortestPath(start, end);
        SearchNode tempNode = shortestPathNode;

        while (tempNode != null) {
            // adding the data value from nodes in the shortest path to the List
            nodePath.addFirst(tempNode.node.data);
            tempNode = tempNode.predecessor;
        }

        return nodePath;
    }

    /**
     * Returns the cost of the path (sum over edge weights) of the shortest
     * path freom the node containing the start data to the node containing the
     * end data. This method uses Dijkstra's shortest path algorithm to find
     * this solution.
     *
     * @param start the data item in the starting node for the path
     * @param end   the data item in the destination node for the path
     * @return the cost of the shortest path between these nodes
     */
    public double shortestPathCost(NodeType start, NodeType end) {
        // getting the cost of the shortest path from start to end node
        SearchNode shortestPathNode = computeShortestPath(start, end);

        return shortestPathNode.cost;
    }

    // TODO: implement 3+ tests in step 4.1

    /*
     * Helper method to create the graph that will be used in the tester methods.
     */
    private DijkstraGraph<String, Double> createGraph() {
        DijkstraGraph<String, Double> graph = new DijkstraGraph<>();

        // add nodes
        graph.insertNode("A");
        graph.insertNode("B");
        graph.insertNode("C");
        graph.insertNode("D");
        graph.insertNode("E");
        graph.insertNode("F");
        graph.insertNode("G");
        graph.insertNode("H");

        // add edges and weights
        graph.insertEdge("A", "B", 4.0);
        graph.insertEdge("A", "C", 2.0);
        graph.insertEdge("A", "E", 15.0);
        graph.insertEdge("B", "E", 10.0);
        graph.insertEdge("B", "D", 1.0);
        graph.insertEdge("C", "D", 5.0);
        graph.insertEdge("D", "E", 3.0);
        graph.insertEdge("D", "F", 0.0);
        graph.insertEdge("F", "D", 2.0);
        graph.insertEdge("F", "H", 4.0);
        graph.insertEdge("G", "H", 4.0);

        return graph;
    }

    /*
     * Test that makes use of an example that we traced through in lecture, and
     * confirm that the results of my implementation match what I previously
     * computed by hand.
     */
    @Test
    public void test1() {
        DijkstraGraph<String, Double> testerGraph = createGraph();

        List<String> path = testerGraph.shortestPathData("A", "D");
        double cost = testerGraph.shortestPathCost("A", "D");

        // check to see if path and cost is correct between the nodes 
        assertEquals(List.of("A", "B", "D"), path);
        assertEquals(5, cost);
    }

    /*
     * Test using the same graph as I did for the test above, but check the cost
     * and sequence of data along the shortest path between a different start and
     * end node.
     */
    @Test
    public void test2() {
        DijkstraGraph<String, Double> testerGraph = createGraph();

        List<String> path = testerGraph.shortestPathData("B", "E");
        double cost = testerGraph.shortestPathCost("B", "E");

        // check to see if path and cost is correct between the nodes 
        assertEquals(List.of("B", "D", "E"), path);
        assertEquals(4, cost);
    }

    /*
     * Test that checks the behavior of my implementation when the nodes that I am
     * searching for a path between existing nodes in the graph, but there is no
     * sequence of directed edges that connects them from the start to the end.
     */
    @Test
    public void test3() {
        DijkstraGraph<String, Double> testerGraph = createGraph();

        // check to see if exception is thrown as intended 
        assertThrows(NoSuchElementException.class, () -> {
            testerGraph.shortestPathData("C", "G");
        }, "Expected NoSuchElementException when no path exists");
    }
}

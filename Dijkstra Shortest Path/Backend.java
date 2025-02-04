import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.PriorityQueue;

/**
 * This is the class where a backend developer will implement the
 * BackendInterface, so that a frontend developer's code can make use of this
 * functionality. It makes use of a GraphADT to perform shortest path
 * computations.
 */
public class Backend implements BackendInterface {
    // This is a private variable graph that helps store the Backend's graph data
    private GraphADT<String, Double> graph;

    /*
     * Implementing classes should support the constructor below.
     * 
     * @param graph object to store the backend's graph data
     */
    public Backend(GraphADT<String, Double> graph) {
        this.graph = graph;
    }

    /**
     * Loads graph data from a dot file. If a graph was previously loaded, this
     * method should first delete the contents (nodes and edges) of the existing
     * graph before loading a new one.
     * 
     * @param filename the path to a dot file to read graph data from
     * @throws IOException if there was any problem reading from this file
     */
    @Override
    public void loadGraphData(String filename) throws IOException {
        // delete contents of previously loaded graph
        for (String node : new ArrayList<>(graph.getAllNodes())) {
            graph.removeNode(node);
        }

        // Load new data from the dot file
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;

            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("//")) {
                    continue; // Skip comments and empty lines
                }

                // Check for valid edge format and extract components
                int arrowIndex = line.indexOf("->");
                int secondsIndex = line.indexOf("[seconds=");
                int closingBracketIndex = line.lastIndexOf("]");

                if (arrowIndex > 0 && secondsIndex > arrowIndex && closingBracketIndex > secondsIndex) {
                    String from = line.substring(0, arrowIndex).trim();
                    String to = line.substring(arrowIndex + 2, secondsIndex).trim();
                    String weightStr = line.substring(secondsIndex + 9, closingBracketIndex).trim();

                    // Remove surrounding quotes, if any
                    from = from.startsWith("\"") && from.endsWith("\"") ? from.substring(1, from.length() - 1) : from;
                    to = to.startsWith("\"") && to.endsWith("\"") ? to.substring(1, to.length() - 1) : to;

                    try {
                        double weight = Double.parseDouble(weightStr);

                        // Insert nodees and edges into the graph
                        graph.insertNode(from);
                        graph.insertNode(to);
                        graph.insertEdge(from, to, weight);
                    } catch (NumberFormatException e) {
                        // Skip lines with invalid weight formats
                    }
                } else {
                    // Skip lines that do not match expected format
                }
            }
        } catch (IOException e) {
            throw new IOException("There is a problem reading from the file: " + filename + "; " + e.getMessage());
        }
    }

    /**
     * Returns a list of all locations (node data) available in the graph.
     * 
     * @return list of all location names
     */
    @Override
    public List<String> getListOfAllLocations() {
        return graph.getAllNodes();
    }

    /**
     * Return the sequence of locations along the shortest path from
     * startLocation to endLocation, or an empty list if no such path exists.
     * 
     * @param startLocation the start location of the path
     * @param endLocation   the end location of the path
     * @return a list with the nodes along the shortest path from startLocation
     *         to endLocation, or an empty list if no such path exists
     */
    @Override
    public List<String> findLocationsOnShortestPath(String startLocation, String endLocation) {
        try {
            return graph.shortestPathData(startLocation, endLocation);
        } catch (NoSuchElementException e) {
            // return empty list if no path exists
            return new ArrayList<>();
        }
    }

    /**
     * Return the walking times in seconds between each two nodes on the
     * shortest path from startLocation to endLocation, or an empty list of no
     * such path exists.
     * 
     * @param startLocation the start location of the path
     * @param endLocation   the end location of the path
     * @return a list with the walking times in seconds between two nodes along
     *         the shortest path from startLocation to endLocation, or an empty
     *         list if no such path exists
     */
    @Override
    public List<Double> findTimesOnShortestPath(String startLocation, String endLocation) {
        try {
            List<String> shortestPath = graph.shortestPathData(startLocation, endLocation);
            List<Double> walkingTime = new ArrayList<>();

            if (shortestPath.size() < 2) {
                return walkingTime;
            }

            for (int i = 0; i < shortestPath.size() - 1; i++) {
                walkingTime.add(graph.getEdge(shortestPath.get(i), shortestPath.get(i + 1)));
            }

            return walkingTime;
        } catch (NoSuchElementException e) {
            // return empty list if no path exists
            return new ArrayList<>();
        }
    }

    /**
     * Returns a list of the ten closest destinations that can be reached most
     * quickly when starting from the specified startLocation.
     * 
     * @param startLocation the location to find the closest destinations from
     * @return the ten closest destinations from the specified startLocation
     * @throws NoSuchElementException if startLocation does not exist, or if
     *                                there are no other locations that can be
     *                                reached from there
     */
    @Override
    public List<String> getTenClosestDestinations(String startLocation) throws NoSuchElementException {
        // If graph does not have the starting location then throw an exception
        if (!graph.containsNode(startLocation)) {
            throw new NoSuchElementException("Starting location, " + startLocation + ", does not exist in the graph.");
        }

        PriorityQueue<String> pq = new PriorityQueue<>((a, b) -> {
            try {
                double costA = graph.shortestPathCost(startLocation, a);
                double costB = graph.shortestPathCost(startLocation, b);
                return Double.compare(costA, costB);
            } catch (NoSuchElementException e) {
                // This should not happen as only valid nodes are added to the queue
                return 0;
            }
        });

        // Traverse all nodes in the graph and check to see if any of them equals the
        // starting location and if they do then add it to the list
        for (String location : graph.getAllNodes()) {
            if (!location.equals(startLocation)) {
                try {
                    graph.shortestPathCost(startLocation, location);
                    pq.offer(location);
                } catch (NoSuchElementException e) {
                    // Skip locations that are not reachable
                }
            }
        }

        // Retrieve up to 10 closest destinations
        List<String> closestDestinations = new ArrayList<>();
        while (!pq.isEmpty() && closestDestinations.size() < 10) {
            closestDestinations.add(pq.poll());
        }

        return closestDestinations;
    }
}
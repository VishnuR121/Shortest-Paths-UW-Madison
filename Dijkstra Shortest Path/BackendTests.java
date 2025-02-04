import static org.junit.jupiter.api.Assertions.*;

import java.beans.Transient;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import org.junit.jupiter.api.Test;

/**
 * This class contains all the JUnit test methods to test the Backend class's
 * five methods.
 */
public class BackendTests {
    /**
     * This test checks if the loadGraphData method can correctly handle both a
     * valid file name being passed and an invalid file name being passed to the
     * parameter.
     * This test also checks the getListOfAllLocations method to ensure it gives a
     * full list of all possible locations that exist in the graph.
     */
    @Test
    public void roleTest1() throws IOException {
        Graph_Placeholder graph = new Graph_Placeholder();
        Backend backend = new Backend(graph);
        List<String> errorMessage = new ArrayList<>();

        backend.loadGraphData("campus.dot"); // works with correct file name

        try {
            backend.loadGraphData("camp.dot"); // testing wrong file name
        } catch (IOException e) {
            errorMessage.add(e.getMessage());
        }

        assertEquals("There is a problem reading from the file: camp.dot; camp.dot (No such file or directory)",
                errorMessage.get(0));

        List<String> allLocations = backend.getListOfAllLocations();

        assertFalse(allLocations.isEmpty(), "Graph should contain nodes after loading data.");

        // Verify a specific node is present
        assertTrue(allLocations.contains("Computer Sciences and Statistics"), "Graph should contain Union South.");
    }

    /**
     * This test checks if the findLocationsOnShortestPath method correctly computes
     * the nodes that exist in the shortest path between two nodes.
     */
    @Test
    public void roleTest2() {
        Graph_Placeholder graph = new Graph_Placeholder();
        Backend backend = new Backend(graph);

        // Find the shortest path from LocationA to LocationB
        List<String> path = backend.findLocationsOnShortestPath("Union South", "Computer Sciences and Statistics");

        assertEquals(2, path.size(), "Path from Union South to Computer Sciences and Statistics should have 2 nodes.");
        assertEquals("Union South", path.get(0), "First node should be Union South.");
        assertEquals("Computer Sciences and Statistics", path.get(1),
                "Second node should be Computer Sciences and Statistics.");
    }

    /**
     * This test checks the findTimesOnShortestPath method by computing the time it
     * takes between two nodes and if it returns the expected time.
     * This test also checks the getTenClosestDestinations method to ensure that it
     * returns at most the top 10 or less locations that are closest from the
     * starting location being passed to the method.
     */
    @Test
    public void roleTest3() {
        Graph_Placeholder graph = new Graph_Placeholder();
        Backend backend = new Backend(graph);

        List<Double> times = backend.findTimesOnShortestPath("Union South", "Computer Sciences and Statistics");

        assertEquals(1, times.size(),
                "Should return one time value for the path from Union South to Computer Sciences and Statistics.");
        assertEquals(1.0, times.get(0), "The time value should be 1.0 seconds.");

        // Get the ten closest destinations from Union South
        List<String> closestDestinations = backend.getTenClosestDestinations("Union South");

        // Check that the returned list is correct
        assertEquals(2, closestDestinations.size(), "Should return 2 closest destinations from Union South.");
        assertTrue(closestDestinations.contains("Computer Sciences and Statistics"),
                "Computer Sciences and Statistics should be in the closest destinations.");
        assertTrue(closestDestinations.contains("Atmospheric, Oceanic and Space Sciences"),
                "Atmospheric, Oceanic and Space Sciences should be in the closest destinations.");
    }

    /**
     * Integration test for `generateShortestPathResponseHTML`.
     * Verifies that the HTML response accurately reflects the shortest path
     * for valid start and end locations, and handles invalid locations.
     */
    @Test
    public void integrationTest1() throws IOException {
        // Replace placeholders with real classes and data
        GraphADT<String, Double> graph = new DijkstraGraph<>();
        BackendInterface backend = new Backend(graph);
        Frontend frontend = new Frontend(backend);
        backend.loadGraphData("campus.dot");

        // Use valid start and end locations and test
        String response = frontend.generateShortestPathResponseHTML("Union South",
                "Memorial Union");

        assertTrue(response.contains("Travel time:"), "HTML should include total travel time.");
        assertTrue(response.contains("<ol>"), "HTML should contain an ordered list for the path.");

        // Use invalid start and end locations and test
        String invalidStartResponse = frontend.generateShortestPathResponseHTML("InvalidStart", "B");
        assertTrue(invalidStartResponse.contains("Error: start and destination not found."),
                "Error message should appear for an invalid start location.");
    }

    /**
     * Integration test for `generateTenClosestDestinationsResponseHTML`.
     * Verifies that the HTML response accurately lists the closest destinations.
     */
    @Test
    public void integrationTest2() throws IOException {
        // Replace placeholders with real classes and data
        GraphADT<String, Double> graph = new DijkstraGraph<>();
        BackendInterface backend = new Backend(graph);
        Frontend frontend = new Frontend(backend);
        backend.loadGraphData("campus.dot");

        // Use valid start location and test closest destinations
        String response = frontend.generateTenClosestDestinationsResponseHTML("Union South");

        // Assertions
        assertTrue(response.contains("<p>Locations near Union South:</p>"),
                "Response should contain the start location.");
        assertTrue(response.contains("<li>Wendt Commons</li>"),
                "Response should contain the closest location.");
        assertTrue(response.contains("<li>Memorial Arch</li>"),
                "Response should contain the closest location.");
        assertTrue(response.contains("<li>Engineering Hall</li>"),
                "Response should contain the closest location.");
        assertFalse(response.contains("<li>Memorial Union</li>"),
                "Response should not contain Memorial Union.");
    }

    /**
     * Integration test for `generateShortestPathPromptHTML`.
     * Verifies that the HTML output includes the expected input fields and button.
     */
    @Test
    public void integrationTest3() {
        // Replace placeholders with real classes
        GraphADT<String, Double> graph = new DijkstraGraph<>();
        BackendInterface backend = new Backend(graph);
        Frontend frontend = new Frontend(backend);

        // Test Frontend prompt methods
        String html = frontend.generateShortestPathPromptHTML();

        // Assertions
        assertTrue(html.contains("id=\"start\""), "HTML should include a 'start' input field.");
        assertTrue(html.contains("id=\"end\""), "HTML should include an 'end' input field.");
        assertTrue(html.contains("Find Shortest Path"), "HTML should include a button for finding the path.");
    }

    /**
     * Integration test for `generateTenClosestDestinationsPromptHTML`.
     * Verifies that the HTML output includes the expected input field and button.
     */
    @Test
    public void integrationTest4() {
        // Replace placeholders with real classes 
        GraphADT<String, Double> graph = new DijkstraGraph<>();
        BackendInterface backend = new Backend(graph);
        Frontend frontend = new Frontend(backend);

        // Test Frontend prompt methods
        String html = frontend.generateTenClosestDestinationsPromptHTML();

        // Assertions
        assertTrue(html.contains("id=\"from\""), "HTML should include a 'from' input field.");
        assertTrue(html.contains("Ten Closest Destinations"), "HTML should include a button for finding destinations.");
    }

    /**
     * Tests the integration of the Frontend with the Backend when the Backend
     * has no data to return for ten closest destinations. Ensures proper error
     * handling and fallback behavior in the HTML response.
     */
    @Test
    public void integrationTest5() {
        // Replace placeholders with real classes 
        GraphADT<String, Double> graph = new DijkstraGraph<>();
        BackendInterface backend = new Backend(graph);
        Frontend frontend = new Frontend(backend);

        // Use invalid start location and test closest destinations
        String response = frontend.generateTenClosestDestinationsResponseHTML("Fake Location");

        // Assertions for expected messages and error handling
        assertTrue(response.contains("Error: location not found"),
                "HTML should include an error message for no available destinations.");
        assertFalse(response.contains("<li>"), "HTML should not contain any list items.");
    }
}

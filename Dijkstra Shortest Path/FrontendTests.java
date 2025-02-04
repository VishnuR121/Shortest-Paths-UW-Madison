/*
Project 2 Frontend Tests
Benjamin Fleckenstein
bfleckenstei@wisc.edu
Notes:
 */
import org.junit.jupiter.api.*;
public class FrontendTests {
    /**
     * Test for generateShortestPathPromptHTML()
     * tests that a string with proper html syntax is generated
     */
    @Test
    public void roleTest1(){
        Frontend fr = new Frontend(new Backend_Placeholder(new Graph_Placeholder()));
        String expected = "<input id=\"start\" type=\"text\" placeholder=\"Enter start location here.\"/>\n" +
                "<input id=\"end\" type=\"text\" placeholder=\"Enter destination location here.\"/>\n" +
                "<input type=\"button\" value=\"Find Shortest Path\"/>";
        String result = fr.generateShortestPathPromptHTML();
        Assertions.assertEquals(expected, result);
    }
    /**
     * Test for generateShortestPathResponseHTML(String start, String end)
     * tests that a string with proper html syntax containing expected information is generated
     */
    @Test
    public void roleTest2(){
        //valid input
        Frontend fr = new Frontend(new Backend_Placeholder(new Graph_Placeholder()));
        String result = fr.generateShortestPathResponseHTML("Union South", "Atmospheric, Oceanic and Space Sciences");
        String expected ="<p>Start: Union South ~ End: Atmospheric, Oceanic and Space Sciences</p>\n" +
                "<ol>\n" +
                "  <li>Union South</li>\n" +
                "  <li>Computer Sciences and Statistics</li>\n" +
                "  <li>Atmospheric, Oceanic and Space Sciences</li>\n" +
                "</ol>\n" +
                "<p>Travel time: 6.0 seconds.</p>";
        Assertions.assertEquals(expected, result);
        //bad start and destination
        result = fr.generateShortestPathResponseHTML("uh", "oh");
        expected = "<p style=\"color: red;\">Error: start and destination not found.</p>";
        Assertions.assertEquals(expected, result);
        //bad destination
        result = fr.generateShortestPathResponseHTML("Union South", "oh");
        expected = "<p style=\"color: red;\">Error: destination not found.</p>";
        Assertions.assertEquals(expected, result);
        //bad start
        result = fr.generateShortestPathResponseHTML("uh", "Atmospheric, Oceanic and Space " +
                "Sciences");
        expected = "<p style=\"color: red;\">Error: start not found.</p>";
        Assertions.assertEquals(expected, result);
    }
    /**
     * Test for generateTenClosestDestinationsPromptHTML()
     * tests that a string with proper html syntax is generated
     */
    @Test
    public void roleTest3(){
        Frontend fr = new Frontend(new Backend_Placeholder(new Graph_Placeholder()));
        String expected = "<input id=\"from\" type=\"text\" placeholder=\"Enter location.\"/>\n" +
                "<input type=\"button\" value=\"Ten Closest Destinations\"/>";
        String result = fr.generateTenClosestDestinationsPromptHTML();
        Assertions.assertEquals(expected, result);
    }
    /**
     * Test for generateTenClosestDestinationsResponseHTML(String start)
     * tests that a string with proper html syntax containing expected information is generated
     */
    @Test
    public void roleTest4(){
        Frontend fr = new Frontend(new Backend_Placeholder(new Graph_Placeholder()));
        String result = fr.generateTenClosestDestinationsResponseHTML("Union South");
        String expected = "<p>Locations near Union South:</p>\n" +
                "<ul>\n" +
                "  <li>Computer Sciences and Statistics</li>\n" +
                "  <li>Atmospheric, Oceanic and Space Sciences</li>\n" +
                "</ul>";
        Assertions.assertEquals(expected, result);
        result = fr.generateTenClosestDestinationsResponseHTML("$#@!");
        expected = "<p style=\"color: red;\">Error: location not found</p>";
        Assertions.assertEquals(expected, result);
    }
}
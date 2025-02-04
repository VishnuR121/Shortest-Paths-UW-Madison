/*
Project 2 Frontend
Benjamin Fleckenstein
bfleckenstei@wisc.edu
Notes:
    UPDATE 12/6/2024 implemented the following logic change:
        - generateShortestPathResponseHTML() should respond with an error message if the shortest
         path list (=> the times list is also empty) is empty (no path exists).
        - generateTenClosestDestinationsResponseHTML() removed redundant logic
        - bug fix in generateTenClosestDestinationsResponseHTML()
 */
import java.util.List;
import java.util.NoSuchElementException;

public class Frontend implements FrontendInterface{
    BackendInterface backend;
    public Frontend(BackendInterface backend) {this.backend = backend;}
    /**
     * Returns an HTML fragment that can be embedded within the body of a
     * larger html page.  This HTML output should include:
     * - a text input field with the id="start", for the start location
     * - a text input field with the id="end", for the destination
     * - a button labelled "Find Shortest Path" to request this computation
     * Ensure that these text fields are clearly labelled, so that the user
     * can understand how to use them.
     * @return an HTML string that contains input controls that the user can
     *         make use of to request a shortest path computation
     */
    @Override
    public String generateShortestPathPromptHTML() {
        return """
                <input id="start" type="text" placeholder="Enter start location here."/>
                <input id="end" type="text" placeholder="Enter destination location here."/>
                <input type="button" value="Find Shortest Path"/>
                """;
    }
    /**
     * Returns an HTML fragment that can be embedded within the body of a
     * larger html page.  This HTML output should include:
     * - a paragraph (p) that describes the path's start and end locations
     * - an ordered list (ol) of locations along that shortest path
     * - a paragraph (p) that includes the total travel time along this path
     * Or if there is no such path, the HTML returned should instead indicate
     * the kind of problem encountered.
     * @param start is the starting location to find a shortest path from
     * @param end is the destination that this shortest path should end at
     * @return an HTML string that describes the shortest path between these
     *         two locations
     */
    @Override
    public String generateShortestPathResponseHTML(String start, String end) {
        //a location does not exist
        List<String> all = backend.getListOfAllLocations();
        if (!all.contains(start) && !all.contains(end)) return "<p style=\"color: red;\">Error: start and destination " +
                "not found.</p>";
        if (!all.contains(start)) return "<p style=\"color: red;\">Error: start not found.</p>";
        if (!all.contains(end)) return "<p style=\"color: red;\">Error: destination not found.</p>";
        //concat list of locations along shortest path
        List<String> loca = backend.findLocationsOnShortestPath(start, end);
        //Returned empty list = no path exists
        if(loca.isEmpty()) {return "<p style=\"color: red;\">Error: no path exists between "+start+
                " and "+end+".</p>";}
        String out = "<p>Start: " + start + " ~ End: " + end + "</p>\n" + "<ol>\n";
        for(String location: loca) {
            out = out.concat("  <li>"+location+"</li>\n");
        }
        out = out.concat("</ol>\n");
        //calculate total travel time in seconds
        Double totalTime=0.0;
        List<Double> times = backend.findTimesOnShortestPath(start, end);
        for (Double time : times) {
            totalTime += time;
        }
        //concat total travel time
        out = out.concat("<p>Travel time: "+totalTime+" seconds.</p>");
        return out;
    }
    /**
     * Returns an HTML fragment that can be embedded within the body of a
     * larger html page.  This HTML output should include:
     * - a text input field with the id="from", for the start location
     * - a button labelled "Ten Closest Destinations" to submit this request
     * Ensure that this text field is clearly labelled, so that the user
     * can understand how to use it.
     * @return an HTML string that contains input controls that the user can
     *         make use of to request a ten closest destinations calculation
     */
    @Override
    public String generateTenClosestDestinationsPromptHTML() {
        return "<input id=\"from\" type=\"text\" placeholder=\"Enter location.\"/>\n" +
                "<input type=\"button\" value=\"Ten Closest Destinations\"/>";
    }
    /**
     * Returns an HTML fragment that can be embedded within the body of a
     * larger html page.  This HTML output should include:
     * - a paragraph (p) that describes the start location that travel time to
     *        the closest destinations are being measured from
     * - an unordered list (ul) of the ten locations that are closest to start
     * Or if no such destinations can be found, the HTML returned should
     * instead indicate the kind of problem encountered.
     * @param start is the starting location to find close destinations from
     * @return an HTML string that describes the closest destinations from the
     *         specified start location.
     */
    @Override
    public String generateTenClosestDestinationsResponseHTML(String start) {
        //get list of nearby locations. Notice that this list is actually limited to
        // destinations.size() rather than 10.
        String out = "<p>Locations near "+start+":</p>\n<ul>\n";
        try {
                List<String> destinations = backend.getTenClosestDestinations(start);
            for (String destination : destinations) {
            out = out.concat("  <li>" + destination + "</li>\n");
            }
            out = out.concat("</ul>");
            return out;
        }
        catch(NoSuchElementException x) {return "<p style=\"color: red;\">Error: location not " + "found</p>";}
    }
}
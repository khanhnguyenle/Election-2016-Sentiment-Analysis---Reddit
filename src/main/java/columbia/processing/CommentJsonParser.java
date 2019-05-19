package columbia.processing;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * A parser for JSON objects that represent reddit comments.
 */
public class CommentJsonParser {

    private JSONObject jo;

    /**
     * String constructor.
     * Performs parsing of the comment object into a JSON structure.
     */
    public CommentJsonParser(String jObject) {
        JSONParser jp = new JSONParser();
        jp = new JSONParser();
        try {
            jo = (JSONObject)jp.parse(jObject);
        }
        catch(ParseException e) {
            System.err.println("Failed to parse the string '" + jObject);
        }
    }

    /**
     * Getter for comment body.
     */
    public String getCommentBody() {
        return jo.get("body").toString();
    }

    /**
     * Getter for comment ID.
     */
    public String getCommentID() {
        return jo.get("id").toString();
    }

    /**
     * Getter for comment timestamp.
     */
    public String getTimestamp() {
        return jo.get("created_utc").toString();
    }
}

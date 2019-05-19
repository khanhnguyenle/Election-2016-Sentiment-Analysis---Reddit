package columbia;

import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import java.util.HashSet;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.IOException;
import columbia.processing.CommentJsonParser;
import java.util.StringTokenizer;

/**
 * RelevantFilterMapper.
 * Parses lines of a JSON file as input, where each line contains a single
 * JSON object representing a Reddit comment. Outputs those comments that
 * contain any word as specified in an input file.
 * Output is the comment ID, and the body of the comment.
 */
public class RelevantFilterMapper extends Mapper<LongWritable, Text, Text, Text> {

    // Relevant timestamp values
    private final long startFrame;
    private final long endFrame;

    // Output Text items
    private Text outID;
    private Text outComment;
    private Text outCandidate; // trump|hillary|both
    private Text outTimestamp;

    /**
     * Set of relevant words to flag for Trump.
     */
    private HashSet<String> trumpWordSet;

    /**
     * Set of relevant words to flag for Clinton.
     */
    private HashSet<String> clintonWordSet;

    /**
     * Constructor.
     * Loads in the list of relevant words to flag from a file.
     */
    public RelevantFilterMapper() {
        super();
        trumpWordSet = new HashSet<String>();
        clintonWordSet = new HashSet<String>();

        // Populate wordSet from a file in src/main/resources
        InputStream trumpFile = RelevantFilterMapper.class.getResourceAsStream("/trump-dictionary.txt");
        InputStream clintonFile = RelevantFilterMapper.class.getResourceAsStream("/hillary-dictionary.txt");

        setupWordSet(trumpFile, trumpWordSet);
        setupWordSet(clintonFile, clintonWordSet);

        try {
            trumpFile.close();
            clintonFile.close();
        }
        catch(IOException e) {
            System.err.println("Unable to close input streams");
        }

        outID = new Text("");
        outComment = new Text("");
        outCandidate = new Text("");
        outTimestamp = new Text("");

        // Set time frame
        // Trump is nominated
        startFrame = 1468972800;    // Jul 20, 2016 00:00:00 GMT
        // Trump is elected
        endFrame = 1478649600;      // Nov 09, 2016 00:00:00 GMT
    }

    /**
     * Map method.
     * Takes a line of text (a JSON object) as input, extracts the id
     * and comment body, and outputs only relevant comments.
     */
    @Override
    public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException  {
        // First, extract the comment and its ID
        CommentJsonParser jp = new CommentJsonParser(value.toString());

        // Verify timestamp first, only continue if comment was created in the timeframe
        if(!checkTimeFrame(jp.getTimestamp())) { return; }

        // Get comment body, remove punctuation, downcase, and create tokenizer from it
        // TODO: Output modified comment (downcase, replace, etc), or original?
        // Note: Also converts any newlines to spaces
        String outputComment = jp.getCommentBody().replaceAll("[\n\r]", " ").toLowerCase();
        // Also strip punctuation, but not from the actual output
        StringTokenizer st = new StringTokenizer(outputComment.replaceAll("[_]|[^\\w\\s]", ""));

        // Process comment body
        while(st.hasMoreTokens()) {
            String word = st.nextToken();

            // Note: if we know we're talking about both candidates, we can stop parsing this comment

            // If Trump is being talked about
            if(trumpWordSet.contains(word)) {
                // Case where we're talking about both candidates now
                if(outCandidate.toString().equals("hillary")) {
                    outCandidate.set("both");
                    break;
                }
                else if(outCandidate.getLength() == 0) {
                    outCandidate.set("trump");
                }
            }
            // If Clinton is being talked about
            if(clintonWordSet.contains(word)) {
                // Case where we're talking about both candidates now
                if(outCandidate.toString().equals("trump")) {
                    outCandidate.set("both");
                    break;
                }
                else if(outCandidate.getLength() == 0) {
                    outCandidate.set("hillary");
                }
            }
        }
        // Only output if the comment is relevant
        if(outCandidate.getLength() != 0) {
            outID.set(jp.getCommentID());
            outTimestamp.set(jp.getTimestamp());
            outComment.set(outCandidate.toString() + "\t" + outTimestamp.toString() + "\t" + outputComment);
            context.write(outID, outComment);
            // Reset outCandidate so we don't get future blanks
            outCandidate.set("");
        }
    }

    /**
     * Loads the words from a file into the passed dictionary.
     * @param is An InputStream for the file.
     * @param wordSet The set to be populated.
     */
    private void setupWordSet(InputStream is, HashSet<String> wordSet) {
        BufferedReader dictReader = new BufferedReader(new InputStreamReader(is));

        // Read dictionary contents into word set
        try {
            String line = dictReader.readLine();
            while(line != null) {
                wordSet.add(line);
                line = dictReader.readLine();
            }
        }
        catch(IOException e) {
            System.err.println("Failed to read in dictionary!");
        }
    }

    /**
     * Checks to see if the passed timestamp is within the
     * appropriate time frame.
     * @param timestamp The timestamp, as a String, in question.
     * @return True if within the frame, false otherwise.
     */
    private boolean checkTimeFrame(String timestamp) {
        long unixTime = 0;
        // First parse the number
        try {
            unixTime = Long.parseLong(timestamp);
        }
        catch(NumberFormatException e) {
            System.err.println("Timestamp '" + timestamp + "' was not a number!");
            return false;
        }
        if(unixTime >= startFrame && unixTime <= endFrame) {
            return true;
        }
        // Implied else
        return false;
    }
}

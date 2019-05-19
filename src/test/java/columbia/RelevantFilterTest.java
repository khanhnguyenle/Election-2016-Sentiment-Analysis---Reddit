package columbia;

import org.junit.Before;
import org.apache.hadoop.mrunit.mapreduce.MapDriver;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.LongWritable;
import org.junit.Test;
import org.junit.Ignore;
import static org.junit.Assert.*;
import java.io.IOException;

/**
 * MRUnit test for RelevantFilterMapper.
 */
public class RelevantFilterTest {

    private MapDriver<LongWritable, Text, Text, Text> mapDriver;
    private RelevantFilterMapper mapper;

    /**
     * Setup method.
     */
    @Before
    public void setUp() {
        mapper = new RelevantFilterMapper();
        mapDriver = MapDriver.newMapDriver(mapper);
    }

    /* Note: The comments in this test file have been altered */

    /**
     * Test RelevantFilterMapper with valid data that should
     * be kept.
     */
    @Test
    public void testMapValidComment() throws IOException {
        String trumpCommentObject = "{\"author\":\"jim25y\",\"author_flair_css_class\":null,\"author_flair_text\":null,\"body\":\"Trump own words on the consequences of pulling ICE out of California:\n\n&gt;You would see crime like nobody has ever seen crime in this country. And yet we get no help from the state of California. \",\"can_gild\":true,\"controversiality\":0,\"created_utc\":1469972800\"distinguished\":null,\"edited\":false,\"gilded\":0,\"id\":\"duw622t\",\"is_submitter\":false,\"link_id\":\"t3_7zt7xi\",\"parent_id\":\"t1_duvztuv\",\"permalink\":\"/r/POTUSWatch/comments/7zt7xi/trump_says_hes_thinking_about_pulling_ice_from/duw622t/\",\"retrieved_on\":1520292904,\"score\":1,\"stickied\":false,\"subreddit\":\"POTUSWatch\",\"subreddit_id\":\"t5_3jgtl\",\"subreddit_type\":\"public\"}";
        String expectedOutput = "trump\t1469972800\ttrump own words on the consequences of pulling ice out of california:  &gt;you would see crime like nobody has ever seen crime in this country. and yet we get no help from the state of california. ";
        mapDriver.withInput(new LongWritable(0), new Text(trumpCommentObject));
        mapDriver.withOutput(new Text("duw622t"), new Text(expectedOutput));
        mapDriver.runTest();
    }

    /**
     * Test RelevantFilterMapper with irrelevant data that should
     * not be kept.
     */
    @Test
    public void testMapInvalidComment() throws IOException {
        String irrelevantObject = "{\"author\":\"RustyBunion\",\"author_flair_css_class\":\"texas-flag\",\"author_flair_text\":\"Texas\",\"body\":\"Please be South Park, please be South Park, please be South Park.\n\nYes.\",\"can_gild\":true,\"controversiality\":0,\"created_utc\":1469972800\"distinguished\":null,\"edited\":false,\"gilded\":0,\"id\":\"duw62ee\",\"is_submitter\":false,\"link_id\":\"t3_80irf2\",\"parent_id\":\"t1_duvx7sm\",\"permalink\":\"/r/politics/comments/80irf2/melania_trump_parts_ways_with_adviser_amid/duw62ee/\",\"retrieved_on\":1520292908,\"score\":2,\"stickied\":false,\"subreddit\":\"politics\",\"subreddit_id\":\"t5_2cneq\",\"subreddit_type\":\"public\"}";
        mapDriver.withInput(new LongWritable(0), new Text(irrelevantObject));
        // Not specifying the withOutput() method means that we expect no output
        mapDriver.runTest();
    }

    /**
     * Test RelevantFilterMapper with a Trump comment.
     */
    @Test
    public void testFilterTrumpComment() throws IOException {
        String trumpComment = "{\"author\":\"name_here\",\"author_flair_css_class\":\"\",\"author_flair_text\":\"\",\"body\":\"I'm talking about Donald Trump.\",\"can_gild\":true,\"controversiality\":0,\"created_utc\":1469972800\"distinguished\":null,\"edited\":false,\"gilded\":0,\"id\":\"uniqueid\",\"is_submitter\":false,\"link_id\":\"t3_80irf2\",\"parent_id\":\"t1_duvx7sm\",\"permalink\":\"\",\"retrieved_on\":1520292908,\"score\":0,\"stickied\":false,\"subreddit\":\"\",\"subreddit_id\":\"\",\"subreddit_type\":\"\"}";
        String expectedOutput = "trump\t1469972800\ti'm talking about donald trump.";
        mapDriver.withInput(new LongWritable(0), new Text(trumpComment));
        mapDriver.withOutput(new Text("uniqueid"), new Text(expectedOutput));
        mapDriver.runTest();
    }

    /**
     * Test RelevantFilterMapper with a Clinton comment.
     */
    @Test
    public void testFilterClintonComment() throws IOException {
        String hillaryComment = "{\"author\":\"hillary_supporter\",\"author_flair_css_class\":\"\",\"author_flair_text\":\"\",\"body\":\"I'm talking about Hillary Clinton.\",\"can_gild\":true,\"controversiality\":0,\"created_utc\":1469972800\"distinguished\":null,\"edited\":false,\"gilded\":0,\"id\":\"otherid\",\"is_submitter\":false,\"link_id\":\"t3_80irf2\",\"parent_id\":\"t1_duvx7sm\",\"permalink\":\"\",\"retrieved_on\":1520292908,\"score\":0,\"stickied\":false,\"subreddit\":\"\",\"subreddit_id\":\"\",\"subreddit_type\":\"\"}";
        String expectedOutput = "hillary\t1469972800\ti'm talking about hillary clinton.";
        mapDriver.withInput(new LongWritable(0), new Text(hillaryComment));
        mapDriver.withOutput(new Text("otherid"), new Text(expectedOutput));
        mapDriver.runTest();
    }

    /**
     * Test RelevantFilterMapper with a comment that talks about
     * both candidates.
     */
    @Test
    public void testFilterBothCandidatesComment() throws IOException {
        String bothComment = "{\"author\":\"mr_middle_road\",\"author_flair_css_class\":\"\",\"author_flair_text\":\"\",\"body\":\"I don't know. The Donald seems bad, but Clinton just seems worse.\",\"can_gild\":true,\"controversiality\":0,\"created_utc\":1469972800\"distinguished\":null,\"edited\":false,\"gilded\":0,\"id\":\"middleman\",\"is_submitter\":false,\"link_id\":\"t3_80irf2\",\"parent_id\":\"t1_duvx7sm\",\"permalink\":\"\",\"retrieved_on\":1520292908,\"score\":0,\"stickied\":false,\"subreddit\":\"\",\"subreddit_id\":\"\",\"subreddit_type\":\"\"}";
        String expectedOutput = "both\t1469972800\ti don't know. the donald seems bad, but clinton just seems worse.";
        mapDriver.withInput(new LongWritable(0), new Text(bothComment));
        mapDriver.withOutput(new Text("middleman"), new Text(expectedOutput));
        mapDriver.runTest();
    }

    /**
     * Test RelevantFilterMapper for keeping comments within time
     * frame.
     */
    @Test
    public void testFilterTimeFrame() throws IOException {
        // A valid timeframe
        String inTime = "{\"author\":\"time_tester\",\"author_flair_css_class\":\"\",\"author_flair_text\":\"\",\"body\":\"I'm talking about Trump to test the timestamp filtering.\",\"can_gild\":true,\"controversiality\":0,\"created_utc\":1469972800\"distinguished\":null,\"edited\":false,\"gilded\":0,\"id\":\"intime\",\"is_submitter\":false,\"link_id\":\"t3_80irf2\",\"parent_id\":\"t1_duvx7sm\",\"permalink\":\"\",\"retrieved_on\":1469000000,\"score\":0,\"stickied\":false,\"subreddit\":\"\",\"subreddit_id\":\"\",\"subreddit_type\":\"\"}";
        String inTimeOutput = "trump\t1469972800\ti'm talking about trump to test the timestamp filtering.";

        // An invalid timeframe (after)
        String afterFrame = "{\"author\":\"time_tester\",\"author_flair_css_class\":\"\",\"author_flair_text\":\"\",\"body\":\"I'm talking about Trump to test the timestamp filtering, but I'm too late!.\",\"can_gild\":true,\"controversiality\":0,\"created_utc\":1524607021,\"distinguished\":null,\"edited\":false,\"gilded\":0,\"id\":\"outtime\",\"is_submitter\":false,\"link_id\":\"t3_80irf2\",\"parent_id\":\"t1_duvx7sm\",\"permalink\":\"\",\"retrieved_on\":1469000000,\"score\":0,\"stickied\":false,\"subreddit\":\"\",\"subreddit_id\":\"\",\"subreddit_type\":\"\"}";
        // An invalid timeframe (before)
        String beforeFrame = "{\"author\":\"time_tester\",\"author_flair_css_class\":\"\",\"author_flair_text\":\"\",\"body\":\"I'm talking about Trump to test the timestamp filtering, but I'm too early!.\",\"can_gild\":true,\"controversiality\":0,\"created_utc\":1467596374,\"distinguished\":null,\"edited\":false,\"gilded\":0,\"id\":\"outtime\",\"is_submitter\":false,\"link_id\":\"t3_80irf2\",\"parent_id\":\"t1_duvx7sm\",\"permalink\":\"\",\"retrieved_on\":1469000000,\"score\":0,\"stickied\":false,\"subreddit\":\"\",\"subreddit_id\":\"\",\"subreddit_type\":\"\"}";

        // All comments as input
        mapDriver.withInput(new LongWritable(0), new Text(inTime));
        mapDriver.withInput(new LongWritable(0), new Text(afterFrame));
        mapDriver.withInput(new LongWritable(0), new Text(beforeFrame));

        // Only expect the first comment in output
        mapDriver.withOutput(new Text("intime"), new Text(inTimeOutput));
        mapDriver.runTest();
    }
}

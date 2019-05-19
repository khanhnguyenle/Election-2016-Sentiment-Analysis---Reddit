package columbia;

import static org.junit.Assert.*;

import org.junit.Before;
import columbia.processing.CommentJsonParser;
import org.junit.Test;
import org.junit.Ignore;

/**
 * Test container for CommentJsonParser.
 */
public class CommentJsonParserTest {

    /**
     * An example Reddit comment.
     */
    private String exampleComment;

    /**
     * An instance of the JSON parser.
     */
    private CommentJsonParser jp;

    /**
     * Set up method.
     */
    @Before
    public void setUp() {
        exampleComment = "{\"author\":\"theovincent1997\",\"author_flair_css_class\":null,\"author_flair_text\":null,\"body\":\"I used maybelline\u2018s fit me concealer here. I wanna use shape tape but I can\u2019t afford it tbh\",\"can_gild\":true,\"controversiality\":0,\"created_utc\":1519709580,\"distinguished\":null,\"edited\":false,\"gilded\":0,\"id\":\"duw61wx\",\"is_submitter\":true,\"link_id\":\"t3_80ig5k\",\"parent_id\":\"t1_duw4p7i\",\"permalink\":\"/r/MakeupAddiction/comments/80ig5k/an_old_look_from_last_october_that_i_never_shared/duw61wx/\",\"retrieved_on\":1520292902,\"score\":1,\"stickied\":false,\"subreddit\":\"MakeupAddiction\",\"subreddit_id\":\"t5_2rww2\",\"subreddit_type\":\"public\"}";

        jp = new CommentJsonParser(exampleComment);
    }

    /**
     * Test extracting the comment.
     */
    @Test
    public void testGetCommentBody() {
        String commentBody = "I used maybelline\u2018s fit me concealer here. I wanna use shape tape but I can\u2019t afford it tbh";
        assertEquals(commentBody, jp.getCommentBody());
    }

    /**
     * Test extracting the comment's ID.
     */
    @Test
    public void testGetCommentID() {
        String commentID = "duw61wx";
        assertEquals(commentID, jp.getCommentID());
    }

    /**
     * Test extracting the comment's timestamp.
     */
    @Test
    public void testGetTimestamp() {
        String timestamp = "1519709580";
        assertEquals(timestamp, jp.getTimestamp());
    }
}

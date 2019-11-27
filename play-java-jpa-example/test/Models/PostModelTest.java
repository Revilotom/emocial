package Models;

import com.vdurmont.emoji.EmojiParser;
import models.Post;
import org.hamcrest.MatcherAssert;
import org.junit.Test;

import static org.hamcrest.Matchers.*;

public class PostModelTest {

    @Test
    public void testValidationOnlyEmojis() {
        Post p = new Post("\uD83D\uDC79\uD83D\uDE24\uD83D\uDE36\uD83D\uDE11\uD83E\uDD14\uD83D" +
                "\uDE01\uD83D\uDE0D\uD83D\uDE17\uD83D\uDE17\uD83D\uDE19");
        MatcherAssert.assertThat(p.validate(), is(nullValue()));
    }

    @Test
    public void testDarkSkin() {
        String content = "\uD83D\uDC69\uD83C\uDFFF\u200D\uD83C\uDF3E";
        System.out.println("[" + EmojiParser.removeAllEmojis(content) + "]");
        Post p = new Post(content);
        MatcherAssert.assertThat(p.validate(), is(nullValue()));


    }

    @Test
    public void testValidationNonEmojis() {
        Post p = new Post("\uD83D\uDC79asd\uD83D\uDE24\uD83D\uDE36\uD83D\uDE11\uD83E\uDD14\uD83D" +
                "\uDE01\uD83D\uDE0D\uD83D\uDE17\uD83D\uDE17\uD83D\uDE19");
        MatcherAssert.assertThat(p.validate(), notNullValue());
    }

    @Test
    public void testValidationNoEmojis() {
        Post p = new Post("hello");
        MatcherAssert.assertThat(p.validate(), notNullValue());
    }
}

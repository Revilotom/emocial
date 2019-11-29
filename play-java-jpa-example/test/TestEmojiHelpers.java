import helpers.EmojiHelper;
import org.hamcrest.MatcherAssert;
import org.junit.Test;

import static org.hamcrest.Matchers.is;

public class TestEmojiHelpers {
    @Test
    public void testInt2EmojiSingle() {
        MatcherAssert.assertThat(EmojiHelper.intToEmojis(4), is("4️⃣"));
    }

    @Test
    public void testInt2EmojiTriple() {
        MatcherAssert.assertThat(EmojiHelper.intToEmojis(101), is("1️⃣0️⃣1️⃣"));
    }
    @Test
    public void testInt2EmojiNegative() {
        MatcherAssert.assertThat(EmojiHelper.intToEmojis(-101), is("➖1️⃣0️⃣1️⃣"));
    }
}

package Models;

import forms.SignUp;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class SignUpModelTest {

    @Test
    public void testCanInstantiateIfPasswordsMatch() {
        SignUp s = new SignUp("tom", "revilotom", "1234", "1234");
        assertEquals(null, s.validate());
    }

    @Test
    public void testCannotInstantiateIfPasswordsDiffer() {
        SignUp s = new SignUp("tom", "revilotom", "hello", "1234");
        assertNotNull(s.validate());
    }
}

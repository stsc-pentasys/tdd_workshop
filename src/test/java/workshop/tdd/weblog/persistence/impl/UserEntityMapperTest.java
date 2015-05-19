package workshop.tdd.weblog.persistence.impl;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

import org.junit.Test;
import workshop.tdd.weblog.core.Author;
import workshop.tdd.weblog.core.AuthorBuilder;
import workshop.tdd.weblog.persistence.UserEntity;
import workshop.tdd.weblog.persistence.UserEntityBuilder;

public class UserEntityMapperTest {

    private UserEntityMapper underTest = new UserEntityMapper();

    private UserEntity original = UserEntityBuilder.defaultUserEntity().build();

    private Author expected = AuthorBuilder.defaultAuthor().build();

    private Author actual = underTest.map(original);

    @Test
    public void nickNameMatches() throws Exception {
        assertThat("Nickname", actual.getNickName(), is(expected.getNickName()));
    }

    @Test
    public void fullNameMatches() throws Exception {
        assertThat("Full name", actual.getFullName(), is(expected.getFullName()));
    }

    @Test
    public void emailAddressMatches() throws Exception {
        assertThat("Email address", actual.getEmailAddress(), is(expected.getEmailAddress()));
    }
}

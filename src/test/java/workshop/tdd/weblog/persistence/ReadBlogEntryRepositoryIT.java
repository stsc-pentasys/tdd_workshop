package workshop.tdd.weblog.persistence;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import workshop.tdd.weblog.IntegrationBase;

public class ReadBlogEntryRepositoryIT extends IntegrationBase {

    @Autowired
    private BlogEntryRepository underTest;

    @Test
    public void getEntryReturnsExisting() throws Exception {
        BlogEntryEntity result = underTest.findByEntryId("is-tdd-dead");
        assertThat("Title", result.getTitle(), is("Is TDD Dead?"));
    }

    @Test
    public void getEntryReturnsNullOnMissingEntry() throws Exception {
        BlogEntryEntity result = underTest.findByEntryId("non-existing-id");
        assertThat("Result", result, nullValue());
    }

}

package workshop.tdd.weblog.persistence.impl;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import workshop.tdd.weblog.core.Author;
import workshop.tdd.weblog.core.AuthorBuilder;
import workshop.tdd.weblog.persistence.UserEntity;
import workshop.tdd.weblog.persistence.UserEntityBuilder;
import workshop.tdd.weblog.persistence.UserEntityRepository;

@RunWith(MockitoJUnitRunner.class)
public class AuthorMapperTest {

    @InjectMocks
    private AuthorMapper underTest = new AuthorMapper();

    @Mock
    private UserEntityRepository userEntityRepositoryMock;

    @Test
    public void mapConvertsAuthorToUserEntity() throws Exception {
        Author author = AuthorBuilder.defaultAuthor().build();
        UserEntity userEntity = UserEntityBuilder.defaultUserEntity().build();
        when(userEntityRepositoryMock.findByUserId(author.getNickName())).thenReturn(userEntity);

        UserEntity result = underTest.map(author);

        assertThat("User entity", result, sameInstance(userEntity));
    }
}
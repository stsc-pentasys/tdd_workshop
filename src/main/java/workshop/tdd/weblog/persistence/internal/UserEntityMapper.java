package workshop.tdd.weblog.persistence.internal;

import org.springframework.stereotype.Component;
import workshop.tdd.weblog.core.Author;
import workshop.tdd.weblog.persistence.Mapper;
import workshop.tdd.weblog.persistence.UserEntity;

/**
 * Transforms UserEntity to Author.
 */
@Component
public class UserEntityMapper implements Mapper<UserEntity, Author> {

    @Override
    public Author map(UserEntity userEntity) {
        Author result = null;
        if (userEntity != null) {
            result = new Author(
                    userEntity.getUserId(),
                    userEntity.getFirstName() + " " + userEntity.getLastName(),
                    userEntity.getEmailAddress()
            );
        }
        return result;
    }

}

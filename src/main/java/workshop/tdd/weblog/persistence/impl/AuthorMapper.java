package workshop.tdd.weblog.persistence.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import workshop.tdd.weblog.core.Author;
import workshop.tdd.weblog.persistence.UserEntityRepository;
import workshop.tdd.weblog.persistence.Mapper;
import workshop.tdd.weblog.persistence.UserEntity;

/**
 * Transforms Author to UserEntity.
 */
@Component
public class AuthorMapper implements Mapper<Author, UserEntity> {

    private UserEntityRepository userEntityRepository;

    @Autowired
    public AuthorMapper(UserEntityRepository userEntityRepository) {
        this.userEntityRepository = userEntityRepository;
    }

    /**
     * Test only.
     */
    AuthorMapper() {
    }

    @Override
    public UserEntity map(Author t) {
        return userEntityRepository.findByUserId(t.getNickName());
    }
}

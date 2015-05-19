package workshop.tdd.weblog.persistence.impl;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import workshop.tdd.weblog.core.Author;
import workshop.tdd.weblog.core.AuthorPersistenceAdapter;
import workshop.tdd.weblog.persistence.UserEntity;
import workshop.tdd.weblog.persistence.UserEntityRepository;

/**
 * Adapter implementation as bridge between technology-independent API and Spring Data JPA specific implementation.
 */
@Component
@Transactional
public class JpaAuthorPersistenceAdapter implements AuthorPersistenceAdapter {

    private UserEntityRepository userEntityRepository;

    private UserEntityMapper userEntityMapper;

    @Autowired
    public JpaAuthorPersistenceAdapter(UserEntityRepository userEntityRepository, UserEntityMapper userEntityMapper) {
        this.userEntityRepository = userEntityRepository;
        this.userEntityMapper = userEntityMapper;
    }

    /**
     * Test only.
     */
    JpaAuthorPersistenceAdapter() {
    }

    @Override
    public Optional<Author> findById(String nickName) {
        UserEntity userEntity = userEntityRepository.findByUserId(nickName);
        return Optional.ofNullable(userEntityMapper.map(userEntity));
    }
}

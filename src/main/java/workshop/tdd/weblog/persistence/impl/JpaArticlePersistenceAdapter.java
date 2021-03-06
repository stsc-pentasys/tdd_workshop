package workshop.tdd.weblog.persistence.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import workshop.tdd.weblog.core.Article;
import workshop.tdd.weblog.core.ArticlePersistenceAdapter;
import workshop.tdd.weblog.persistence.BlogEntryEntity;
import workshop.tdd.weblog.persistence.BlogEntryRepository;

/**
 * Adapter implementation as bridge between technology-independent API and Spring Data JPA specific implementation.
 */
@Component
@Transactional
public class JpaArticlePersistenceAdapter implements ArticlePersistenceAdapter {

    private BlogEntryRepository blogEntryRepository;

    private ArticleMapper articleMapper;

    private BlogEntryEntityMapper blogEntryEntityMapper;

    @Autowired
    public JpaArticlePersistenceAdapter(
            BlogEntryRepository blogEntryRepository,
            ArticleMapper articleMapper,
            BlogEntryEntityMapper blogEntryEntityMapper) {
        this.blogEntryRepository = blogEntryRepository;
        this.articleMapper = articleMapper;
        this.blogEntryEntityMapper =blogEntryEntityMapper;
    }

    /**
     * Test only.
     */
    JpaArticlePersistenceAdapter() {
    }

    @Override
    public List<Article> findAll() {
        List<BlogEntryEntity> entities = blogEntryRepository.findAllByCustomQuery();
        return createBlogEntryList(entities);
    }

    private List<Article> createBlogEntryList(List<BlogEntryEntity> entities) {
        List<Article> result = new ArrayList<>(entities.size());
        result.addAll(entities.stream().map(blogEntryEntityMapper::map).collect(Collectors.toList()));
        return result;
    }

    @Override
    public Optional<Article> findById(String entryId) {
        BlogEntryEntity entity = blogEntryRepository.findByEntryId(entryId);
        return Optional.ofNullable(entity).map(blogEntryEntityMapper::map);
    }

    @Override
    public void save(Article newEntry) {
        BlogEntryEntity entity = articleMapper.map(newEntry);
        blogEntryRepository.save(entity);
    }

    @Override
    public void update(Article updated) {
        BlogEntryEntity entity = blogEntryRepository.findByEntryId(updated.getArticleId());
        entity.setTitle(updated.getTitle());
        entity.setContent(updated.getContent());
    }
}

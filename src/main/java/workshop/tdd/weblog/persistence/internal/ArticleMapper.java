package workshop.tdd.weblog.persistence.internal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import workshop.tdd.weblog.core.Article;
import workshop.tdd.weblog.persistence.BlogEntryEntity;
import workshop.tdd.weblog.persistence.Mapper;

/**
 * Transforms Article to BlogEntryEntity.
 */
@Component
public class ArticleMapper implements Mapper<Article, BlogEntryEntity> {

    private AuthorMapper authorMapper;

    @Autowired
    public ArticleMapper(AuthorMapper authorMapper) {
        this.authorMapper = authorMapper;
    }

    /**
     * Test only.
     */
    ArticleMapper() {
    }

    @Override
    public BlogEntryEntity map(Article t) {
        BlogEntryEntity entity = new BlogEntryEntity();
        entity.setEntryId(t.getArticleId());
        entity.setTitle(t.getTitle());
        entity.setContent(t.getContent());
        entity.setAuthor(authorMapper.map(t.getPublishedBy()));
        entity.setCreated(t.getCreated());
        return entity;
    }
}

package workshop.tdd.weblog.persistence;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA enabled persistence API.
 *
 * No explicit implementation necessary, is implemented auto-magically at runtime by Spring Data JPA.
 */
@Repository
public interface BlogEntryRepository extends CrudRepository<BlogEntryEntity, Long> {

    /**
     * Retrieve a BlogEntry by its unique id.
     *
     * Corresponding query is generated automatically by Spring Data JPA.
     *
     * @param entryId the entry's id
     * @return the full entry or null, if not found
     */
    BlogEntryEntity findByEntryId(String entryId);

}

package hac.repositories;

import hac.model.QuestionsPool;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
@Repository
public interface QuestionsPoolRepository extends JpaRepository<QuestionsPool, Long>  {

    /**
     * Returns the count of QuestionsPool entities.
     * @return The count of QuestionsPool entities.
     */
    @Override
    long count();
}

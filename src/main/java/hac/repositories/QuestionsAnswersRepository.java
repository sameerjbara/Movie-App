package hac.repositories;

import hac.model.QuestionsAnswers;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QuestionsAnswersRepository extends JpaRepository<QuestionsAnswers, Long> {
    /**
     * Retrieves the QuestionsAnswers entity associated with the given user email.
     * @param userEmail The email of the user.
     * @return The QuestionsAnswers entity associated with the user email, or null if not found.
     */
    QuestionsAnswers findByUserEmail(String userEmail);
}

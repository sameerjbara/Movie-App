package hac.Beans;

import hac.model.QuestionsPool;
import hac.model.User;
import hac.repositories.QuestionsPoolRepository;
import hac.repositories.UserRepository;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * This class implements ApplicationListener class to create a listener that initiates required data
 * as soon as the website runs, such as creating an admin user and adding questions to the QuestionPool
 * repository.
 */
@Component
public class ApplicationStartupListener implements ApplicationListener<ContextRefreshedEvent> {
    //------------------QUESTIONS---------------------------------
    private static final String QUESTION_1 = "What your pets name?";
    private static final String QUESTION_2 = "What is you favorite color?";
    private static final String QUESTION_3 = "What's the name of the high school you attended?";
    private static final String QUESTION_4 = "What is your lucky number?";
    private static final String QUESTION_5 = "What's your mother's maiden name?";

    //--------------------ADMIN CREDENTIALS---------------------------
    private static final String ADMIN_EMAIL = "admin@admin.com";
    private static final String ADMIN_USER_NAME = "admin";
    private static final String ADMIN_PASSWORD = "admin123";
    private static final String ADMIN_ROLE = "ADMIN";

    // Inject the required repositories
    private final UserRepository userRepository;
    private final QuestionsPoolRepository questionsPoolRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();


    public ApplicationStartupListener(UserRepository userRepository,
                                      QuestionsPoolRepository questionsPoolRepository) {
        this.userRepository = userRepository;
        this.questionsPoolRepository = questionsPoolRepository;
    }

    /**
     * even listener that is triggered when the website is run, it creates an admin user object and
     * inserts it to the database, as well as, validation questions.
     * @param event
     */
    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        // Add validation questions if none exist
        if (questionsPoolRepository.count() == 0) {
            addNewQuestion(QUESTION_1);
            addNewQuestion(QUESTION_2);
            addNewQuestion(QUESTION_3);
            addNewQuestion(QUESTION_4);
            addNewQuestion(QUESTION_5);
        }

        // Create admin user if it doesn't exist
        User existingAdmin = userRepository.findByUsername(ADMIN_USER_NAME);
        if (existingAdmin == null) {
            String encryptedPassword = passwordEncoder.encode(ADMIN_PASSWORD);
            User adminUser = new User();
            adminUser.setEmail(ADMIN_EMAIL);
            adminUser.setUsername(ADMIN_USER_NAME);
            adminUser.setPassword(encryptedPassword);
            adminUser.setRole(ADMIN_ROLE);

            userRepository.save(adminUser);
        }
    }


    /**
     * this method receives a question and saves it to the database of QuestionsPool repo.
     * @param question question to be added to database. (TYPE: String)
     */
    private void addNewQuestion(String question) {
        QuestionsPool newQuestion = new QuestionsPool();
        newQuestion.setQuestion(question);
        questionsPoolRepository.save(newQuestion);
    }
}


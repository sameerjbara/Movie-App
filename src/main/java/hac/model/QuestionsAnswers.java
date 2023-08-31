package hac.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.io.Serializable;

/**
 * This Entity class defines a QuestionsAnswers model that has 6 members,
 * id - the id of a single row in the database.
 * first question - the first question the user chose.
 * first answer - the first answer of the first question.
 * second question - the second question the user chose.
 * second answer - the second answer of the first question.
 * User - an instance of the user that declares a one-to-one by-directional
 * relationship with the user model.
 * the model uses suitable sql validation annotations before inserting values
 * to the table
 */
@Entity
public class QuestionsAnswers implements Serializable {

    public QuestionsAnswers() {
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotEmpty(message="Choosing the first question is required")
    private String firstQuestion;

    @NotEmpty(message="Choosing the second question is required")
    private String secondQuestion;

    @NotEmpty(message = "Answer to the first question is required")
    private String firstAnswer;

    @NotEmpty(message = "Answer to the second question is required")
    private String secondAnswer;

   @OneToOne(cascade = CascadeType.ALL, mappedBy = "questionsAnswers")
   private User user;

    public String getFirstQuestion(){ return firstQuestion; }
    public String getSecondQuestion(){ return secondQuestion; }

    public void setFirstQuestion(String firstQuestion){
        this.firstQuestion = firstQuestion;
    }
    public void setSecondQuestion(String secondQuestion){
        this.secondQuestion = secondQuestion;
    }

    public String getFirstAnswer(){return firstAnswer;}
    public String getSecondAnswer(){return secondAnswer;}

    public void setFirstAnswer(String firstAnswer)
    {
        this.firstAnswer=firstAnswer.trim();
    }
    public void setSecondAnswer(String secondAnswer)
    {
        this.secondAnswer=secondAnswer.trim();
    }

    public void setId(Long id) {
        this.id = id;
    }
    public Long getId() {
        return id;
    }

    public User getUser()
    {
        return user;
    }
    public void setUser(User user)
    {
        this.user=user;
    }
}

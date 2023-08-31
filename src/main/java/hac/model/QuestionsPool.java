package hac.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;

import java.io.Serializable;

/**
 * this Entity class resembles the Question pool for the validation questions,
 * the model has 2 members:
 * id - unique id of a single row in the database.
 * question - the question to ask the user.
 * the model uses suitable sql validation annotations before inserting values
 * to the table
 */
@Entity
public class QuestionsPool  implements Serializable {
    public QuestionsPool(){
    }
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotEmpty(message="an empty questions could not be added to question pool.")
    private String question;

    public Long getId(){ return id;}
    public String getQuestion(){ return question; }

    public void setId(Long id){
        this.id = id;
    }
    public void setQuestion(String question){
        this.question = question;
    }
}

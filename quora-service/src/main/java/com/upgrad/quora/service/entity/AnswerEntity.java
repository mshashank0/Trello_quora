package com.upgrad.quora.service.entity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.ZonedDateTime;

@Entity
@Table(name = "answer" , schema = "public")
@NamedQueries(
        {
                @NamedQuery(name = "answerFromUuid" , query = "select a from AnswerEntity a where a.uuid = :answerUuid"),
                @NamedQuery(name = "allAnswersToQuestion" , query = "select a from AnswerEntity a where a.question.uuid = :questionUuid"),
        }
)

public class AnswerEntity implements Serializable {

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "UUID")
    @NotNull
    private String uuid;

    @Column(name = "ANS")
    @NotNull
    private String answer;

    @Column(name = "DATE")
    @NotNull
    private ZonedDateTime date;

    @ManyToOne
    @JoinColumn(name = "USER_ID")
    private UserEntity user;

    @ManyToOne
    @JoinColumn(name = "QUESTION_ID")
    private QuestionEntity question;


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public ZonedDateTime getDate() {
        return date;
    }

    public void setDate(ZonedDateTime date) {
        this.date = date;
    }

    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }

    public QuestionEntity getQuestion() {
        return question;
    }

    public void setQuestion(QuestionEntity question) {
        this.question = question;
    }
}

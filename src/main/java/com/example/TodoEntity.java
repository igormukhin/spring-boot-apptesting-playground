package com.example;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.io.Serializable;

@Entity
public class TodoEntity implements Serializable {

    @Id
    @GeneratedValue
    @Column(updatable = false)
    private Integer id;

    @Column(updatable = false, nullable = false)
    private String username;

    @Column(nullable = false)
    private String description;

    public TodoEntity() {
    }

    public TodoEntity(String username, String description) {
        this.username = username;
        this.description = description;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}

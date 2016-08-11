package com.example;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

@Service
public class TodoService {

    private TodoRepository todoRepo;

    public TodoService(TodoRepository todoRepo) {
        this.todoRepo = todoRepo;
    }

    @Transactional
    public int addTodo(String username, String description) {
        Assert.hasText(username);
        Assert.hasText(description);

        TodoEntity todo = new TodoEntity(username, description);
        todo = todoRepo.save(todo);
        return todo.getId();
    }
}

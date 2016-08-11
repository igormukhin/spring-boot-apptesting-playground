package com.example;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = BootTestingPracticeApplication.class)
@Transactional
public class TodoRepositoryIT {

	@Autowired
	private TodoRepository todoRepo;

	@Test
	public void savesOne() {
		TodoEntity todo = new TodoEntity("mjordan", "do 1");
        todo = todoRepo.save(todo);
        assertThat(todo.getId()).isNotNull().isNotZero();

        List<TodoEntity> all = todoRepo.findAll();
        assertThat(all).size().isEqualTo(1);
        assertThat(all.get(0).getUsername()).isEqualTo("mjordan");
        assertThat(all.get(0).getDescription()).isEqualTo("do 1");

        TodoEntity found = todoRepo.findOne(todo.getId());
        assertThat(found.getUsername()).isEqualTo("mjordan");
        assertThat(found.getDescription()).isEqualTo("do 1");
	}

    @Test
    public void idsAreAutoGenerated() {
        TodoEntity todo1 = new TodoEntity("mjordan", "do 1");
        todo1 = todoRepo.save(todo1);
        assertThat(todo1.getId()).isNotNull().isNotZero();

        TodoEntity todo2 = new TodoEntity("mjordan", "do 2");
        todo2 = todoRepo.save(todo2);
        assertThat(todo2.getId()).isNotNull().isNotZero();

        assertThat(todo2.getId()).isNotEqualTo(todo1.getId());
    }

    @Test
    public void startsEmpty() {
        assertThat(todoRepo.findAll()).size().isEqualTo(0);
    }
}

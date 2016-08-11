package com.example;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class TodoServiceTest {

    @Mock
    private TodoRepository todoRepo;

    @Captor
    private ArgumentCaptor<TodoEntity> todoEntityCaptor;

    private TodoService service;

    @Before
    public void before() {
        service = new TodoService(todoRepo);
    }

    @Test
    public void addTodoSavesItInRepo() {
        // given
        TodoEntity mockSavedTodo = new TodoEntity("mjordan", "do 1");
        mockSavedTodo.setId(1);
        when(todoRepo.save(Matchers.<TodoEntity>any())).thenReturn(mockSavedTodo);

        // when
        int savedTodoId = service.addTodo("mjordan", "do 1");

        // then
        assertThat(savedTodoId).isEqualTo(1);
        verify(todoRepo).save(todoEntityCaptor.capture());
        assertThat(todoEntityCaptor.getValue().getUsername()).isEqualTo("mjordan");
        assertThat(todoEntityCaptor.getValue().getDescription()).isEqualTo("do 1");
    }

}
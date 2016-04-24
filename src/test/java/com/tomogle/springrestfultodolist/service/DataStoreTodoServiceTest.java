package com.tomogle.springrestfultodolist.service;

import com.tomogle.springrestfultodolist.domain.Todo;
import com.tomogle.springrestfultodolist.dto.TodoDTO;
import com.tomogle.springrestfultodolist.exception.TodoNotFoundException;
import com.tomogle.springrestfultodolist.repository.TodoRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.notNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DataStoreTodoServiceTest {

  @Mock
  private TodoRepository mockTodoRepository;

  private DataStoreTodoService serviceUnderTest;
  private final String ID = "1";
  private final String TITLE = "A Placeholder";
  private final String DESCRIPTION = "Some DESCRIPTION";

  @Before
  public void setUp() {
    serviceUnderTest = new DataStoreTodoService(mockTodoRepository);
  }

  @Test
  public void createShouldSaveTodo() throws Exception {
    TodoDTO input = createTodoDTO(ID, TITLE, DESCRIPTION);
    Todo expectedTodoToSave = createToDo(ID, TITLE, DESCRIPTION);
    when(mockTodoRepository.save(expectedTodoToSave)).thenReturn(expectedTodoToSave);

    serviceUnderTest.create(input);
    verify(mockTodoRepository).save(expectedTodoToSave);
  }

  @Test
  public void createShouldReturnTheOriginalTodoDTO() {
    TodoDTO input = createTodoDTO(ID, TITLE, DESCRIPTION);
    Todo todoReturnedFromSave = createToDo(ID, TITLE, DESCRIPTION);
    when(mockTodoRepository.save((Todo)notNull())).thenReturn(todoReturnedFromSave);

    TodoDTO result = serviceUnderTest.create(input);
    assertEquals("The resulting DTO did not equal the input DTO", input, result);
  }

  @Test(expected = TodoNotFoundException.class)
  public void findByIdShouldThrowIfCannotFindTodo() throws TodoNotFoundException {
    String idNotInDataStore = "ID not present in data store";
    when(mockTodoRepository.find(idNotInDataStore)).thenReturn(Optional.empty());

    serviceUnderTest.findById(idNotInDataStore);
  }

  @Test
  public void findByIdShouldReturnExpectedToDoDtoIfPresentInDataStore() throws TodoNotFoundException {
    String idInDataStore = "id in data store";
    Todo returnedFromDataStore = new Todo(idInDataStore, TITLE, DESCRIPTION);
    TodoDTO expectedResult = createTodoDTO(idInDataStore, TITLE, DESCRIPTION);
    when(mockTodoRepository.find(idInDataStore)).thenReturn(Optional.of(returnedFromDataStore));

    TodoDTO result = serviceUnderTest.findById(idInDataStore);
    assertEquals("The returned ToDoDTO was not the expected ToDoDTO", expectedResult, result);
  }

  private static TodoDTO createTodoDTO(String id, String title, String content) {
    return new TodoDTO(id, title, content);
  }

  private static Todo createToDo(String id, String title, String content) {
    return new Todo(id, title, content);
  }

}

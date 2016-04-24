package com.tomogle.springrestfultodolist.service;

import com.tomogle.springrestfultodolist.domain.Todo;
import com.tomogle.springrestfultodolist.dto.TodoDTO;
import com.tomogle.springrestfultodolist.exception.TodoBadIDException;
import com.tomogle.springrestfultodolist.exception.TodoNotFoundException;
import com.tomogle.springrestfultodolist.repository.TodoRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.notNull;
import static org.mockito.Mockito.times;
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

  @Test
  public void findAllGivenNoTodosInDatastoreShouldReturnEmptyList() {
    List<TodoDTO> expectedResult = new ArrayList<>();

    when(mockTodoRepository.findAll()).thenReturn(new ArrayList<>());
    List<TodoDTO> result = serviceUnderTest.findAll();
    assertEquals("An empty List of DTOs was expected but not returned", expectedResult, result);
  }

  @Test
  public void findAllShouldReturnConvertedToDoDTOsFromDataStore() {
    Todo todo1 = new Todo("1", "title", "contents");
    Todo todo2 = new Todo("2", "title 2", "contents 2");
    List<Todo> returnedFromDatastore = Arrays.asList(todo1, todo2);

    TodoDTO todoDTO1 = new TodoDTO("1", "title", "contents");
    TodoDTO todoDTO2 = new TodoDTO("2", "title 2", "contents 2");
    List<TodoDTO> expectedResult = Arrays.asList(todoDTO1, todoDTO2);

    when(mockTodoRepository.findAll()).thenReturn(returnedFromDatastore);
    List<TodoDTO> result = serviceUnderTest.findAll();
    assertEquals("The returned List of DTOs was not as expected", expectedResult, result);
  }

  @Test
  public void deleteShouldDeleteTheRequestId() throws TodoNotFoundException {
    String idToDelete = "id to delete";
    Todo deletedTodoReturnedFromRepository = new Todo(idToDelete, "title", "some content");
    when(mockTodoRepository.delete(idToDelete)).thenReturn(deletedTodoReturnedFromRepository);

    serviceUnderTest.delete(idToDelete);
    verify(mockTodoRepository, times(1)).delete(idToDelete);
  }

  @Test
  public void deleteShouldReturnDeletedTodoDetails() throws TodoNotFoundException {
    String idToDelete = "id to delete";
    Todo todoDeleteFromRepository = new Todo(idToDelete, "title", "content");
    TodoDTO expectedResult = new TodoDTO(idToDelete, "title", "content");
    when(mockTodoRepository.delete(idToDelete)).thenReturn(todoDeleteFromRepository);

    TodoDTO result = serviceUnderTest.delete(idToDelete);
    assertEquals(expectedResult, result);
  }

  @Test(expected = TodoNotFoundException.class)
  public void deleteGivenRepositoryThrowsTodoNotFoundExceptionShouldPropagateException() throws TodoNotFoundException {
    String idToDelete = "an ID";
    when(mockTodoRepository.delete(idToDelete)).thenThrow(new TodoNotFoundException(""));
    serviceUnderTest.delete(idToDelete);
  }

  @Test(expected = TodoBadIDException.class)
  public void updateGivenTodoDTOIdIsSetAndTodoDTOIdDoesNotMatchStringIdShouldThrowTodoBadIDException() throws TodoBadIDException {
    String id = "an ID";
    String differentId = "a different ID";
    TodoDTO todoDTO = new TodoDTO(differentId, "title", "content");

    serviceUnderTest.update(id, todoDTO);
  }

  @Test
  public void updateGivenTodoDTOWithMatchingIdShouldSaveTheUpdatedTodo() throws TodoBadIDException {
    String id = "an ID";
    TodoDTO todoDTO = new TodoDTO(id, "title", "content");
    Todo todoToBeSaved = new Todo(id, "title", "content");
    when(mockTodoRepository.save(todoToBeSaved)).thenReturn(todoToBeSaved);

    serviceUnderTest.update(id, todoDTO);
    verify(mockTodoRepository, times(1)).save(todoToBeSaved);
  }

  @Test
  public void updateGivenTodoDTOWithBlankTodoIdShouldSaveTheUpdatedTodo() throws TodoBadIDException {
    String id = "an ID";
    TodoDTO todoDTO = new TodoDTO("", "title", "content");
    Todo todoToBeSaved = new Todo(id, "title", "content");
    when(mockTodoRepository.save(todoToBeSaved)).thenReturn(todoToBeSaved);

    serviceUnderTest.update(id, todoDTO);
    verify(mockTodoRepository, times(1)).save(todoToBeSaved);
  }

  @Test
  public void updateGivenTodoDTOWithNullTodoIdShouldSaveTheUpdatedTodo() throws TodoBadIDException {
    String id = "an ID";
    TodoDTO todoDTO = new TodoDTO(null, "title", "content");
    Todo todoToBeSaved = new Todo(id, "title", "content");
    when(mockTodoRepository.save(todoToBeSaved)).thenReturn(todoToBeSaved);

    serviceUnderTest.update(id, todoDTO);
    verify(mockTodoRepository, times(1)).save(todoToBeSaved);
  }

  @Test
  public void updateGivenTodoDTOWithMatchingIdShouldReturnTheUpdatedDTO() throws TodoBadIDException {
    String id = "an ID";
    TodoDTO todoDTO = new TodoDTO(id, "title", "content");
    Todo todoToBeSaved = new Todo(id, "title", "content");
    Todo savedTodo = new Todo(id, "title different for test", "content");
    TodoDTO expectedResult = new TodoDTO(id, "title different for test", "content");
    when(mockTodoRepository.save(todoToBeSaved)).thenReturn(savedTodo);

    TodoDTO result = serviceUnderTest.update(id, todoDTO);
    assertEquals(expectedResult, result);
  }

  @Test
  public void updateGivenTodoDTOWithBlankTodoIdShouldReturnTheUpdatedDTO() throws TodoBadIDException {
    String id = "an ID";
    TodoDTO todoDTO = new TodoDTO("", "title", "content");
    Todo todoToBeSaved = new Todo(id, "title", "content");
    Todo savedTodo = new Todo(id, "title different for test", "content");
    TodoDTO expectedResult = new TodoDTO(id, "title different for test", "content");
    when(mockTodoRepository.save(todoToBeSaved)).thenReturn(savedTodo);

    TodoDTO result = serviceUnderTest.update(id, todoDTO);
    assertEquals(expectedResult, result);
  }

  private static TodoDTO createTodoDTO(String id, String title, String content) {
    return new TodoDTO(id, title, content);
  }

  private static Todo createToDo(String id, String title, String content) {
    return new Todo(id, title, content);
  }

}

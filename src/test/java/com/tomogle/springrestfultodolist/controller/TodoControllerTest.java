package com.tomogle.springrestfultodolist.controller;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tomogle.springrestfultodolist.dto.TodoDTO;
import com.tomogle.springrestfultodolist.exception.TodoBadIDException;
import com.tomogle.springrestfultodolist.exception.TodoNotFoundException;
import com.tomogle.springrestfultodolist.service.TodoService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockServletContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = MockServletContext.class)
@WebAppConfiguration
public class TodoControllerTest {

  // TODO: Further tests for bad input, e.g. title and description over max length

  private final String todoResourceBasePath = TodoController.TODO_RESOURCE_BASE_PATH;
  private final String todoByIdResourcePath = todoResourceBasePath + "/{id}";

  @Mock
  private TodoService mockTodoService;

  private MockMvc mvc;

  @InjectMocks
  private TodoController controllerUnderTest;

  @Before
  public void setUp() {
    MockitoAnnotations.initMocks(this);
    mvc = MockMvcBuilders.standaloneSetup(controllerUnderTest).build();
  }

  @Test
  public void getAllTodosShouldReturnOkResponseCode() throws Exception {
    mvc.perform(get(todoResourceBasePath))
            .andExpect(status().isOk());
  }

  @Test
  public void getAllTodosShouldReturnAllTodoDetailsAsAnArray() throws Exception {
    String todo1Id = "1";
    String todo1Title = "Title 1";
    String todo1Content = "Description 1";
    TodoDTO firstReturned = new TodoDTO(todo1Id, todo1Title, todo1Content);
    String todo2Id = "2";
    String todo2Title = "Title 2";
    String todo2Content = "Description 2";
    TodoDTO secondReturned = new TodoDTO(todo2Id, todo2Title, todo2Content);
    List<TodoDTO> todoDTOsReturnedByService = Arrays.asList(firstReturned, secondReturned);
    when(mockTodoService.findAll()).thenReturn(todoDTOsReturnedByService);

    mvc.perform(get(todoResourceBasePath)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$[0].id", is(todo1Id)))
            .andExpect(jsonPath("$[0].title", is(todo1Title)))
            .andExpect(jsonPath("$[0].content", is(todo1Content)))
            .andExpect(jsonPath("$[1].id", is(todo2Id)))
            .andExpect(jsonPath("$[1].title", is(todo2Title)))
            .andExpect(jsonPath("$[1].content", is(todo2Content)))
            .andExpect(jsonPath("$", hasSize(2)));

  }

  @Test
  public void getTodoByIdGivenIdFoundByServiceShouldReturnOkResponseCode() throws Exception {
    String idThatExists = "ID exists";

    TodoDTO todoReturnedFromService = new TodoDTO(idThatExists, "some title", "some contents");

    when(mockTodoService.findById(idThatExists)).thenReturn(todoReturnedFromService);

    mvc.perform(get(todoByIdResourcePath, idThatExists)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());
  }

  @Test
  public void getTodoByIdGivenIdFoundByServiceShouldReturnTodoDetails() throws Exception {
    String idThatExists = "IDexists";
    String title = "Todo Title";
    String content = "Some contents";

    TodoDTO todoReturnedFromService = new TodoDTO(idThatExists, title, content);

    when(mockTodoService.findById(idThatExists)).thenReturn(todoReturnedFromService);

    mvc.perform(get(todoByIdResourcePath, idThatExists)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id", is(idThatExists)))
            .andExpect(jsonPath("$.title", is(title)))
            .andExpect(jsonPath("$.content", is(content)));
  }

  @Test
  public void getTodoByIdGivenIdNotFoundByServiceShouldReturnNotFoundResponse() throws Exception {
    String idDoesNotExist = "IDdoesnotexist";
    when(mockTodoService.findById(idDoesNotExist)).thenThrow(new TodoNotFoundException(""));

    mvc.perform(get(todoByIdResourcePath, idDoesNotExist)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound());
  }

  @Test
  public void deleteTodoByIdGivenIdNotFoundByServiceShouldReturnNotFoundResponse() throws Exception {
    String idDoesNotExist = "IDdoesnotexist";
    when(mockTodoService.delete(idDoesNotExist)).thenThrow(new TodoNotFoundException(""));

    mvc.perform(delete(todoByIdResourcePath, idDoesNotExist)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound());
  }

  @Test
  public void deleteTodoByIdGivenIdFoundByServiceShouldRequestDeleteFromTheService() throws Exception {
    mvc.perform(delete(todoByIdResourcePath, "someid")
            .accept(MediaType.APPLICATION_JSON));
    verify(mockTodoService, times(1)).delete("someid");
  }

  @Test
  public void deleteTodoByIdGivenIdFoundByServiceShouldReturnOkResponseCode() throws Exception {
    mvc.perform(delete(todoByIdResourcePath, "someid")
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());
  }

  @Test
  public void deleteTodoByIdGivenIdFoundByServiceShouldReturnDeletedToDoDetails() throws Exception {
    String id = "someid";
    TodoDTO returnedFromService = new TodoDTO(id, "A Title", "Contents");

    when(mockTodoService.delete(id)).thenReturn(returnedFromService);
  }

  @Test
  public void createTodoShouldReturnCreatedResponseCode() throws Exception {
    TodoDTO todoToCreate = new TodoDTO("some ID", "some title", "some content");
    byte[] todoAsBytes = convertToJsonBytes(todoToCreate);

    mvc.perform(post(todoResourceBasePath)
            .content(todoAsBytes)
            .contentType(MediaType.APPLICATION_JSON_UTF8)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isCreated());
  }

  @Test
  public void createTodoShouldRequestCreateFromTheService() throws Exception {
    TodoDTO todoToCreate = new TodoDTO("some ID", "some title", "some content");
    byte[] todoAsBytes = convertToJsonBytes(todoToCreate);

    mvc.perform(post(todoResourceBasePath)
            .content(todoAsBytes)
            .contentType(MediaType.APPLICATION_JSON_UTF8)
            .accept(MediaType.APPLICATION_JSON));
    verify(mockTodoService, times(1)).create(todoToCreate);
  }

  @Test
  public void createTodoShouldReturnCreatedTodoDetails() throws Exception {
    String todoId = "some ID";
    String todoTitle = "some title";
    String todoContent = "some content";
    TodoDTO todoToCreate = new TodoDTO();
    todoToCreate.setTitle(todoTitle);
    todoToCreate.setContent(todoContent);
    TodoDTO todoThatIsCreated = new TodoDTO(todoId, todoTitle, todoContent);
    byte[] todoAsBytes = convertToJsonBytes(todoToCreate);

    when(mockTodoService.create(todoToCreate)).thenReturn(todoThatIsCreated);

    mvc.perform(post(todoResourceBasePath)
            .content(todoAsBytes)
            .contentType(MediaType.APPLICATION_JSON_UTF8)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id", is(todoId)))
            .andExpect(jsonPath("$.title", is(todoTitle)))
            .andExpect(jsonPath("$.content", is(todoContent)));

  }

  @Test
  public void updateTodoShouldReturnOkResponseCode() throws Exception {
    String todoId = "some ID";
    TodoDTO todoToUpdate = new TodoDTO(todoId, "some title", "some content");
    byte[] todoAsBytes = convertToJsonBytes(todoToUpdate);

    mvc.perform(put(todoByIdResourcePath, todoId)
            .content(todoAsBytes)
            .contentType(MediaType.APPLICATION_JSON_UTF8)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());
  }

  @Test
  public void updateTodoShouldRequestUpdateFromTheService() throws Exception {
    String todoId = "some ID";
    TodoDTO todoToUpdate = new TodoDTO(todoId, "some title", "some content");
    byte[] todoAsBytes = convertToJsonBytes(todoToUpdate);

    mvc.perform(put(todoByIdResourcePath, todoId)
            .content(todoAsBytes)
            .contentType(MediaType.APPLICATION_JSON_UTF8)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

    verify(mockTodoService, times(1)).update(todoId, todoToUpdate);
  }

  @Test
  public void updateTodoShouldReturnCreatedTodoDetails() throws Exception {
    String todoId = "some ID";
    String todoTitle = "some title";
    String todoContent = "some content";
    TodoDTO todoToUpdate = new TodoDTO(todoId, todoTitle, todoContent);
    byte[] todoAsBytes = convertToJsonBytes(todoToUpdate);

    when(mockTodoService.update(todoId, todoToUpdate)).thenReturn(todoToUpdate);

    mvc.perform(put(todoByIdResourcePath, todoId)
            .content(todoAsBytes)
            .contentType(MediaType.APPLICATION_JSON_UTF8)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id", is(todoId)))
            .andExpect(jsonPath("$.title", is(todoTitle)))
            .andExpect(jsonPath("$.content", is(todoContent)));

  }

  @Test
  public void updateTodoGivenServiceThrowsBadIDExceptionShouldReturnBadRequestResponse() throws Exception {
    String pathTodoId = "Some ID";
    String todoId = "another ID";
    String todoTitle = "some title";
    String todoContent = "some content";
    TodoDTO todoToUpdate = new TodoDTO(todoId, todoTitle, todoContent);
    byte[] todoAsBytes = convertToJsonBytes(todoToUpdate);

    when(mockTodoService.update(pathTodoId, todoToUpdate)).thenThrow(new TodoBadIDException(""));

    mvc.perform(put(todoByIdResourcePath, pathTodoId)
            .content(todoAsBytes)
            .contentType(MediaType.APPLICATION_JSON_UTF8)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest());
  }

  private static byte[] convertToJsonBytes(TodoDTO todoToCreate) throws JsonProcessingException {
    ObjectMapper mapper = new ObjectMapper();
    mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    return mapper.writeValueAsBytes(todoToCreate);
  }
}

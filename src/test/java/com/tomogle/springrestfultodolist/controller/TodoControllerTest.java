package com.tomogle.springrestfultodolist.controller;


import com.tomogle.springrestfultodolist.dto.TodoDTO;
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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = MockServletContext.class)
@WebAppConfiguration
public class TodoControllerTest {

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
    mvc.perform(MockMvcRequestBuilders.get(TodoController.TODO_RESOURCE_BASE_PATH))
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

    mvc.perform(MockMvcRequestBuilders.get(TodoController.TODO_RESOURCE_BASE_PATH)
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

    mvc.perform(MockMvcRequestBuilders.get(TodoController.TODO_RESOURCE_BASE_PATH + "/{id}", idThatExists)
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

    mvc.perform(MockMvcRequestBuilders.get(TodoController.TODO_RESOURCE_BASE_PATH + "/{id}", idThatExists)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id", is(idThatExists)))
            .andExpect(jsonPath("$.title", is(title)))
            .andExpect(jsonPath("$.content", is(content)));
  }

  @Test
  public void getTodoByIdGivenIdNotFoundByServiceShouldReturnNotFoundResponse() throws Exception {
    String idDoesNotExist = "IDdoesnotexist";
    when(mockTodoService.findById(idDoesNotExist)).thenThrow(new TodoNotFoundException(""));

    mvc.perform(MockMvcRequestBuilders.get(TodoController.TODO_RESOURCE_BASE_PATH + "/{id}", idDoesNotExist)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound());
  }

  @Test
  public void deleteTodoByIdGivenIdNotFoundByServiceShouldReturnNotFoundResponse() throws Exception {
    String idDoesNotExist = "IDdoesnotexist";
    when(mockTodoService.delete(idDoesNotExist)).thenThrow(new TodoNotFoundException(""));

    mvc.perform(MockMvcRequestBuilders.delete(TodoController.TODO_RESOURCE_BASE_PATH + "/{id}", idDoesNotExist)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound());
  }

  @Test
  public void deleteTodoByIdGivenIdFoundByServiceShouldRequestDeleteFromTheService() throws Exception {
    mvc.perform(MockMvcRequestBuilders.delete(TodoController.TODO_RESOURCE_BASE_PATH + "/{id}", "someid")
            .accept(MediaType.APPLICATION_JSON));
    verify(mockTodoService, times(1)).delete("someid");
  }

  @Test
  public void deleteTodoByIdGivenIdFoundByServiceShouldReturnOkResponseCode() throws Exception {
    mvc.perform(MockMvcRequestBuilders.delete(TodoController.TODO_RESOURCE_BASE_PATH + "/{id}", "someid")
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());
  }

  @Test
  public void deleteTodoByIdGivenIdFoundByServiceShouldReturnDeletedToDoDetails() {
    String id = "someid";
    TodoDTO returnedFromService = new TodoDTO(id, "A Title", "Contents");

    when(mockTodoService.delete(id)).thenReturn(returnedFromService);
  }
}

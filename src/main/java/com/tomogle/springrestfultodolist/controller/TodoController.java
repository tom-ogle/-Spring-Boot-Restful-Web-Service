package com.tomogle.springrestfultodolist.controller;

import com.tomogle.springrestfultodolist.Constants;
import com.tomogle.springrestfultodolist.dto.TodoDTO;
import com.tomogle.springrestfultodolist.exception.TodoNotFoundException;
import com.tomogle.springrestfultodolist.service.TodoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.tomogle.springrestfultodolist.controller.TodoController.TODO_RESOURCE_BASE_PATH;

/**
 * Restful controller for interacting with Todos.
 */
@RestController
@RequestMapping(TODO_RESOURCE_BASE_PATH)
class TodoController {

  static final String TODO_RESOURCE_BASE_PATH = Constants.BASE_API_PATH + "/todo";

  private final TodoService todoService;

  @Autowired
  public TodoController(TodoService todoService) {
    this.todoService = todoService;
  }

  @RequestMapping(method = RequestMethod.GET)
  public List<TodoDTO> getAllTodos() {
    return todoService.findAll();
  }

  @RequestMapping(value = "/{id}", method = RequestMethod.GET)
  public TodoDTO getTodoById(@PathVariable(value="id") String id) {
    return todoService.findById(id);
  }

  @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
  public TodoDTO deleteTodoById(@PathVariable(value="id") String id) {
    return todoService.delete(id);
  }

  @ExceptionHandler
  @ResponseStatus(HttpStatus.NOT_FOUND)
  public void handleTodoNotFoundException(TodoNotFoundException exception) {
    // TODO: Log the exception
  }
}

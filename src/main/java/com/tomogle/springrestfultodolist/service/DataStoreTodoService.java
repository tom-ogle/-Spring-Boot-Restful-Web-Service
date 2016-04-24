package com.tomogle.springrestfultodolist.service;

import com.tomogle.springrestfultodolist.domain.Todo;
import com.tomogle.springrestfultodolist.dto.TodoDTO;
import com.tomogle.springrestfultodolist.exception.TodoBadIDException;
import com.tomogle.springrestfultodolist.exception.TodoNotFoundException;
import com.tomogle.springrestfultodolist.repository.TodoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static java.lang.String.format;

/**
 * Implementation of TodoService backed by a data store.
 */
@Service
class DataStoreTodoService implements TodoService {

  private final TodoRepository repository;

  @Autowired
  public DataStoreTodoService(TodoRepository repository) {
    this.repository = repository;
  }

  @Override
  public List<TodoDTO> findAll() {
    // TODO
    return null;
  }

  @Override
  public TodoDTO findById(String id) throws TodoNotFoundException {
    Optional<Todo> todo = repository.find(id);
    Todo definitelyTodo = todo.orElseThrow(() -> new TodoNotFoundException(format("Could not find Todo with ID %s", id)));
    return convertTodoToDTO(definitelyTodo);
  }

  @Override
  public TodoDTO create(TodoDTO todoDTO) {
    Todo todoToSave = new Todo(todoDTO.getId(), todoDTO.getTitle(), todoDTO.getContent());
    Todo todoWasSaved = repository.save(todoToSave);
    return convertTodoToDTO(todoWasSaved);
  }

  private TodoDTO convertTodoToDTO(Todo todoToConvert) {
    return new TodoDTO(todoToConvert.getId(), todoToConvert.getTitle(), todoToConvert.getContent());
  }

  @Override
  public TodoDTO update(String id, TodoDTO todo) throws TodoBadIDException {
    // TODO
    return null;
  }

  @Override
  public TodoDTO delete(String id) throws TodoNotFoundException {
    // TODO
    return null;
  }
}

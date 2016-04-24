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
import java.util.stream.Collectors;

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
    List<Todo> todos = repository.findAll();
    return todos.stream().map(this::convertTodoToDTO).collect(Collectors.toList());
  }

  @Override
  public TodoDTO findById(String id) throws TodoNotFoundException {
    Optional<Todo> todo = repository.find(id);
    Todo definitelyTodo = todo.orElseThrow(() -> new TodoNotFoundException(format("Could not find Todo with ID %s", id)));
    return convertTodoToDTO(definitelyTodo);
  }

  @Override
  public TodoDTO create(TodoDTO todoDTO) {
    Todo todoToSave = convertTodoDTOToToDo(todoDTO);
    Todo todoWasSaved = repository.save(todoToSave);
    return convertTodoToDTO(todoWasSaved);
  }

  @Override
  public TodoDTO update(String id, TodoDTO todoDTO) throws TodoBadIDException {
    String idFromDTO = todoDTO.getId();
    verifyIdOrThrow(id, idFromDTO);
    addIdToDTOIfRequired(id, todoDTO, idFromDTO);
    Todo todoToUpdate = convertTodoDTOToToDo(todoDTO);
    Todo updatedTodo = repository.save(todoToUpdate);
    return convertTodoToDTO(updatedTodo);
  }

  @Override
  public TodoDTO delete(String id) throws TodoNotFoundException {
    Todo deletedTodo = repository.delete(id);
    return convertTodoToDTO(deletedTodo);
  }

  private void addIdToDTOIfRequired(String id, TodoDTO todoDTO, String idFromDTO) {
    if(!notEmpty(idFromDTO)) {
      todoDTO.setId(id);
    }
  }

  private void verifyIdOrThrow(String idToUpdate, String idFromDTO) throws TodoBadIDException {
    if(notEmpty(idFromDTO) && !idFromDTO.equals(idToUpdate)) {
      throw new TodoBadIDException(format("The provided ID ( %s ) did not match the ID in the Object ( %s )", idToUpdate, idFromDTO));
    }
  }

  private boolean notEmpty(String idFromDTO) {
    return idFromDTO != null && !idFromDTO.isEmpty();
  }

  private TodoDTO convertTodoToDTO(Todo todoToConvert) {
    return new TodoDTO(todoToConvert.getId(), todoToConvert.getTitle(), todoToConvert.getContent());
  }

  private Todo convertTodoDTOToToDo(TodoDTO todoDTO) {
    return new Todo(todoDTO.getId(), todoDTO.getTitle(), todoDTO.getContent());
  }
}

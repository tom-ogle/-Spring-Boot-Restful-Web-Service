package com.tomogle.springrestfultodolist.repository;

import com.tomogle.springrestfultodolist.domain.Todo;
import org.springframework.data.repository.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Interface providing persistence interactions for Todos.
 */
public interface TodoRepository extends Repository<Todo, String> {

  List<Todo> findAll();
  Optional<Todo> find(String id);
  Todo save(Todo toBeSaved);
  void delete(Todo toBeDeleted);

}

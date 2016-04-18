package com.tomogle.springrestfultodolist.controller;

import com.tomogle.springrestfultodolist.domain.Todo;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Restful controller for todos.
 */
@RestController
@RequestMapping("/todo")
public class TodoController {

  @RequestMapping(method = RequestMethod.GET)
  public Todo getTodoById(@RequestParam(value="id") long id) {
    return new Todo(id, "Hard coded Todo", "Some contents");
  }

//  @RequestMapping(method = RequestMethod.POST)
//  public
}

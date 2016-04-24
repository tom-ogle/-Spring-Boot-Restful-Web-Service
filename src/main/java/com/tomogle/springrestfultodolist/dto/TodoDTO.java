package com.tomogle.springrestfultodolist.dto;

import com.tomogle.springrestfultodolist.domain.Todo;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.Size;

public class TodoDTO {

  private String id;

  @NotEmpty
  @Size(max = Todo.MAX_LENGTH_TITLE)
  private String title;
  @Size(max = Todo.MAX_LENGTH_CONTENT)
  private String content;

  public TodoDTO() {
  }

  public TodoDTO(String id, String title, String content) {
    this.id = id;
    this.title = title;
    this.content = content;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    TodoDTO todoDTO = (TodoDTO) o;

    if (id != null ? !id.equals(todoDTO.id) : todoDTO.id != null) return false;
    if (title != null ? !title.equals(todoDTO.title) : todoDTO.title != null) return false;
    return content != null ? content.equals(todoDTO.content) : todoDTO.content == null;

  }

  @Override
  public int hashCode() {
    int result = id != null ? id.hashCode() : 0;
    result = 31 * result + (title != null ? title.hashCode() : 0);
    result = 31 * result + (content != null ? content.hashCode() : 0);
    return result;
  }
}

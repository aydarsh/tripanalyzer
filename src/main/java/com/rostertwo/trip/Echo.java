package com.rostertwo.trip;

import com.fasterxml.jackson.annotation.JsonCreator;

public class Echo {
  private String content;
  
  @JsonCreator
  public Echo(String content) {
    this.content = content;
  }

  public String getContent() {
    return content;
  }

}

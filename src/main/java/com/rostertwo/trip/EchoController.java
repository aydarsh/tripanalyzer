package com.rostertwo.trip;


import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class EchoController {
  
  @PostMapping("/echo")
  public Echo echoResponseController(@RequestBody Echo echoObject) {
    return echoObject;
  }
}

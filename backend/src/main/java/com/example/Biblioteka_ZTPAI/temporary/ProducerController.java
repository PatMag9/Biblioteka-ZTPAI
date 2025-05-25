package com.example.Biblioteka_ZTPAI.temporary;

import com.example.Biblioteka_ZTPAI.dto.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/test")
public class ProducerController {
    private Producer producer;
    private Producer2 jsonProducer;

    @Autowired
    public ProducerController(Producer producer, Producer2 jsonProducer) {
        this.producer = producer;
        this.jsonProducer = jsonProducer;
    }

    //localhost:8080/test/send?message=msg
    @GetMapping("/send")
    public ResponseEntity<String> sendMessage(@RequestParam("message") String message){
        producer.sendMessage(message);
        return ResponseEntity.ok("Message sent to RabbitMQ ...");
    }

    @PostMapping("/sendJson")
    public ResponseEntity<String> sendJsonMessage(@RequestBody User user){
        jsonProducer.sendJsonMessage(user);
        return ResponseEntity.ok("Json Message sent to RabbitMQ ...");
    }
}

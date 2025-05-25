package com.example.Biblioteka_ZTPAI.temporary;

import com.example.Biblioteka_ZTPAI.dto.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
public class Consumer {
    private static final Logger LOGGER = LoggerFactory.getLogger(Consumer.class);

    @RabbitListener(queues = "exampleQueue")
    public void consume(String message){
        LOGGER.info(String.format("Received message -> %s", message));
    }

    @RabbitListener(queues = "secondQueue")
    public void consumeJson(User user){
        LOGGER.info(String.format("Received message -> %s", user.toString()));
    }
}

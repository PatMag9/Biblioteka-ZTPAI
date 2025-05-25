package com.example.Biblioteka_ZTPAI.temporary;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class Producer {

    private static final String EXCHANGE  = "exampleExchange";
    private static final String ROUTING_KEY  = "exampleRoutingKey";

    private static final Logger LOGGER = LoggerFactory.getLogger(Producer.class);
    private RabbitTemplate rabbitTemplate;

    @Autowired
    public Producer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public  void sendMessage(String message){
        LOGGER.info(String.format("Message sent -> %s", message));
        rabbitTemplate.convertAndSend(EXCHANGE, ROUTING_KEY, message);
    }
}

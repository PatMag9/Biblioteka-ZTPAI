package com.example.Biblioteka_ZTPAI.temporary;

import com.example.Biblioteka_ZTPAI.dto.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class Producer2 {

    public static final String EXCHANGE  = "exampleExchange";
    public static final String SECOND_ROUTING_KEY  = "secondRoutingKey";
    private static final Logger LOGGER = LoggerFactory.getLogger(Producer2.class);
    private RabbitTemplate rabbitTemplate;

    @Autowired
    public Producer2(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void sendJsonMessage(User user){
        LOGGER.info(String.format("Json message sent -> %s", user.toString()));
        rabbitTemplate.convertAndSend(EXCHANGE, SECOND_ROUTING_KEY, user);
    }
}

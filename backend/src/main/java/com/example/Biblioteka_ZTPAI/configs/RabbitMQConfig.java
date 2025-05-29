package com.example.Biblioteka_ZTPAI.configs;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {
    public static final String REGISTER_QUEUE = "registerQueue";
    private static final String REGISTER_EXCHANGE  = "registerExchange";
    private static final String REGISTER_ROUTING_KEY  = "registerRoutingKey";

    @Bean
    public Queue registerQueue(){
        return new Queue(REGISTER_QUEUE, false);
    }

    @Bean
    public TopicExchange registerExchange(){
        return  new TopicExchange(REGISTER_EXCHANGE);
    }

    @Bean
    public Binding reisterBinding(){
        return BindingBuilder
                .bind(registerQueue())
                .to(registerExchange())
                .with(REGISTER_ROUTING_KEY);
    }

    @Bean
    public MessageConverter converter(){
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public AmqpTemplate amqpTemplate(ConnectionFactory connectionFactory){
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(converter());
        return rabbitTemplate;
    }

}

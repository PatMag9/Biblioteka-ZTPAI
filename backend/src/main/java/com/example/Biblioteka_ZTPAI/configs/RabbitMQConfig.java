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
    public static final String QUEUE = "exampleQueue";
    public static final String SECOND_QUEUE = "secondQueue";
    public static final String EXCHANGE  = "exampleExchange";
    public static final String ROUTING_KEY  = "exampleRoutingKey";
    public static final String SECOND_ROUTING_KEY  = "secondRoutingKey";

    @Bean
    public Queue queue() {
        return new Queue(QUEUE, false);
    }

    @Bean
    public Queue secondQueue(){
        return new Queue(SECOND_QUEUE, false);
    }

    @Bean
    public TopicExchange exchange(){
        return  new TopicExchange(EXCHANGE);
    }

    @Bean
    public Binding binding(){
        return BindingBuilder
                .bind(queue())
                .to(exchange())
                .with(ROUTING_KEY);
    }

    @Bean
    public Binding secondBinding(){
        return BindingBuilder
                .bind(secondQueue())
                .to(exchange())
                .with(SECOND_ROUTING_KEY);
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

//    SimpleMessageListenerContainer container(ConnectionFactory connectionFactory, MessageListenerAdapter listenerAdapter) {
//        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
//        container.setConnectionFactory(connectionFactory);
//        container.setQueueNames(QUEUE);
//        container.setMessageListener(listenerAdapter);
//        return container;
//    }

}

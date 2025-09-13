package com.fitness.aiservice.config;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;

import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMqConfig {


    @Value("${rabbitmq.exchange.name}")
    private String exchange;
    @Value("${rabbitmq.routing.key}")
    private String routingkey;
    @Value("${rabbitmq.queue.name}")
    private String queue;

    @Bean
    public Queue activityQueue(){      //creating queue
        return new Queue(queue, true);    // true because whrn rabbitmq restart it does not lost the queue
    }

    @Bean
    public DirectExchange activityExchange(){
        return new DirectExchange(exchange);
    }


    @Bean
    public Binding activityBinding(Queue activityQueue, DirectExchange activityExchange){
        return BindingBuilder.bind(activityQueue).to(activityExchange).with(routingkey);

    }

    @Bean
    public MessageConverter jsonMessageConverter(){     // it converts java objects into json
        return new Jackson2JsonMessageConverter();
    }
}



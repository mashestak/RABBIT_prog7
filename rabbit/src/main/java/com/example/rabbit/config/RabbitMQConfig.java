package com.example.rabbit.config;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.amqp.core.Binding;

@Configuration
public class RabbitMQConfig {

    // Настройка очереди для Task Queue
    @Bean
    public Queue taskQueue() {
        return new Queue("task_queue", true); // durable = true
    }

    // Настройка Fanout exchange для Publish/Subscribe
    @Bean
    public FanoutExchange fanoutExchange() {
        return new FanoutExchange("logs");
    }

    // Настройка Direct exchange для маршрутизации
    @Bean
    public TopicExchange directExchange() {
        return new TopicExchange("direct_logs");
    }

        // Создание очереди "info"
    @Bean
    public Queue infoQueue() {
        return new Queue("info", true); // Устойчивая очередь
    }

    // Создание очереди "error"
    @Bean
    public Queue errorQueue() {
        return new Queue("error", true); // Устойчивая очередь
    }

    // Привязка "info" к exchange с routing key "info"
    @Bean
    public Binding bindingInfo(Queue infoQueue, TopicExchange directExchange) {
        return BindingBuilder.bind(infoQueue).to(directExchange).with("info");
    }

    // Привязка "error" к exchange с routing key "error"
    @Bean
    public Binding bindingError(Queue errorQueue, TopicExchange directExchange) {
        return BindingBuilder.bind(errorQueue).to(directExchange).with("error");
    }
}

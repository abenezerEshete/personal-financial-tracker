package com.abenezer.personalfinancetracker.jms;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

@Component
public class BudgetNotificationProducer {

    private final JmsTemplate jmsTemplate;

    @Value("${jms.queue.name}")
    private String jsmQueueName;


    public BudgetNotificationProducer(JmsTemplate jmsTemplate) {
        this.jmsTemplate = jmsTemplate;
    }

    public void sendBudgetExceededNotification( Double currentAmount, Double limit) {
        String message = String.format(
                "Budget has exceeded the limit. Current Amount: %.2f, Limit: %.2f",
                currentAmount, limit
        );
        System.out.println("message Sent: " + message);
        jmsTemplate.convertAndSend(jsmQueueName, message);
    }
}


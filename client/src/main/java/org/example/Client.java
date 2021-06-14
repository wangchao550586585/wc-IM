package org.example;

import org.example.core.CommandController;
import org.example.core.LowSpeedConsumerClient;
import org.example.core.SoulTest1Client;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * Hello world!
 */
@SpringBootApplication
public class Client {
    public static void main(String[] args) {
        ConfigurableApplicationContext applicationContext = SpringApplication.run(Client.class);
        startChatClient(applicationContext);
        //startSoultest1Client(applicationContext);
        //startLowSpeedClient(applicationContext);
    }

    private static void startLowSpeedClient(ConfigurableApplicationContext applicationContext) {
        LowSpeedConsumerClient lowSpeedConsumerClient = applicationContext.getBean(LowSpeedConsumerClient.class);
        lowSpeedConsumerClient.doConnect();
    }

    private static void startSoultest1Client(ConfigurableApplicationContext applicationContext) {
        SoulTest1Client soulTest1Client = applicationContext.getBean(SoulTest1Client.class);
        soulTest1Client.doConnect();
    }

    private static void startChatClient(ApplicationContext applicationContext) {
        CommandController commandController = applicationContext.getBean(CommandController.class);
        try {
            commandController.commandThreadRunning();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}

package org.example.listen;

import org.example.command.BaseCommand;
import org.example.core.CommandController;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

/**
 * @Classname MyServletContextListener
 * @Description TODO
 * @Date 2021/6/10 23:11
 * @Created by wangchao
 */
@Component
public class MyServletContextListener  implements ApplicationListener<ContextRefreshedEvent> {
    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        ApplicationContext applicationContext = contextRefreshedEvent.getApplicationContext();
        CommandController commandController = applicationContext.getBean(CommandController.class);
        commandController.register(applicationContext.getBeansOfType(BaseCommand.class).values());
    }
}

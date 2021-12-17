package org.example.command;

import lombok.Data;
import org.springframework.stereotype.Service;

import java.util.Scanner;

/**
 * @Classname CharConsoleCommand
 * @Description TODO
 * @Date 2021/6/10 23:07
 * @Created by wangchao
 */
@Service
@Data
public class ChatConsoleCommand implements BaseCommand {
    public static final String KEY = "2";
    private String toUserId;
    private String message;

    @Override
    public String getKey() {
        return KEY;
    }

    @Override
    public String getTip() {
        return "聊天";
    }

    @Override
    public void exec(Scanner scanner) {
        System.out.print("请输入聊天的消息(userId:message)：");
        String[] info = null;
        while (true) {
            String input = scanner.next();
            info = input.split(":");
            if (info.length != 2) {
                System.out.println("请输入聊天的消息(userId:message):");
            }else {
                break;
            }
        }
        toUserId = info[0];
        message =  info[1];
    }

}

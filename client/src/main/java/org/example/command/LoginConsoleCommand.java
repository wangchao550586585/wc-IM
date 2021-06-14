package org.example.command;

import lombok.Data;
import org.springframework.stereotype.Service;

import java.util.Scanner;

/**
 * @Classname LoginConsoleCommand
 * @Description TODO
 * @Date 2021/6/10 23:06
 * @Created by wangchao
 */
@Service
@Data
public class LoginConsoleCommand implements BaseCommand {
    public static final String KEY = "1";

    private String username;
    private String password;
    @Override
    public String getKey() {
        return KEY;
    }

    @Override
    public String getTip() {
        return "登录";
    }

    @Override
    public void exec(Scanner scanner) {

        System.out.println("请输入用户信息(userId@password)  ");
        String[] info = null;
        while (true) {
            String input = scanner.next();
            info = input.split("@");
            if (info.length != 2) {
                System.out.println("请按照格式输入(id@password):");
            }else {
                break;
            }
        }
        username=info[0];
        password = info[1];
    }
}

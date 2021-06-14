package org.example.command;

import org.springframework.stereotype.Service;

import java.util.Scanner;

/**
 * @Classname LogoutConsoleCommand
 * @Description TODO
 * @Date 2021/6/10 23:06
 * @Created by wangchao
 */
@Service
public class LogoutConsoleCommand implements BaseCommand {
    public static final String KEY = "10";

    @Override
    public String getKey() {
        return KEY;
    }

    @Override
    public String getTip() {
        return "退出";
    }

    @Override
    public void exec(Scanner scanner) {
    }
}

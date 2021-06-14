package org.example.command;

import java.util.Scanner;

/**
 * @Classname BaseCommand
 * @Description TODO
 * @Date 2021/6/10 23:05
 * @Created by wangchao
 */
public interface BaseCommand {
    String getKey();

    String getTip();

    void exec(Scanner scanner);
}

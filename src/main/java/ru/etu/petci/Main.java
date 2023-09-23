package ru.etu.petci;

import ru.etu.petci.handlers.AddJobCommandHandler;
import ru.etu.petci.handlers.CommandHandler;
import ru.etu.petci.handlers.ContinueCommandHandler;
import ru.etu.petci.handlers.InitCommandHandler;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main {
    private static final Logger LOGGER = Logger.getLogger(Main.class.getName());
    private static final Map<String, CommandHandler> commandHandlersMap = new HashMap<>();


    static {
        LOGGER.setLevel(Level.WARNING);
        commandHandlersMap.put("init", new InitCommandHandler());
        commandHandlersMap.put("continue", new ContinueCommandHandler());
        commandHandlersMap.put("add", new AddJobCommandHandler());
    }


    public static void main(String[] args) {
        if (args.length == 0) {
            showHelp();
            System.exit(1);
        }
        CommandHandler handler = commandHandlersMap.get(args[0].trim());
        System.exit(handler.handle(args));
    }


    public static void showHelp() {
        System.out.println("Commands: ");
        System.out.println("init - configure repository for work with CI");
        System.out.println("continue - start repository monitoring");
        System.out.println("add - add new job");
    }
}
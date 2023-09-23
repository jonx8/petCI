package ru.etu.petci;


import ru.etu.petci.handlers.AddJobCommandHandler;
import ru.etu.petci.handlers.CommandHandler;
import ru.etu.petci.handlers.ContinueCommandHandler;
import ru.etu.petci.handlers.InitCommandHandler;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

public class Main {
    private static final Map<String, CommandHandler> commandHandlersMap = new HashMap<>();
    private static final Logger LOGGER;


    static {
        try (InputStream input = Main.class.getClassLoader().getResourceAsStream("logging.properties")) {
            LogManager.getLogManager().readConfiguration(input);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        LOGGER = Logger.getLogger(Main.class.getName());

        commandHandlersMap.put("init", new InitCommandHandler());
        commandHandlersMap.put("continue", new ContinueCommandHandler());
        commandHandlersMap.put("add", new AddJobCommandHandler());
    }


    public static void main(String[] args) {
        int exitStatus;
        if (args.length == 0) {
            showHelp();
            exitStatus = 1;
        } else {
            CommandHandler handler = commandHandlersMap.get(args[0].trim());
            exitStatus = handler.handle(args);
        }
        LOGGER.log(Level.FINE, "The program finished with exit code {0}", exitStatus);
        System.exit(exitStatus);
    }


    public static void showHelp() {
        System.out.println("Commands: ");
        System.out.println("init - configure repository for work with CI");
        System.out.println("continue - start repository monitoring");
        System.out.println("add - add new job");
    }
}
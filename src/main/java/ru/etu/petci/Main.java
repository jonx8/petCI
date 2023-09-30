package ru.etu.petci;


import ru.etu.petci.handlers.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

public class Main {
    private static final Map<String, Command> commandHandlersMap = new HashMap<>();
    private static final Logger LOGGER;


    static {
        try (InputStream input = Main.class.getResourceAsStream("logging.properties")) {

            LogManager.getLogManager().readConfiguration(input);
        } catch (IOException e) {
            System.out.println("Error while reading \"logging.properties\"");
        }
        LOGGER = Logger.getLogger(Main.class.getName());

        commandHandlersMap.put("init", new InitCommand());
        commandHandlersMap.put("continue", new ContinueCommand());
        commandHandlersMap.put("job", new JobCommand());
    }


    public static void main(String[] args) {
        int exitStatus = 1;
        if (args.length > 0) {
            Command cmd = commandHandlersMap.get(args[0]);
            if (cmd != null) {
                exitStatus = cmd.handle(args);
            } else {
                System.out.printf("'%s' is not a command%n", args[0]);
            }
        } else {
            showHelp();
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
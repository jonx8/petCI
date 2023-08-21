package ru.etu.petci;

import ru.etu.petci.handlers.CommandHandler;
import ru.etu.petci.handlers.ContinueCommandHandler;
import ru.etu.petci.handlers.InitCommandHandler;

import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main {
    public static final String SETTINGS_FILE_NAME = "settings.properties";
    public static final String JOBS_DIR_NAME = "";
    private static final Logger LOGGER = Logger.getLogger(Main.class.getName());

    static {
        LOGGER.setLevel(Level.WARNING);
    }


    public static void main(String[] args) {

        try {
            Properties settings = new Properties();
            settings.load(Main.class.getClassLoader().getResourceAsStream(SETTINGS_FILE_NAME));
            settings.getProperty("JOBS_DIR_NAME", "jobs");
        } catch (IOException e) {
            System.out.println("Unable to read settings property");
            System.exit(1);
        }

        int exitStatus = 0;
        if (args.length > 0) {
            CommandHandler handler;
            switch (args[0]) {
                case "init" -> {
                    LOGGER.info("User entered \"init\" command.");
                    handler = new InitCommandHandler();
                    exitStatus = handler.handle(args.length > 1 ? args[1] : "");
                }
                case "continue" -> {
                    LOGGER.info("User entered \"continue\" command.");
                    handler = new ContinueCommandHandler();
                    exitStatus = handler.handle("");
                }
                case "add" -> LOGGER.info("User entered \"add\" command.");
                default -> {
                    System.out.printf("petCI: \"%s\" is not a command. Use \"help\" command%n", args[0]);
                    LOGGER.log(Level.SEVERE, "User entered \"{0}\". Such a command does not exist.", args[0]);
                    exitStatus = 1;
                }
            }
        } else exitStatus = 1;

        LOGGER.log(Level.INFO, "The program has been terminated with exit code {0}.", exitStatus);
        System.exit(exitStatus);
    }
}

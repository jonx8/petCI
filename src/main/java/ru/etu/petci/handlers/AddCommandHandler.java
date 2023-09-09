package ru.etu.petci.handlers;

import ru.etu.petci.configuration.Configurator;

import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AddCommandHandler implements CommandHandler {
    private static final Logger LOGGER;

    static {
        LOGGER = Logger.getLogger(ContinueCommandHandler.class.getName());
        LOGGER.setLevel(Level.WARNING);
    }

    @Override
    public int handle(String arg) {

        Scanner scanner = new Scanner(System.in);
        var configurator = new Configurator();

        System.out.print("Job name: ");
        String jobName = scanner.nextLine();
        if (jobName.isBlank()) {
            LOGGER.severe("Job name must not be blank.");
            return 1;
        }

        System.out.print("Script path: ");
        String scriptPath = scanner.nextLine();
        if (scriptPath.isBlank()) {
            LOGGER.severe("Script path must not be blank");
            return 1;
        }

        configurator.saveJobsConfig(jobName, scriptPath);
        scanner.close();
        return 0;
    }
}

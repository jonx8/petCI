package ru.etu.petci.handlers;

import ru.etu.petci.configuration.Configurator;
import ru.etu.petci.jobs.Job;

import java.nio.file.Path;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.BackingStoreException;

public class AddCommandHandler implements CommandHandler {
    private static final Logger LOGGER;

    static {
        LOGGER = Logger.getLogger(ContinueCommandHandler.class.getName());
        LOGGER.setLevel(Level.WARNING);
    }

    @Override
    public int handle(String arg) {

        var scanner = new Scanner(System.in);
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

        scanner.close();

        try {
            configurator.saveJobsConfig(new Job(Path.of(scriptPath), jobName));
        } catch (BackingStoreException e) {
            LOGGER.log(Level.SEVERE, e.getMessage());
            return 1;
        }

        return 0;
    }
}

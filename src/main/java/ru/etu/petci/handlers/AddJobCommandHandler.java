package ru.etu.petci.handlers;

import ru.etu.petci.configuration.Configurator;
import ru.etu.petci.jobs.Job;

import java.io.IOException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AddJobCommandHandler implements CommandHandler {
    private static final Logger LOGGER = Logger.getLogger(AddJobCommandHandler.class.getName());

    @Override
    public int handle(String[] args) {
        try (var scanner = new Scanner(System.in)) {
            System.out.print("Job name: ");
            String jobName = scanner.nextLine();
            if (jobName.isBlank()) {
                LOGGER.severe("Job name must not be blank.");
                return 1;
            }

            System.out.print("Script name: ");
            String scriptName = scanner.nextLine();
            if (scriptName.isBlank()) {
                LOGGER.severe("Script path must not be blank");
                return 1;
            }
            Configurator.saveJobConfig(new Job(jobName, scriptName, true));
        } catch (IOException e) {
            LOGGER.severe(e.getMessage());
            return 1;
        }
        return 0;
    }
}

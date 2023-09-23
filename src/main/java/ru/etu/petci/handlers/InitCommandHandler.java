package ru.etu.petci.handlers;

import ru.etu.petci.configuration.Configurator;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

import static ru.etu.petci.configuration.Configurator.JOBS_DIR;
import static ru.etu.petci.configuration.Configurator.LOGS_DIR;

public class InitCommandHandler implements CommandHandler {
    private static final Logger LOGGER = Logger.getLogger(InitCommandHandler.class.getName());


    @Override
    public int handle(String[] args) {
        var scanner = new Scanner(System.in);

        // If branchName is empty, using master-branch
        System.out.print("Name of observing branch (default - master): ");
        var branchName = scanner.nextLine().strip();
        if (branchName.isBlank()) {
            branchName = "master";
        }

        try {
            new File(JOBS_DIR).mkdirs();
            new File(LOGS_DIR).mkdirs();
            Configurator.saveRepositoryConfig(branchName);

        } catch (IOException e) {
            LOGGER.severe(e.getMessage());
            return 1;
        }

        System.out.println("Successful initialization!");
        return 0;
    }

}

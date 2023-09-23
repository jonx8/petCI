package ru.etu.petci.handlers;

import ru.etu.petci.configuration.Configurator;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

import static ru.etu.petci.configuration.Configurator.JOBS_DIR;

public class InitCommandHandler implements CommandHandler {
    private static final Logger LOGGER = Logger.getLogger(InitCommandHandler.class.getName());

    static {
        LOGGER.setLevel(Level.INFO);
    }


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
            Configurator.saveRepositoryConfig(branchName);
            if (new File(JOBS_DIR).mkdir()) {
                LOGGER.log(Level.CONFIG, "Directory \"{0}\" has been created", JOBS_DIR);
            } else {
                LOGGER.log(Level.WARNING, "Failed while creating directory \"{0}\"", JOBS_DIR);
            }
        } catch (IOException e) {
            LOGGER.severe(e.getMessage());
            return 1;
        }

        System.out.println("Successful initialization!");
        return 0;
    }

}

package ru.etu.petci.handlers;

import ru.etu.petci.configuration.Configurator;

import java.io.IOException;
import java.util.Objects;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class InitCommandHandler implements CommandHandler {
    private static final Logger LOGGER = Logger.getLogger(InitCommandHandler.class.getName());

    static {
        LOGGER.setLevel(Level.WARNING);
    }

    @Override
    public int handle(String arg) {
        Objects.requireNonNull(arg);

        var configurator = new Configurator();
        var scanner = new Scanner(System.in);

        System.out.println("--Initializing--");

        // If path to repository has not been specified
        String repositoryPath = arg;
        if (repositoryPath.isEmpty()) {
            System.out.print("Repository path (default - current dir): ");
            repositoryPath = scanner.nextLine().strip();
            if (repositoryPath.isEmpty()) {
                repositoryPath = ".";
            }
        }


        // If branchName is empty, using master-branch
        System.out.print("Name of observing branch (default - master): ");
        var branchName = scanner.nextLine().strip();
        try {
            if (branchName.isEmpty()) {
                configurator.saveRepositoryConfig(repositoryPath, "master");
            } else {
                configurator.saveRepositoryConfig(repositoryPath, branchName);
            }
        } catch (IOException e) {
            LOGGER.severe(e.getMessage());
            return 1;
        }
        scanner.close();
        System.out.println("Successful initialization!");
        return 0;
    }

}

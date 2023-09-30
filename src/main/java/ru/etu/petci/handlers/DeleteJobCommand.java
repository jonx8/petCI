package ru.etu.petci.handlers;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.Level;
import java.util.logging.Logger;

import static ru.etu.petci.configuration.Configurator.JOBS_DIR;


public class DeleteJobCommand implements Command {
    private static final Logger LOGGER = Logger.getLogger(DeleteJobCommand.class.getName());

    @Override
    public int handle(String[] args) {
        if (args.length != 3) {
            System.out.println("Needed to specify job name");
            return 1;
        }

        try {
            Files.delete(Path.of(JOBS_DIR + args[2] + ".properties"));
        } catch (IOException e) {
            LOGGER.severe(e.getMessage());
            return 1;
        }

        LOGGER.log(Level.INFO, "Job \"{0}\" has been deleted successfully", args[2]);
        return 0;
    }
}

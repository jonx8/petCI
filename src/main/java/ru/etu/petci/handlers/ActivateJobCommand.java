package ru.etu.petci.handlers;

import ru.etu.petci.jobs.Job;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

import static ru.etu.petci.configuration.Configurator.*;

public class ActivateJobCommand implements Command {
    private final boolean isActive;
    private static final Logger LOGGER = Logger.getLogger(ActivateJobCommand.class.getName());

    public ActivateJobCommand(boolean isActive) {
        this.isActive = isActive;
    }

    @Override
    public int handle(String[] args) {
        if (args.length != 3) {
            System.out.println("Needed to specify job name");
            return 1;
        }
        Job job = readJobConfig(new File(JOBS_DIR + args[2] + ".properties"));
        if (job == null) return 1;

        if (job.isActive() != isActive) {
            try {
                saveJobConfig(new Job(job.name(), job.scriptName(), isActive));
            } catch (IOException e) {
                LOGGER.severe(e.getMessage());
                return 1;
            }
        }
        return 0;
    }
}

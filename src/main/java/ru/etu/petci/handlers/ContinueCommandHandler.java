package ru.etu.petci.handlers;


import ru.etu.petci.configuration.Configurator;
import ru.etu.petci.exceptions.RepositoryNotFoundException;
import ru.etu.petci.jobs.Job;
import ru.etu.petci.jobs.JobsExecutor;
import ru.etu.petci.observers.RepositoryObserver;

import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.BackingStoreException;

public class ContinueCommandHandler implements CommandHandler {

    private static final Logger LOGGER;

    static {
        LOGGER = Logger.getLogger(ContinueCommandHandler.class.getName());
        LOGGER.setLevel(Level.WARNING);
    }

    @Override
    public int handle(String arg) {
        Objects.requireNonNull(arg);
        var configurator = new Configurator();
        try {
            RepositoryObserver repositoryObserver = configurator.readRepositoryConfig();
            List<Job> jobs = configurator.readJobsConfig();
            repositoryObserver.setExecutor(new JobsExecutor(jobs));
            repositoryObserver.start();
        } catch (InterruptedException e) {
            LOGGER.log(Level.SEVERE, "The program was interrupted");
            Thread.currentThread().interrupt();
        } catch (RepositoryNotFoundException | BackingStoreException e) {
            LOGGER.log(Level.SEVERE, e.getMessage());
        }
        return 1;
    }
}

package ru.etu.petci.handlers;


import ru.etu.petci.configuration.Configurator;
import ru.etu.petci.jobs.Job;
import ru.etu.petci.jobs.JobsExecutor;
import ru.etu.petci.observers.RepositoryObserver;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ContinueCommandHandler implements CommandHandler {

    private static final Logger LOGGER = Logger.getLogger(ContinueCommandHandler.class.getName());

    @Override
    public int handle(String[] args) {
        try {
            RepositoryObserver repositoryObserver = Configurator.readRepositoryConfig();
            List<Job> jobs = Configurator.readJobsConfig();
            repositoryObserver.setExecutor(new JobsExecutor(jobs));
            repositoryObserver.start();
        } catch (InterruptedException e) {
            LOGGER.warning("The program was interrupted");
            Thread.currentThread().interrupt();
        } catch (IOException e) {
            LOGGER.severe(e.getMessage());
        }
        return 1;
    }
}

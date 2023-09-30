package ru.etu.petci.handlers;

import ru.etu.petci.jobs.Job;

import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

import static ru.etu.petci.configuration.Configurator.readAllJobs;

public class ListJobCommand implements Command {
    private static final Logger LOGGER = Logger.getLogger(ListJobCommand.class.getName());

    @Override
    public int handle(String[] args) {
        try {
            List<Job> jobs = readAllJobs();
            jobs.forEach(System.out::println);
        } catch (IOException e) {
            LOGGER.severe(e.getMessage());
            return 1;
        }
        return 0;
    }
}

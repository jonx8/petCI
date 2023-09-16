package ru.etu.petci.jobs;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

public class JobsExecutor {

    private final List<Job> jobs;
    private static final Logger LOGGER = Logger.getLogger(JobsExecutor.class.getName());

    static {
        LOGGER.setLevel(Level.WARNING);
    }

    public JobsExecutor(List<Job> jobs) {
        this.jobs = jobs;
    }

    public void runJobs() throws IOException {
        Objects.requireNonNull(jobs);
        for (Job job : jobs) {
            if (!job.isActive()) {
                System.out.printf("Job \"%s\": Deactivated%n", job.name());
                continue;
            }
            System.out.printf("Run job \"%s\"...%n%n", job.name());

            if (job.execute() == 0) {
                System.out.printf("%n--Succeed--%n");
            } else {
                System.out.printf("%n--Failed--%n");
            }
        }
    }

}

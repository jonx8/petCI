package ru.etu.petci.configuration;


import ru.etu.petci.jobs.Job;
import ru.etu.petci.observers.RepositoryObserver;

import java.io.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class Configurator {

    public static final String JOBS_DIR = "petCI/jobs/";
    public static final String LOGS_DIR = "petCI/logs/";

    public static final String REPOSITORY_PROPERTY = "petCI/repo.properties";

    private static final Logger LOGGER = Logger.getLogger(Configurator.class.getName());

    private Configurator() {
    }

    public static RepositoryObserver readRepositoryConfig() throws IOException {
        try (var reader = new FileReader(REPOSITORY_PROPERTY)) {
            Properties properties = new Properties();
            properties.load(reader);
            String branchName = properties.getProperty("branch_name");
            String lastHash = properties.getProperty("last_hash");
            return RepositoryObserver.of(branchName, lastHash);
        }
    }


    public static void saveRepositoryConfig(String branchName, String lastHash) throws IOException {
        try (var writer = new FileWriter(REPOSITORY_PROPERTY)) {
            Properties properties = new Properties();
            properties.setProperty("last_hash", lastHash);
            properties.setProperty("branch_name", branchName);
            properties.store(writer, "Repository config");
            LOGGER.log(Level.FINE, "File {0} was updated", REPOSITORY_PROPERTY);
        }
    }

    public static void saveRepositoryConfig(String branchName) throws IOException {
        saveRepositoryConfig(branchName, "");
    }


    public static List<Job> readJobsConfig() throws IOException {
        List<Job> jobs = new ArrayList<>();

        File jobsDir = new File(JOBS_DIR);
        File[] propertiesArray = jobsDir.listFiles(((dir, name) -> name.toLowerCase().endsWith(".properties")));
        if (Objects.isNull(propertiesArray)) {
            throw new IOException("Error while reading jobs directory: \"%s\"".formatted(jobsDir.getAbsolutePath()));
        }
        for (File jobProperty : propertiesArray) {
            try (var reader = new FileReader(jobProperty)) {
                Properties properties = new Properties();
                properties.load(reader);

                String jobName = properties.getProperty("name");
                String scriptName = properties.getProperty("script_name");
                boolean isActive = Boolean.parseBoolean(properties.getProperty("active"));

                if (jobName != null && scriptName != null) {
                    jobs.add(new Job(jobName, scriptName, isActive));
                } else {
                    LOGGER.log(Level.WARNING, "File \"{0}\" is corrupted", jobProperty.getName());
                }

            } catch (IOException e) {
                // Only logging because needed to continue the work of application.
                LOGGER.log(Level.WARNING, "Error while reading \"{0}\" file", jobProperty.getName());
            }
        }
        return jobs;
    }


    public static void saveJobConfig(Job job) throws IOException {
        Objects.requireNonNull(job);
        try (var writer = new FileWriter(JOBS_DIR + job.name() + ".properties")) {
            Properties properties = new Properties();
            properties.setProperty("name", job.name());
            properties.setProperty("script_name", job.scriptName());
            properties.setProperty("active", String.valueOf(job.isActive()));
            properties.store(writer, "Job settings");
            LOGGER.log(Level.FINE, "Config for job \"{0}\" was updated", job.name());
        }
    }
}

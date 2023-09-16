package ru.etu.petci.configuration;


import ru.etu.petci.jobs.Job;
import ru.etu.petci.observers.RepositoryObserver;

import java.io.*;
import java.util.*;

public final class Configurator {

    public static final String JOBS_DIR = "jobs/";
    public static final String REPOSITORY_PROPERTY = ".repo.properties";

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
        }
    }

    public static void saveRepositoryConfig(String branchName) throws IOException {
        saveRepositoryConfig(branchName, "");
    }


    public static List<Job> readJobsConfig() {
        List<Job> jobs = new ArrayList<>();
        File jobsDir = new File(JOBS_DIR);
        File[] propertiesArray = jobsDir.listFiles(((dir, name) -> name.toLowerCase().endsWith(".properties")));
        Objects.requireNonNull(propertiesArray);
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
                    System.out.printf("File \"%s\" is corrupted%n", jobProperty.getName());
                }

            } catch (IOException e) {
                // Ignoring because it needs to continue work.
                // TODO Logging
                System.out.printf("Error while reading \"%s\" file%n", jobProperty.getName());
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
        }
    }
}

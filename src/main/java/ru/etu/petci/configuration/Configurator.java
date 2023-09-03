package ru.etu.petci.configuration;


import ru.etu.petci.jobs.Job;
import ru.etu.petci.observers.RepositoryObserver;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import static ru.etu.petci.Main.JOBS_SETTINGS_FILE;

public class Configurator {


    public RepositoryObserver readRepositoryConfig() throws IOException {
        RepositoryObserver observer = new RepositoryObserver();
        Properties repoConfig = new Properties();
        try (var propertyReader = new FileReader("repository.properties")) {
            repoConfig.load(propertyReader);
            observer.setRepositoryPath(repoConfig.getProperty("repository_path"));
            observer.setBranchName(repoConfig.getProperty("branch_name", "master"));
            observer.setLastHash(repoConfig.getProperty("last_hash"));
        }
        return observer;
    }


    public void saveRepositoryConfig(String repoPath, String branchName, String lastHash) throws IOException {
        var properties = new Properties();
        properties.setProperty("repository_path", Path.of(repoPath).toAbsolutePath().normalize().toString());
        properties.setProperty("branch_name", branchName);
        properties.setProperty("last_hash", lastHash.length() == 40 ? lastHash : "null");
        try (var propertyWriter = new FileWriter("repository.properties")) {
            properties.store(propertyWriter, "Repository configuration");
        }
    }

    public void saveRepositoryConfig(String repoPath, String branchName) throws IOException {
        saveRepositoryConfig(repoPath, branchName, "");
    }


    public List<Job> readJobsConfig() throws IOException {
        List<Job> jobsList = new ArrayList<>();
        try (var propertiesReader = new FileReader(JOBS_SETTINGS_FILE)) {
            var properties = new Properties();
            properties.load(propertiesReader);
            for (String jobName : properties.stringPropertyNames()) {
                Path scriptPath = Path.of(properties.getProperty(jobName));
                jobsList.add(new Job(scriptPath, jobName));
            }
        }
        return jobsList;
    }


    public void saveJobsConfig(String jobName, String scriptPath) throws IOException {
        try (var propertiesReader = new FileReader(JOBS_SETTINGS_FILE);
             var propertiesWriter = new FileWriter(JOBS_SETTINGS_FILE)) {
            var properties = new Properties();
            properties.load(propertiesReader);
            if (properties.getProperty(jobName) == null) {
                properties.setProperty(jobName, Path.of(scriptPath).toAbsolutePath().normalize().toString());
                properties.store(propertiesWriter, "Jobs settings properties");
            } else {
                System.out.printf("Job with name \"%s\" has already existed%n", jobName);
            }
        }
    }

}

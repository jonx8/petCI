package ru.etu.petci.configuration;


import ru.etu.petci.jobs.Job;
import ru.etu.petci.observers.RepositoryObserver;

import java.io.*;
import java.nio.file.Path;
import java.util.Objects;
import java.util.Properties;

import static ru.etu.petci.Main.JOBS_DIR_NAME;

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

    public void saveJobConfig(Job job) {
        Objects.requireNonNull(job);
        try (var out = new BufferedWriter(new FileWriter(JOBS_DIR_NAME + File.separator + job.getName()))) {
            out.append(job.getName());
            out.newLine();
            out.append(job.getScriptFile().toString());
            out.newLine();
            out.append(String.valueOf(job.isActive()));
        } catch (IOException e) {
            System.out.println("Error with writing jobFile");
            System.exit(1);
        }
    }
}

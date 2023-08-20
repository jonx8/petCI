package ru.etu.petci.configuration;


import ru.etu.petci.observers.RepositoryObserver;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.Properties;

import static ru.etu.petci.Main.JOBS_DIR_NAME;
import static ru.etu.petci.Main.SETTINGS_FILE_NAME;

public class Configurator {


    public RepositoryObserver readRepositoryConfig() {
        RepositoryObserver observer = new RepositoryObserver();
        try {
            Properties repoConfig = new Properties();
            repoConfig.load(new FileReader("repository.properties"));
            if (!observer.setRepositoryPath(repoConfig.getProperty("repository_path"))) {
                System.exit(1);
            }
            observer.setBranchName(repoConfig.getProperty("branch_name", "master"));
            observer.setLastHash(repoConfig.getProperty("last_hash", null));
        } catch (IOException e) {
            System.out.println("Error while reading repository config");
            System.exit(1);
        }
        return observer;
    }


    public void saveRepositoryConfig(String repoPath, String branchName, String lastHash) {
        try {
            var properties = new Properties();
            properties.setProperty("repository_path", Path.of(repoPath).toAbsolutePath().normalize().toString());
            properties.setProperty("branch_name", branchName);
            properties.setProperty("last_hash", lastHash.length() == 40 ? lastHash : "null");
            properties.store(new FileWriter("repository.properties"), "Repository configuration");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void saveRepositoryConfig(String repoPath, String branchName) {
        saveRepositoryConfig(repoPath, branchName, "");
    }


    /**
     * <p>This method performs the check whether the current folder
     * is an application folder. An application folder must consist
     * of a configuration file with name {@link}
     * and a directory with name {@link}.</p>
     * <p>If there is no application directory, then the method returned false,
     * user should use "init" command.</p>
     */
    public boolean checkApplicationDir() {
        return Files.isDirectory(Path.of(JOBS_DIR_NAME)) && Files.isRegularFile(Path.of(SETTINGS_FILE_NAME));
    }
}

package ru.etu.petci.observers;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Properties;
import java.util.Scanner;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RepositoryObserver {

    private Path repositoryPath;
    private String lastHash;             // Hash of last commit
    private String branchName;  // Name of the observed branch
    private final ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
    private static final Logger LOGGER = Logger.getLogger(RepositoryObserver.class.getName());

    static {
        LOGGER.setLevel(Level.INFO);
    }

    public boolean setRepositoryPath(Path path) {
        boolean isSet = false;
        path = path.toAbsolutePath().normalize();

        Process gitProcess = null;

        try {
            gitProcess = Runtime.getRuntime().exec("git ls-remote %s".formatted(path));
            if (gitProcess.exitValue() == 0) {
                repositoryPath = path;
                isSet = true;
            } else {
                LOGGER.log(Level.WARNING, "Unable to find repository by path: {0}", repositoryPath);
                System.out.println("Unable to find repository by path");
            }
        } catch (IOException e) {
            LOGGER.warning("Error while executing 'git ls-remote'.%n" + e.getMessage());
        } finally {
            if (gitProcess != null) {
                gitProcess.destroy();
            }
        }
        return isSet;
    }


    public String getLastHash() {
        return lastHash;
    }

    public void setBranchName(String branchName) {
        this.branchName = branchName;
    }

    public String getBranchName() {
        return branchName;
    }

    public boolean setRepositoryPath(String path) {
        return setRepositoryPath(Path.of(path));
    }

    public Path getRepositoryPath() {
        return repositoryPath;
    }


    private void checkRepositoryUpdate() {
        Path lastCommitMaster = Path.of(repositoryPath + "/.git/refs/heads/" + branchName);
        try (Scanner scanner = new Scanner(lastCommitMaster.toFile())) {
            String currentHash = scanner.nextLine();
            if (currentHash.length() == 40 && !currentHash.equals(lastHash)) {
                lastHash = currentHash;
                try {
                    Properties properties = new Properties();
                    properties.load(new FileReader("repository.properties"));
                    properties.setProperty("last_hash", lastHash);
                    properties.store(new FileWriter("repository.properties"), "repository settings");
                } catch (IOException e) {
                    LOGGER.log(Level.WARNING, "Error while working with repository properties");
                }
                LOGGER.log(Level.INFO, "Commits checked. New commit was found. Hash: {0}", lastHash);
            } else {
                LOGGER.info("Commits checked. No new commits found.");
            }
        } catch (FileNotFoundException e) {
            LOGGER.warning(e.getMessage());
            System.out.printf("Branch \"%s\" does not exist!%n", getBranchName());
            service.shutdown();
            System.exit(1);
        }
    }

    public void start() throws InterruptedException {
        // Check for a new commit per 3 seconds
        if (repositoryPath != null) {
            service.scheduleWithFixedDelay(this::checkRepositoryUpdate, 0, 3, TimeUnit.SECONDS);
            Thread.currentThread().join();
        }
    }

    public void setLastHash(String lastHash) {
        if (lastHash.length() == 40) {
            this.lastHash = lastHash;
        }
    }
}

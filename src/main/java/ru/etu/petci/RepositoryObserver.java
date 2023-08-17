package ru.etu.petci;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Scanner;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RepositoryObserver {

    private boolean isUpdated = false;
    private Path repositoryPath;
    private String lastHash;             // Hash of last commit
    private String branchName = "main";  // Name of the observed branch
    private final ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
    private static final Logger LOGGER = Logger.getLogger(RepositoryObserver.class.getName());

    public RepositoryObserver() {
        LOGGER.setLevel(Level.WARNING);
    }

    public void setRepositoryPath(Path path) {
        path = path.toAbsolutePath().normalize();

        Process gitProcess = null;

        try {
            gitProcess = Runtime.getRuntime().exec("git ls-remote %s".formatted(path));
            if (gitProcess.exitValue() == 0) {
                repositoryPath = path;
            } else {
                LOGGER.warning("Unable to find repository by path: " + repositoryPath);
                System.out.println("Unable to find repository by path");
            }
        } catch (IOException e) {
            LOGGER.warning("Error while executing 'git ls-remote'. \n" + e.getMessage());
        } finally {
            if (gitProcess != null) {
                gitProcess.destroy();
            }
        }
    }

    public void setBranchName(String branchName) {
        this.branchName = branchName;
    }

    public String getBranchName() {
        return branchName;
    }

    public boolean isUpdated() {
        return isUpdated;
    }

    public void setRepositoryPath(String path) {
        setRepositoryPath(Path.of(path));
    }

    public Path getRepositoryPath() {
        return repositoryPath;
    }


    private void checkRepositoryUpdate() {
        isUpdated = false;
        Path lastCommitMaster = Path.of(repositoryPath + "/.git/refs/heads/" + branchName);
        try (Scanner scanner = new Scanner(lastCommitMaster.toFile())) {
            String currentHash = scanner.nextLine();
            if (currentHash.length() == 40 && !currentHash.equals(lastHash)) {
                lastHash = currentHash;
                LOGGER.info("Commits checked. New commit was found. Hash: " + lastHash);
                isUpdated = true;
            } else {
                LOGGER.info("Commits checked. No new commits found.");
            }
        } catch (FileNotFoundException e) {
            LOGGER.warning(e.getMessage());
        }
    }

    public void start() {
        // Check for a new commit per 3 seconds
        if (repositoryPath != null) {
            service.scheduleAtFixedRate(this::checkRepositoryUpdate, 0, 3, TimeUnit.SECONDS);
        }
    }
}

package ru.etu.petci.observers;

import ru.etu.petci.exceptions.RepositoryNotFoundException;
import ru.etu.petci.jobs.JobsExecutor;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Objects;
import java.util.Scanner;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;

import static ru.etu.petci.configuration.Configurator.REPOSITORY_PREFERENCES;

public class RepositoryObserver {

    private Path repositoryPath;
    private String lastHash;         // Hash of last commit
    private String branchName;       // Name of the observed branch
    private JobsExecutor executor;
    private final ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
    private static final Logger LOGGER = Logger.getLogger(RepositoryObserver.class.getName());

    static {
        LOGGER.setLevel(Level.INFO);
    }


    public boolean isRepositoryExists() throws InterruptedException {
        if (Objects.isNull(repositoryPath)) {
            return false;
        }
        Process gitProcess = null;
        int exitStatus = -1;
        try {
            gitProcess = Runtime.getRuntime().exec("git ls-remote %s".formatted(repositoryPath));
            gitProcess.waitFor();
            exitStatus = gitProcess.exitValue();
        } catch (IOException e) {
            LOGGER.warning("Error while executing 'git ls-remote'.%n" + e.getMessage());
        } finally {
            if (gitProcess != null) {
                gitProcess.destroy();
            }
        }
        return exitStatus == 0;
    }


    public void setRepositoryPath(Path path) {
        if (Objects.isNull(path))
            return;
        repositoryPath = path.toAbsolutePath().normalize();
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

    public void setRepositoryPath(String path) {
        if (Objects.isNull(path))
            return;
        setRepositoryPath(Path.of(path));
    }

    public Path getRepositoryPath() {
        return repositoryPath;
    }


    private void checkRepositoryUpdate() {
        Path lastCommitMaster = Path.of(repositoryPath + "/.git/refs/heads/" + branchName);
        try (Scanner scanner = new Scanner(lastCommitMaster.toFile())) {
            String currentHash = scanner.nextLine();
            if (currentHash.length() == 40 && !currentHash.equals(lastHash)) {
                // Update hash
                lastHash = currentHash;
                Preferences.userRoot().node(REPOSITORY_PREFERENCES).put("last_hash", lastHash);

                LOGGER.log(Level.INFO, "Commits checked. New commit was found. Hash: {0}", lastHash);
                executor.runJobs();
            } else {
                LOGGER.info("Commits checked. No new commits found.");
            }
        } catch (FileNotFoundException e) {
            LOGGER.severe(e.getMessage());
            System.out.printf("Unable to find branch \"%s\"%n", getBranchName());
            service.shutdown();
            System.exit(1);
        } catch (IOException e) {
            LOGGER.severe(e.getMessage());
            service.shutdown();
            System.exit(1);
        }
    }

    public void start() throws InterruptedException, RepositoryNotFoundException {
        if (isRepositoryExists()) {
            // Check for a new commit per 3 seconds
            service.scheduleWithFixedDelay(this::checkRepositoryUpdate, 0, 3, TimeUnit.SECONDS);
            Thread.currentThread().join();
        } else {
            throw new RepositoryNotFoundException("Unable to find git repository by path: %s".formatted(repositoryPath));
        }
    }

    public void setLastHash(String lastHash) {
        if (Objects.nonNull(lastHash) && lastHash.length() == 40) {
            this.lastHash = lastHash;
        }
    }

    public void setExecutor(JobsExecutor executor) {
        this.executor = executor;
    }
}

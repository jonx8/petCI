package ru.etu.petci.observers;

import ru.etu.petci.configuration.Configurator;
import ru.etu.petci.jobs.JobsExecutor;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Objects;
import java.util.Scanner;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;


public class RepositoryObserver {

    private String lastHash;         // Hash of last commit
    private JobsExecutor executor = new JobsExecutor(Collections.emptyList());
    private final String branchName;       // Name of the observed branch
    private final ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
    private static final Logger LOGGER = Logger.getLogger(RepositoryObserver.class.getName());

    static {
        LOGGER.setLevel(Level.INFO);
    }

    public static RepositoryObserver of(String branchName, String lastHash) {
        Objects.requireNonNull(branchName);
        Objects.requireNonNull(lastHash);
        return new RepositoryObserver(branchName, lastHash);
    }

    private RepositoryObserver(String branchName, String lastHash) {
        this.branchName = branchName;
        this.lastHash = lastHash;
    }

    public boolean isRepositoryExists() throws InterruptedException {
        Process gitProcess = null;
        int exitStatus;
        try {
            gitProcess = Runtime.getRuntime().exec("git status");
            gitProcess.waitFor();
            exitStatus = gitProcess.exitValue();
        } catch (IOException e) {
            LOGGER.severe(e.getMessage());
            return false;
        } finally {
            if (gitProcess != null) {
                gitProcess.destroy();
            }
        }
        return exitStatus == 0;
    }

    public String getLastHash() {
        return lastHash;
    }

    public String getBranchName() {
        return branchName;
    }

    private void checkRepositoryUpdate() {
        Path lastCommit = Path.of(".git/refs/heads/" + branchName);
        try (Scanner scanner = new Scanner(lastCommit.toFile())) {
            String newHash = scanner.nextLine();
            if (newHash.length() == 40 && !newHash.equals(lastHash)) {
                LOGGER.log(Level.INFO, "Commits checked. New commit was found. Hash: {0}", newHash);
                if (executor.runJobs()) {
                    lastHash = newHash;
                    Configurator.saveRepositoryConfig(branchName, lastHash);
                } else {
                    LOGGER.info("Running jobs failed. New commit has been rejected");
                    rejectCommit();
                }


            } else {
                LOGGER.fine("Commits checked. No new commits found.");
            }
        } catch (IOException e) {
            LOGGER.severe(e.getMessage());
            service.shutdown();
            System.exit(1);
        }
    }

    private void rejectCommit() throws IOException {
        if (lastHash.length() != 40) {
            LOGGER.severe("No information about last commit. Impossible to roll back");
            service.shutdown();
        }
        Process gitProcess = null;
        try {
            gitProcess = Runtime.getRuntime().exec("git reset --hard %s".formatted(lastHash));
            if (gitProcess.waitFor() != 0) {
                LOGGER.warning("Error while rejecting");
            }
        } catch (IOException e) {
            throw new IOException(e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            if (gitProcess != null) {
                gitProcess.destroy();
            }
        }
    }

    public void start() throws InterruptedException {
        if (isRepositoryExists()) {
            // Check for a new commit per 3 seconds
            service.scheduleWithFixedDelay(this::checkRepositoryUpdate, 0, 3, TimeUnit.SECONDS);
            Thread.currentThread().join();
        }
    }

    public void setExecutor(JobsExecutor executor) {
        this.executor = executor;
    }
}

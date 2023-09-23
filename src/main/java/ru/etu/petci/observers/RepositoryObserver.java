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
        int exitStatus = -1;
        try {
            gitProcess = Runtime.getRuntime().exec("git status");
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

    public String getLastHash() {
        return lastHash;
    }

    public String getBranchName() {
        return branchName;
    }

    private void checkRepositoryUpdate() {
        Path lastCommit = Path.of(".git/refs/heads/" + branchName);
        try (Scanner scanner = new Scanner(lastCommit.toFile())) {
            String currentHash = scanner.nextLine();
            if (currentHash.length() == 40 && !currentHash.equals(lastHash)) {
                lastHash = currentHash;
                Configurator.saveRepositoryConfig(branchName, lastHash);
                LOGGER.log(Level.INFO, "Commits checked. New commit was found. Hash: {0}", lastHash);
                executor.runJobs();
            } else {
                LOGGER.fine("Commits checked. No new commits found.");
            }
        } catch (IOException e) {
            LOGGER.severe(e.getMessage() + "Unable to find branch \"%s\"".formatted(getBranchName()));
            service.shutdown();
            System.exit(1);
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

package ru.etu.petci.configuration;


import ru.etu.petci.jobs.Job;
import ru.etu.petci.observers.RepositoryObserver;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

public class Configurator {

    public static final String JOBS_PREFERENCES = "petCI/jobs";
    public static final String REPOSITORY_PREFERENCES = "petCI/repositorySettings";

    public RepositoryObserver readRepositoryConfig() {
        var observer = new RepositoryObserver();
        var repoPreferences = Preferences.userRoot().node(REPOSITORY_PREFERENCES);
        observer.setRepositoryPath(repoPreferences.get("repository_path", "."));
        observer.setBranchName(repoPreferences.get("branch_name", "master"));
        observer.setLastHash(repoPreferences.get("last_hash", ""));
        return observer;
    }

    public void saveRepositoryConfig(String repoPath, String branchName, String lastHash) {
        Preferences repoPreferences = Preferences.userRoot().node(REPOSITORY_PREFERENCES);
        repoPreferences.put("repository_path", Path.of(repoPath).toAbsolutePath().normalize().toString());
        repoPreferences.put("branch_name", branchName);
        repoPreferences.put("last_hash", lastHash.length() == 40 ? lastHash : "null");
    }

    public void saveRepositoryConfig(String repoPath, String branchName) {
        saveRepositoryConfig(repoPath, branchName, "");
    }


    public List<Job> readJobsConfig() throws BackingStoreException {
        List<Job> jobsList = new ArrayList<>();
        var jobsPreferences = Preferences.userRoot().node(JOBS_PREFERENCES);
        for (String jobName : jobsPreferences.keys()) {
            Path scriptPath = Path.of(jobsPreferences.get(jobName, null));
            jobsList.add(new Job(scriptPath, jobName));
        }
        return jobsList;
    }


    public void saveJobsConfig(String jobName, String scriptPath) {
        var jobsPreferences = Preferences.userRoot().node(JOBS_PREFERENCES);

        if (jobsPreferences.get(jobName, null) == null) {
            jobsPreferences.put(jobName, Path.of(scriptPath).toAbsolutePath().normalize().toString());
        } else {
            System.out.printf("Job with name \"%s\" has already existed%n", jobName);
        }
    }
}



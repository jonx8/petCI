package ru.etu.petci.jobs;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.util.Objects;

public class Job {
    private boolean isActive = true;
    private String name;
    private final Path scriptFile;

    public Job(Path scriptFile, String name, boolean isActive) {
        this.scriptFile = scriptFile.toAbsolutePath().normalize();
        this.name = name;
        this.isActive = isActive;
    }

    public Job(Path scriptFile, String name) {
        this.scriptFile = scriptFile;
        this.name = name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public Path getScriptFile() {
        return scriptFile;
    }


    public void setActive(boolean active) {
        isActive = active;
    }

    public boolean isActive() {
        return isActive;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Job job)) return false;
        return Objects.equals(name, job.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }


    public int execute() throws IOException {
        Process scriptProcess = Runtime.getRuntime().exec(scriptFile.toString());
        try {
            var input = new BufferedReader(new InputStreamReader(scriptProcess.getInputStream()));
            String line;
            while ((line = input.readLine()) != null) {
                System.out.println(line);
            }
            scriptProcess.waitFor();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return scriptProcess.exitValue();
    }
}

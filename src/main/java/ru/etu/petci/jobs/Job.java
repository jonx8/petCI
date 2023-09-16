package ru.etu.petci.jobs;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import static ru.etu.petci.configuration.Configurator.JOBS_DIR;

public record Job(String name, String scriptName, boolean isActive) {

    public int execute() throws IOException {
        Process scriptProcess = Runtime.getRuntime().exec(JOBS_DIR + scriptName);
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

package com.org.maven.runner;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;

@Service
public class MavenExecutorService {

    @Value("${git.clone.dir}")
    private String cloneDir;

    @Value("${maven.home}")
    private String mavenHome;  // Specify path to Maven binary (e.g., `C:/path/to/maven/bin/mvn`)

    // Method to run Maven commands without global installation
    public String runMavenCommand(String command) {
        ProcessBuilder processBuilder = new ProcessBuilder();
        processBuilder.directory(new File(cloneDir));  // Set the working directory to the cloned repo

        // Specify full path to the Maven executable
        if (mavenHome != null && !mavenHome.isEmpty()) {
            processBuilder.command(mavenHome + "/bin/mvn", command);  // Use the full path to mvn
        } else {
            processBuilder.command("mvn", command);  // Use system's mvn if it's globally available
        }

        try {
            Process process = processBuilder.start();
            int exitCode = process.waitFor();
            if (exitCode == 0) {
                return "Maven command executed successfully.";
            } else {
                return "Maven command failed with exit code " + exitCode;
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return "Error executing Maven command: " + e.getMessage();
        }
    }
}

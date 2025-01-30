package com.org.maven.runner;


import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/maven")
@Slf4j
public class MavenController {

    @PostMapping("/buildWithEnv")
    public String buildProject(@RequestParam String projectPath, @RequestParam String mavenGoals) {
        return runMavenCommand(projectPath, mavenGoals);
    }


    @PostMapping("/build")
    public String buildProject(@RequestParam String projectPath) {
        return runMavenbuildProjectCommand(projectPath, "clean install");
    }

    private String runMavenbuildProjectCommand(String projectPath, String goals) {
        try {
            File projectDir = new File(projectPath);

            // Validate project path and pom.xml existence
            if (!projectDir.exists() || !new File(projectDir, "pom.xml").exists()) {
                return "Invalid project path or pom.xml not found!";
            }

            // Detect OS and choose the correct Maven Wrapper
            boolean isWindows = System.getProperty("os.name").toLowerCase().contains("win");
            String mvnWrapper = isWindows ? "mvnw.cmd" : "./mvnw";
            File mvnWrapperFile = new File(projectDir, mvnWrapper);

            // Check if mvnw exists, if not return an error
            if (!mvnWrapperFile.exists()) {
                return "Maven Wrapper (mvnw) not found in the cloned repository!";
            }

            // Use absolute path for mvnw
            String mvnCommand = mvnWrapperFile.getAbsolutePath();

            // Split goals into individual components for both Windows and Unix-based systems
            List<String> command = new ArrayList<>();
            if (isWindows) {
                command.add("cmd.exe");
                command.add("/c");
                command.add(mvnCommand);
            } else {
                command.add("bash");
                command.add("-c");
                command.add(mvnCommand);
            }

            // Split the goals by space and add each goal separately to the command
            String[] goalArray = goals.split(" ");
            for (String goal : goalArray) {
                command.add(goal);
            }

            // Prepare the ProcessBuilder
            ProcessBuilder builder = new ProcessBuilder(command);
            builder.directory(projectDir);

            // Set environment variables, ensuring JAVA_HOME is correctly set
            Map<String, String> env = builder.environment();

            // Try to fetch JAVA_HOME from system environment or use default value
            String javaHome = System.getenv("JAVA_HOME");
            if (javaHome == null || javaHome.isEmpty()) {
                javaHome = "C:\\Program Files\\Microsoft\\jdk-17.0.12.7-hotspot"; // Fallback path
            }

            // Validate the JAVA_HOME path if it's a valid directory
            File javaHomeDir = new File(javaHome);
            if (!javaHomeDir.exists() || !javaHomeDir.isDirectory()) {
                return "Invalid JAVA_HOME path!";
            }

            env.put("JAVA_HOME", javaHome);

            // If PATH is available, set it
            String path = System.getenv("PATH");
            if (path != null && !path.isEmpty()) {
                env.put("PATH", path);
            }

            // Redirect output and error streams
            builder.redirectErrorStream(true);
            Process process = builder.start();

            // Capture logs
            StringBuilder output = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    output.append(line).append("\n");
                }
            }

            // Wait for process to finish and capture exit code
            int exitCode = process.waitFor();
            return exitCode == 0 ? "Maven build successful!\n" + output : "Maven build failed!\n" + output;

        } catch (IOException e) {
            e.printStackTrace();
            return "Error executing Maven (IOException): " + e.getMessage();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); // Reset interrupted flag
            return "Error executing Maven (InterruptedException): " + e.getMessage();
        } catch (Exception e) {
            return "Error executing Maven: " + e.getMessage();
        }
    }

    private String runMavenCommand(String projectPath, String goals) {
        try {
            File projectDir = new File(projectPath);

            // Validate project path and pom.xml existence
            if (!projectDir.exists() || !new File(projectDir, "pom.xml").exists()) {
                return "Invalid project path or pom.xml not found!";
            }

            // Detect OS and choose the correct Maven Wrapper
            boolean isWindows = System.getProperty("os.name").toLowerCase().contains("win");
            String mvnWrapper = isWindows ? "mvnw.cmd" : "./mvnw";
            File mvnWrapperFile = new File(projectDir, mvnWrapper);

            // Check if Maven wrapper exists
            if (!mvnWrapperFile.exists()) {
                return "Maven Wrapper (mvnw) not found in the cloned repository!";
            }

            // Build the command
            List<String> command = new ArrayList<>();
            if (isWindows) {
                command.add("cmd.exe");
                command.add("/c");
                command.add(mvnWrapperFile.getAbsolutePath());
            } else {
                command.add("bash");
                command.add("-c");
                command.add(mvnWrapperFile.getAbsolutePath());
            }

            // Split goals and add them to the command list
            String[] goalArray = goals.split(" ");
            command.addAll(Arrays.asList(goalArray));

            // Prepare the ProcessBuilder
            ProcessBuilder builder = new ProcessBuilder(command);
            builder.directory(projectDir);

            // Set JAVA_HOME
            Map<String, String> env = builder.environment();
            String javaHome = System.getenv("JAVA_HOME");
            if (javaHome == null || javaHome.isEmpty()) {
                javaHome = "C:\\Program Files\\Microsoft\\jdk-17.0.12.7-hotspot"; // Fallback path
            }
            if (!new File(javaHome).isDirectory()) {
                return "Invalid JAVA_HOME path!";
            }
            env.put("JAVA_HOME", javaHome);

            // Set PATH
            String path = System.getenv("PATH");
            if (path != null && !path.isEmpty()) {
                env.put("PATH", path);
            }

            // Redirect output and error streams
            builder.redirectErrorStream(true);
            Process process = builder.start();

            // Capture logs
            StringBuilder output = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    output.append(line).append("\n");
                }
            }

            // Wait for process completion
            int exitCode = process.waitFor();
            return exitCode == 0 ? "Maven build successful!\n" + output : "Maven build failed!\n" + output;

        } catch (IOException | InterruptedException e) {
            Thread.currentThread().interrupt();
            return "Error executing Maven: " + e.getMessage();
        }
    }


}


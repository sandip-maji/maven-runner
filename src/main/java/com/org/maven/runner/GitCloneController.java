package com.org.maven.runner;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GitCloneController {

    @Autowired
    private GitCloneService gitCloneService;

    @Autowired
    private MavenExecutorService mavenExecutorService;

    // Endpoint to clone the repository
    @GetMapping("/clone-repo")
    public String cloneRepository() {
        return gitCloneService.cloneRepository();
    }

    // Endpoint to execute Maven build after cloning
    @GetMapping("/run-maven-build")
    public String runMavenBuild() {
        return mavenExecutorService.runMavenCommand("clean install");  // Or "spring-boot:run" to run the application
    }
}


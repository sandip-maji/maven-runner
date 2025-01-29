package com.org.maven.runner;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CloneController {

    @Autowired
    private CloneGitService gitCloneService;

    @Autowired
    private CloneBitbucketService CloneBitbucketService;

    @Autowired
    private MavenExecutorService mavenExecutorService;

    // Endpoint to clone the repository
    @GetMapping("/clone-git")
    public String cloneGitRepository() {
        return gitCloneService.cloneRepository();
    }

    @GetMapping("/clone-bitbucket")
    public String cloneRepository() {
        return CloneBitbucketService.cloneRepository();
    }

    // Endpoint to execute Maven build after cloning
    @GetMapping("/run-maven-build")
    public String runMavenBuild() {
        return mavenExecutorService.runMavenCommand("clean install");  // Or "spring-boot:run" to run the application
    }
}


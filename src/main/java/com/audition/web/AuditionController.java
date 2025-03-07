package com.audition.web;

import com.audition.model.AuditionPost;
import com.audition.service.AuditionService;
import java.util.List;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuditionController {

    private final AuditionService auditionService;

    public AuditionController(AuditionService auditionService) {
        this.auditionService = auditionService;
    }

    @GetMapping(value = "/posts", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<AuditionPost> getPosts(@RequestParam(value = "filter", required = false) String filter) {
        return auditionService.getPosts(filter);
    }

    @GetMapping(value = "/posts/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public AuditionPost getPostById(@PathVariable("id") final String postId) {
        final AuditionPost auditionPosts = auditionService.getPostById(postId);

        // TODO Add input validation

        return auditionPosts;
    }

    // TODO Add additional methods to return comments for each post. Hint: Check https://jsonplaceholder.typicode.com/

}

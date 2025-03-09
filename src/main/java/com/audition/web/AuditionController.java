package com.audition.web;

import com.audition.common.exception.SystemException;
import com.audition.model.AuditionPost;
import com.audition.service.AuditionService;
import java.util.List;
import org.springframework.http.HttpStatus;
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
    public AuditionPost getPostById(@PathVariable("id") final int postId) {
        if (postId < 1) {
            throw new SystemException("Invalid post ID " + postId, "Bad Request",
                HttpStatus.BAD_REQUEST.value());
        }

        return auditionService.getPostById(postId);
    }

    @GetMapping(value = "/posts/{id}/comments", produces = MediaType.APPLICATION_JSON_VALUE)
    public AuditionPost getPostWithComments(@PathVariable("id") final int postId) {
        if (postId < 1) {
            throw new SystemException("Invalid post ID " + postId, "Bad Request",
                HttpStatus.BAD_REQUEST.value());
        }

        return auditionService.getPostWithComments(postId);
    }

    // TODO Add additional methods to return comments for each post. Hint: Check https://jsonplaceholder.typicode.com/

}

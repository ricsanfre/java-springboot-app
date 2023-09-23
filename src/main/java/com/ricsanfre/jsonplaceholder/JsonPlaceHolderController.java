package com.ricsanfre.jsonplaceholder;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class JsonPlaceHolderController {

    private JsonPlaceHolderClient jsonPlaceHolderClient;

    public JsonPlaceHolderController(JsonPlaceHolderClient jsonPlaceHolderClient) {
        this.jsonPlaceHolderClient = jsonPlaceHolderClient;
    }

    @GetMapping("api/v1/placeholder/posts")
    List<Post> getPosts() {
        return jsonPlaceHolderClient.getPosts();
    }

    @GetMapping("api/v1/placeholder/posts/{id}")
    Post getPost(@PathVariable("id") Integer id) {
        return jsonPlaceHolderClient.getPost(id);
    }
}

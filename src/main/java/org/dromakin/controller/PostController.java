package org.dromakin.controller;

import com.google.gson.Gson;
import lombok.AllArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dromakin.exception.NotFoundException;
import org.dromakin.model.Post;
import org.dromakin.repository.PostRepository;
import org.dromakin.service.PostService;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Reader;

@AllArgsConstructor
public class PostController {
    private static final Logger logger = LogManager.getLogger(PostRepository.class);
    public static final String APPLICATION_JSON = "application/json";
    private final PostService service;

    public void all(HttpServletResponse response) {
        try {
            response.setContentType(APPLICATION_JSON);
            final var data = service.all();
            final var gson = new Gson();
            response.getWriter().print(gson.toJson(data));
        } catch (IOException e) {
            logger.error(e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    public void getById(long id, HttpServletResponse response) {
        try {
            response.setContentType(APPLICATION_JSON);
            final var data = service.getById(id);
            if (data == null) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            } else {
                final var gson = new Gson();
                response.getWriter().print(gson.toJson(data));
            }
        } catch (IOException e) {
            logger.error(e);
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    public void save(Reader body, HttpServletResponse response) {
        Post post = null;
        try {
            response.setContentType(APPLICATION_JSON);
            final var gson = new Gson();
            post = gson.fromJson(body, Post.class);
            final var data = service.save(post);
            response.getWriter().print(gson.toJson(data));
        } catch (IOException e) {
            logger.error(e);
            if (post.getId() == 0) {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                if (e.getCause() != null) {
                    e.getCause().printStackTrace();
                }
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            }
        }
    }

    public void removeById(long id, HttpServletResponse response) {
        try {
            service.removeById(id);
            response.setStatus(HttpServletResponse.SC_OK);
        } catch (NotFoundException e) {
            logger.error(e);
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        }
    }
}

package ru.netology.servlet;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import ru.netology.config.JavaConfig;
import ru.netology.controller.PostController;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class MainServlet extends HttpServlet {
    private PostController controller;
    public static final String PATH = "/api/posts";

    @Override
    public void init() {
        final var context = new AnnotationConfigApplicationContext(JavaConfig.class);
        controller = (PostController) context.getBean("postController");
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) {
        // если деплоились в root context, то достаточно этого
        try {
            final var path = req.getRequestURI();
            final var method = req.getMethod();
            if (path.equals(PATH)) {
                if (method.equals("GET")) {
                    controller.all(resp);
                }
                if (method.equals("POST")) {
                    controller.save(req.getReader(), resp);
                }
                return;
            }
            if (path.matches(PATH + "/\\d+")) {
                final var id = Long.parseLong(path.substring(path.lastIndexOf("/")));
                if (method.equals("GET")) {
                    controller.getById(id, resp);
                }
                if (method.equals("DELETE")) {
                    controller.removeById(id, resp);
                }
                return;
            }
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
        } catch (Exception e) {
            e.printStackTrace();
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}


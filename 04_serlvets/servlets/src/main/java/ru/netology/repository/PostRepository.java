package ru.netology.repository;

import ru.netology.model.Post;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicLong;

public class PostRepository {

    private final List<Post> list;
    private static final AtomicLong maxId = new AtomicLong(0);

    public PostRepository() {
        list = new CopyOnWriteArrayList<>();
    }

    public List<Post> all() {
        return list;
    }

    public Optional<Post> getById(long id) {
        return list.stream().filter(post -> post.getId() == id).findFirst();
    }

    public Post save(Post post) {
        if (post.getId() == 0) {
            list.add(new Post(maxId.incrementAndGet(), post.getContent()));
        } else {
            Optional<Post> existedPost = getById(post.getId());
            if (existedPost.isPresent()) {
                list.set(list.indexOf(existedPost.get()), post);
            } else {
                if (post.getId() > maxId.get()) {
                    maxId.getAndUpdate(n -> post.getId());
                }
                list.add(post);
            }
        }
        return post;
    }

    public void removeById(long id) {
        Optional<Post> post = getById(id);
        post.ifPresent(list::remove);
    }
}

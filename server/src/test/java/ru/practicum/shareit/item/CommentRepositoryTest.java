package ru.practicum.shareit.item;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.item.comment.CommentRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsNull.notNullValue;

@DataJpaTest
public class CommentRepositoryTest {
    @Autowired
    private TestEntityManager testEntityManager;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private CommentRepository commentRepository;
    private Item item;

    @BeforeEach
    public void addEnvironsForAll() {
        User user = new User();
        user.setName("userNameTest");
        user.setEmail("userEmailTest@mail.ru");
        userRepository.save(user);
        item = new Item();
        item.setName("itemNameTest");
        item.setDescription("itemDescriptionTest");
        item.setIsAvailable(Boolean.TRUE);
        item.setOwner(user);
        itemRepository.save(item);
        Comment comment = new Comment();
        comment.setText("commentTextTest");
        comment.setAuthor(user);
        comment.setCreated(LocalDateTime.now());
        comment.setItem(item);
        commentRepository.save(comment);
    }

    @Test
    void contextLoadsTest() {
        Assertions.assertNotNull(testEntityManager);
    }

    @Test
    void findAllByItemIdTest() {
        List<Comment> result = commentRepository.findAllByItemId(item.getId());

        assertThat(result, notNullValue());
        assertThat(result.size(), equalTo(1));
    }
}

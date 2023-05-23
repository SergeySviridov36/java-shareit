package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import ru.practicum.shareit.item.comment.CommentDto;

import java.io.IOException;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@JsonTest
public class CommentDtoTest {
    @Autowired
    private JacksonTester<CommentDto> json;

    @Test
    void CommentDtoJsonTest() throws IOException {
        String jsonContent = "{\"text\":\"itemDescription\"}";

        CommentDto result = this.json.parse(jsonContent).getObject();

        assertThat(result.getText()).isEqualTo("itemDescription");
    }
}

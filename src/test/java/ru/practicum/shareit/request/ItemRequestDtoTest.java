package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

import java.io.IOException;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@JsonTest
public class ItemRequestDtoTest {
    @Autowired
    private JacksonTester<ItemRequestDto> json;

    @Test
    void itemRequestJsonDtoTest() throws IOException {
        String jsonContent = "{\"description\":\"descriptionTextTest\"}";
        ItemRequestDto result = this.json.parse(jsonContent).getObject();

        assertThat(result.getDescription()).isEqualTo("descriptionTextTest");
    }
}

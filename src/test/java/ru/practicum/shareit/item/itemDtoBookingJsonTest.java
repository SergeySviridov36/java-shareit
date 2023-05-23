package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

import java.io.IOException;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@JsonTest
public class itemDtoBookingJsonTest {
    @Autowired
    private JacksonTester<ItemDto> json;

    @Test
    void testJsonItemDto() throws IOException {
        String jsonContent = "{\"name\":\"itemNameTest\", \"description\":\"setDescriptionTest\", \"available\":\"true\"}";

        ItemDto result = this.json.parse(jsonContent).getObject();

        assertThat(result.getName()).isEqualTo("itemNameTest");
        assertThat(result.getDescription()).isEqualTo("setDescriptionTest");
        assertThat(result.getAvailable()).isEqualTo(true);
    }
}

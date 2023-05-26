package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

import java.io.IOException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@JsonTest
public class UserDtoTest {
    @Autowired
    private JacksonTester<UserDto> json;

    @Test
    void jsonUserJsonDtoTest() throws IOException {
        String jsonContent = "{\"name\":\"nameTest\", \"email\":\"emailTest@mail.ru\"}";
        UserDto result = this.json.parse(jsonContent).getObject();

        assertThat(result.getName(), equalTo("nameTest"));
        assertThat(result.getEmail(), equalTo("emailTest@mail.ru"));
    }
}

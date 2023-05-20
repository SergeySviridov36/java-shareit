package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jooq.JooqTest;
import org.springframework.boot.test.json.JacksonTester;

import java.io.IOException;
import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@JooqTest
public class BookingRequestDtoClassTest {

@Autowired
private JacksonTester<BookingRequestDto> jacksonTester;
    @Test
    void bookingRequestDtoTest() throws IOException {
        var jsonContent = "{\"itemId\":\"1\", \"start\":\"2024-05-22T12:00:01\", \"end\":\"2024-05-23T13:00:01\"}";

        var result = this.jacksonTester.parse(jsonContent).getObject();

        assertThat(result.getItemId()).isEqualTo(1L);
        assertThat(result.getStart()).isEqualTo(LocalDateTime.of(2024, 5, 22, 12, 0, 1));
        assertThat(result.getEnd()).isEqualTo(LocalDateTime.of(2024, 5, 23, 13, 0, 1));
    }
}

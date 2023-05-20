package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.springframework.data.domain.Sort;
import ru.practicum.shareit.exception.NotFoundEntityExeption;

@WebMvcTest(BookingController.class)
public class BookingControllerTest {
    @Autowired
    ObjectMapper mapper;

    @MockBean
    BookingService bookingService;

    @Autowired
    MockMvc mvc;

    private static final String USER_ID = "X-Sharer-User-Id";

    private final BookingRequestDto bookingRequestDto = new BookingRequestDto(
            1L,
            LocalDateTime.of(2024, 5, 20, 12, 0, 0),
            LocalDateTime.of(2024, 5, 21, 12, 0, 0));

    private final BookingDto bookingDto = new BookingDto(
            1L,
            LocalDateTime.of(2024, 5, 20, 12, 0, 0),
            LocalDateTime.of(2024, 5, 21, 12, 0, 0),
            new BookingDto.Item(1L, "Весы"),
            new BookingDto.Booker(1L, "Тимон"),
            Status.WAITING
    );


    @Test
    void createTest() throws Exception {

        when(bookingService.create(any(), anyLong()))
                .thenReturn(bookingDto);

        mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(bookingRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header(USER_ID,1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(bookingDto.getId()))
                .andExpect(jsonPath("$.item.name").value(bookingDto.getItem().getName()))
                .andExpect(jsonPath("$.booker.id").value(bookingDto.getBooker().getId()))
                .andExpect(jsonPath("$.start").value(bookingDto.getStart().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)))
                .andExpect(jsonPath("$.end").value(bookingDto.getEnd().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)))
                .andExpect(jsonPath("$.status").value(bookingDto.getStatus().name()));
        verify(bookingService, times(1)).create(any(), anyLong());
    }

    @Test
    void updateTest() throws Exception {
        var userId = 1L;
        boolean approved = Boolean.TRUE;
        bookingDto.setStatus(Status.APPROVED);

        when(bookingService.update(bookingDto.getId(), userId, approved))
                .thenReturn(bookingDto);

        mvc.perform(patch("/bookings/" + bookingDto.getId())
                        .content(mapper.writeValueAsString(bookingRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .queryParam("approved", "true")
                        .header(USER_ID, userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.start").value(bookingDto.getStart().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)))
                .andExpect(jsonPath("$.end").value(bookingDto.getEnd().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)))
                .andExpect(jsonPath("$.status").value(bookingDto.getStatus().name()));
        verify(bookingService, times(1)).update(bookingDto.getId(), userId, approved);
    }

    @Test
    void findByIdTest() throws Exception {
        var userId = 1L;

        when(bookingService.findById(userId, bookingDto.getId()))
                .thenReturn(bookingDto);

        mvc.perform(get("/bookings/" + bookingDto.getId())
                        .content(mapper.writeValueAsString(bookingDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header(USER_ID, userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(bookingDto.getId()))
                .andExpect(jsonPath("$.start").value(bookingDto.getStart().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)))
                .andExpect(jsonPath("$.end").value(bookingDto.getEnd().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)))
                .andExpect(jsonPath("$.status").value(Status.WAITING.name()));
        verify(bookingService, times(1)).findById(userId, bookingDto.getId());
    }

    @Test
    void findAllByBookerTest() throws Exception {
        var state = "FUTURE";
        var userId = 1L;
        final var sort = Sort.by("start").descending();
        final var page = PageRequest.of(0, 10, sort);

        when(bookingService.findAllByBooker(userId, state, page))
                .thenReturn(Collections.emptyList());

        mvc.perform(get("/bookings?from=0&size=10")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header(USER_ID, userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .param("state", state))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
        verify(bookingService, times(1)).findAllByBooker(userId, state, page);
    }

    @Test
    void findAllByOwnerTest() throws Exception {
        var state = "ALL";
        var userId = 1L;
        final var sort = Sort.by("start").descending();
        final var page = PageRequest.of(0, 10, sort);

        when(bookingService.findAllByOwner(userId, state, page))
                .thenReturn(Collections.emptyList());

        mvc.perform(get("/bookings/owner?from=0&size=10")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header(USER_ID, userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .param("state", state))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
        verify(bookingService, times(1)).findAllByOwner(userId, state, page);
    }

    @Test
    void shouldErrorStatusTest() throws Exception {
        var state = "NEVER";
        var userId = 1L;
        final var sort = Sort.by("start").descending();
        var page = PageRequest.of(0, 10, sort);

        when(bookingService.findAllByBooker(userId, state, page))
                .thenThrow(new NotFoundEntityExeption("Unknown state: " + state));

        mvc.perform(get("/bookings?from=0&size=10")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header(USER_ID, userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.ALL)
                        .param("state", state))
                .andExpect(status().is4xxClientError())
                .andExpect(content().json("{\"error\":\"Unknown state: " + state + "\"}"));
        verify(bookingService, times(1)).findAllByBooker(userId, state, page);
    }


}

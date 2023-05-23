package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

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
    MockMvc mockMvc;

    private static final String X_SHARER_USER_ID = "X-Sharer-User-Id";

    private static final Long USER_ID = 1L;

    private final Sort sort = Sort.by("start").descending();
    private final PageRequest page = PageRequest.of(0, 10, sort);

    private final BookingRequestDto bookingRequestDto = new BookingRequestDto(
            1L,
            LocalDateTime.of(2024, 5, 20, 12, 0, 0),
            LocalDateTime.of(2024, 5, 21, 12, 0, 0));

    private final BookingDto bookingDto = new BookingDto(
            1L,
            LocalDateTime.of(2024, 5, 20, 12, 0, 0),
            LocalDateTime.of(2024, 5, 21, 12, 0, 0),
            new BookingDto.Item(1L, "itemTest"),
            new BookingDto.Booker(1L, "bookerTest"),
            Status.WAITING
    );


    @Test
    void createTest() throws Exception {
        when(bookingService.create(any(), anyLong()))
                .thenReturn(bookingDto);

        mockMvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(bookingRequestDto))
                        .header(X_SHARER_USER_ID, 1)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(bookingDto.getId()))
                .andExpect(jsonPath("$.item.name").value(bookingDto.getItem().getName()))
                .andExpect(jsonPath("$.booker.id").value(bookingDto.getBooker().getId()));
        verify(bookingService, times(1)).create(any(), anyLong());
    }

    @Test
    void updateTest() throws Exception {
        bookingDto.setStatus(Status.APPROVED);
        boolean approved = Boolean.TRUE;
        when(bookingService.update(bookingDto.getId(), USER_ID, approved))
                .thenReturn(bookingDto);

        mockMvc.perform(patch("/bookings/{bookingId}", bookingDto.getId())
                        .content(mapper.writeValueAsString(bookingRequestDto))
                        .queryParam("approved", "true")
                        .header(X_SHARER_USER_ID, USER_ID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.start").value(bookingDto.getStart().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)))
                .andExpect(jsonPath("$.end").value(bookingDto.getEnd().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)))
                .andExpect(jsonPath("$.status").value(bookingDto.getStatus().name()));
        verify(bookingService, times(1)).update(bookingDto.getId(), USER_ID, approved);
    }

    @Test
    void findByIdTest() throws Exception {
        when(bookingService.findById(USER_ID, bookingDto.getId()))
                .thenReturn(bookingDto);

        mockMvc.perform(get("/bookings/{bookingId}", bookingDto.getId())
                        .content(mapper.writeValueAsString(bookingDto))
                        .header(X_SHARER_USER_ID, USER_ID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(bookingDto.getId()))
                .andExpect(jsonPath("$.status").value(Status.WAITING.name()));
        verify(bookingService, times(1)).findById(USER_ID, bookingDto.getId());
    }

    @Test
    void findAllByBookerTest() throws Exception {
        String state = "FUTURE";
        when(bookingService.findAllByBooker(USER_ID, state, page))
                .thenReturn(Collections.emptyList());

        mockMvc.perform(get("/bookings?from=0&size=10")
                        .header(X_SHARER_USER_ID, USER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("state", state))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
        verify(bookingService, times(1)).findAllByBooker(USER_ID, state, page);
    }

    @Test
    void findAllByOwnerTest() throws Exception {
        String state = "ALL";
        when(bookingService.findAllByOwner(USER_ID, state, page))
                .thenReturn(Collections.emptyList());

        mockMvc.perform(get("/bookings/owner?from=0&size=10")
                        .header(X_SHARER_USER_ID, USER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("state", state))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
        verify(bookingService, times(1)).findAllByOwner(USER_ID, state, page);
    }

    @Test
    void shouldErrorStatusTest() throws Exception {
        String state = "NEVER";
        when(bookingService.findAllByBooker(USER_ID, state, page))
                .thenThrow(new NotFoundEntityExeption("Unknown state: " + state));

        mockMvc.perform(get("/bookings?from=0&size=10")
                        .header(X_SHARER_USER_ID, USER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("state", state))
                .andExpect(status().is4xxClientError())
                .andExpect(content().json("{\"error\":\"Unknown state: " + state + "\"}"));
        verify(bookingService, times(1)).findAllByBooker(USER_ID, state, page);
    }
}

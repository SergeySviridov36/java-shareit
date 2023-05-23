package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ItemRequestController.class)
public class ItemRequestControllerTest {
    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ItemRequestServiceImpl itemRequestService;

    @Autowired
    private MockMvc mockMvc;
    private final Long userId = 1L;

    long requestId = 1L;

    private static final String USER_ID = "X-Sharer-User-Id";

    private final ItemRequestDto itemRequestDto = new ItemRequestDto(
            null,
            null,
            "itemNameTest",
            null
    );
    private final ItemRequestDto itemRequestDtoResponse = new ItemRequestDto(
            1L,
            Collections.emptyList(),
            "itemNameTest",
            LocalDateTime.now()

    );

    @Test
    void createRequestTest() throws Exception {
        when(itemRequestService.create(any(), anyLong()))
                .thenReturn(itemRequestDtoResponse);

        mockMvc.perform(post("/requests")
                        .content(objectMapper.writeValueAsString(itemRequestDto))
                        .header(USER_ID, userId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemRequestDtoResponse.getId()))
                .andExpect(jsonPath("$.description").value(itemRequestDtoResponse.getDescription()));
        verify(itemRequestService, times(1)).create(any(), anyLong());
    }

    @Test
    void findAllRequestByOwner() throws Exception {
        when(itemRequestService.findAllRequestByOwner(userId))
                .thenReturn(Collections.emptyList());

        mockMvc.perform(get("/requests")
                        .header(USER_ID, userId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
        verify(itemRequestService, times(1)).findAllRequestByOwner(userId);
    }

    @Test
    void findAllRequestTest() throws Exception {
        Sort sort = Sort.by("created").descending();
        PageRequest page = PageRequest.of(0, 10, sort);
        when(itemRequestService.findAllRequest(userId, page))
                .thenReturn(Collections.emptyList());

        mockMvc.perform(get("/requests/all?from=0&size=10")
                        .header(USER_ID, userId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
        verify(itemRequestService, times(1)).findAllRequest(userId, page);
    }

    @Test
    void findByRequestIdTest() throws Exception {
        when(itemRequestService.findByRequestId(userId, requestId))
                .thenReturn(itemRequestDtoResponse);

        mockMvc.perform(get("/requests/{requestId}", requestId)
                        .header(USER_ID, userId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemRequestDtoResponse.getId()))
                .andExpect(jsonPath("$.description").value(itemRequestDtoResponse.getDescription()));
        verify(itemRequestService, times(1)).findByRequestId(userId, requestId);
    }
}

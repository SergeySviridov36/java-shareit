package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.comment.CommentDto;
import ru.practicum.shareit.item.comment.CommentDtoResponse;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
@WebMvcTest(ItemController.class)
public class ItemControllerTest {
    @Autowired
    ObjectMapper objectMapper;
    @MockBean
    private ItemService itemService;
    @Autowired
    MockMvc mockMvc;

    private static final String X_SHARER_USER_ID = "X-Sharer-User-Id";

    private final PageRequest page = PageRequest.of(0, 10);

    private long userId = 1L;

    private final ItemDto itemDto = new ItemDto(
            1L,
            "itemTest",
            "descriptionItemTest",
            Boolean.TRUE,
            null,
            null
    );
    private final ItemDtoBooking itemDtoBooking = new ItemDtoBooking(
            1L,
            "itemDtoBookingTest",
            "descriptionItemDtoBookingTest",
            Boolean.TRUE,
            null,
            null,
            null,
            null,
            null
    );

    private final CommentDto commentDto = new CommentDto(
            "commentTest"
    );
    private final CommentDtoResponse commentDtoResponse = new CommentDtoResponse(
            1L,
            "commentTest",
            "userTest",
            null
    );


    @Test
    void createItemTest() throws Exception {
        when(itemService.create(any(), anyLong()))
                .thenReturn(itemDto);

        mockMvc.perform(post("/items")
                        .content(objectMapper.writeValueAsString(itemDto))
                        .header(X_SHARER_USER_ID, 1)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemDto.getId()));
        verify(itemService, times(1)).create(any(), anyLong());
    }

    @Test
    void updateItemTest() throws Exception {
        itemDto.setName("testName");
        when(itemService.update(any(), anyLong(), anyLong()))
                .thenReturn(itemDto);

        mockMvc.perform(patch("/items/{itemId}", 1)
                        .content(objectMapper.writeValueAsString(itemDto))
                        .header(X_SHARER_USER_ID, 1)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemDto.getId()))
                .andExpect(jsonPath("$.name").value(itemDto.getName()));
        verify(itemService, times(1)).update(any(), anyLong(), anyLong());
    }

    @Test
    void findItemByIdTest() throws Exception {
        when(itemService.findItemById(anyLong(), anyLong()))
                .thenReturn(itemDtoBooking);

        mockMvc.perform(get("/items/{itemId}", itemDto.getId())
                        .header(X_SHARER_USER_ID, 1)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemDtoBooking.getId()))
                .andExpect(jsonPath("$.name").value(itemDtoBooking.getName()));
        verify(itemService, times(1)).findItemById(anyLong(), anyLong());
    }

    @Test
    void findAllItemsTest() throws Exception {
        Sort sort = Sort.by(Sort.Direction.ASC, "id");
        PageRequest page = PageRequest.of(0, 10, sort);
        when(itemService.findAllItemsOwner(userId, page))
                .thenReturn(Collections.emptyList());

        mockMvc.perform(get("/items?from=0&size=10")
                        .header(X_SHARER_USER_ID, userId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
        verify(itemService, times(1)).findAllItemsOwner(userId, page);
    }

    @Test
    void searchItemsTest() throws Exception {
        String text = "testName";
        when(itemService.searchItem(userId, text, page))
                .thenReturn(Collections.emptyList());
        mockMvc.perform(get("/items/search?from=0&size=10&text=testName")
                        .header(X_SHARER_USER_ID, userId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
        verify(itemService, times(1)).searchItem(userId, text, page);
    }

    @Test
    void createCommentTest() throws Exception {
        userId = 2L;
        long itemId = 1L;
        when(itemService.createComment(userId, commentDto, itemId))
                .thenReturn(commentDtoResponse);

        mockMvc.perform(post("/items/{itemId}/comment", itemId, "/comment")
                        .content(objectMapper.writeValueAsString(commentDto))
                        .header(X_SHARER_USER_ID, userId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(commentDtoResponse.getId()));
        verify(itemService, times(1)).createComment(userId, commentDto, itemId);
    }
}

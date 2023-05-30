package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;

import java.util.Map;

import static ru.practicum.shareit.util.Constants.*;

@Service
public class ItemClient extends BaseClient {

    private static final String API_PREFIX = "/items";

    @Autowired
    public ItemClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(builder
                .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                .build()
        );
    }

    public ResponseEntity<Object> create(Long userId, ItemDto itemDto) {
        return post("", userId, itemDto);
    }

    public ResponseEntity<Object> update(ItemDto inputItemDto, long userId, Long itemId) {
        return patch("/" + itemId, userId, inputItemDto);
    }

    public ResponseEntity<Object> findItemById(Long itemId, Long owner) {
        return get("/" + itemId, owner);
    }

    public ResponseEntity<Object> findAllItems(Long owner, Integer from, Integer size) {
        Map<String, Object> parametr = Map.of(
                FROM, from,
                SIZE, size);
        return get("?from={from}&size={size}", owner, parametr);
    }

    public ResponseEntity<Object> searchItem(Long userId, String text, Integer from, Integer size) {
        Map<String, Object> parametr = Map.of(
                "text", text,
                FROM, from,
                SIZE, size);
        return get("/search?text={text}&from={from}&size={size}", userId, parametr);
    }

    public ResponseEntity<Object> createComment(Long userId, CommentDto commentDto, Long itemId) {
        return post("/" + itemId + "/comment", userId, commentDto);
    }
}



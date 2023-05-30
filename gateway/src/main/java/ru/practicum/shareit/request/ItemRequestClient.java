package ru.practicum.shareit.request;

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
public class ItemRequestClient extends BaseClient {

    private static final String API_PREFIX = "/requests";

    @Autowired
    public ItemRequestClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(builder
                .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                .build()
        );
    }

    public ResponseEntity<Object> create(Long userId, ItemRequestDto inputItemRequestDto) {
        return post("", userId, inputItemRequestDto);
    }

    public ResponseEntity<Object> findAllRequestByOwner(Long userId) {
        return get("", userId);
    }

    public ResponseEntity<Object> findAllRequest(Long userId, Integer from, Integer size) {
        Map<String, Object> parametr = Map.of(
                FROM, from,
                SIZE, size);
        return get("/all?from={from}&size={size}", userId, parametr);
    }

    public ResponseEntity<Object> findByRequestId(Long userId, Long requestId) {
        return get("/" + requestId, userId);
    }
}

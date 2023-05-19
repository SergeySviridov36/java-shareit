package ru.practicum.shareit.item;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {

    List<Item> findAllByOwnerId(Long ownerId);

    @Query(" select i from Item i " +
            "where i.isAvailable = true and" +
            " (upper(i.name) like upper(concat('%', ?1, '%')) " +
            "  or upper(i.description) like upper(concat('%', ?1, '%')))")
    List<Item> search(String text);

    List<Item> findAllByRequestIdIn(List<Long> listRequestIds);

    List<Item> findAllByRequestId(Long requestId);

    Page<Item> findAllByOwnerId(Long ownerId, PageRequest page);
}

package ru.practicum.shareit.request;

import lombok.*;
import ru.practicum.shareit.user.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@ToString
@Entity
@Table(name = "item_request")
public class ItemRequest {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "requestor_id", referencedColumnName = "id")
    private User requestor;
    @Column(name = "description")
    private String description;
    @Column(name = "created")
    private LocalDateTime created;
}

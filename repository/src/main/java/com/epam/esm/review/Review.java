package com.epam.esm.review;

import com.epam.esm.user.User;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@RequiredArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@ToString
@Entity
@Table(name = "review")
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.TABLE)
    private long id;
    @NonNull
    @Column(name = "comment")
    public String comment;
    @NonNull
    @Column(name = "rating")
    public int rating;
    @Column(name = "review_date")
    public LocalDateTime reviewDate;
    @ManyToOne(fetch = FetchType.EAGER, cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    @JoinColumn(name = "developer_id", nullable = true)
    private User developer;
    @ManyToOne(fetch = FetchType.EAGER, cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    @JoinColumn(name = "creator_id", nullable = true)
    private User creator;
}

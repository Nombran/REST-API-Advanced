package com.epam.esm.service;

import com.epam.esm.tag.Tag;
import com.epam.esm.user.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.EqualsAndHashCode;
import lombok.NonNull;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@RequiredArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@Entity
@Table(name = "service")
public class Service {
    @Id
    @GeneratedValue(strategy = GenerationType.TABLE)
    private long id;
    @NonNull
    @Column(name = "name")
    private String name;
    @NonNull
    @Column(name = "description", length = 3000)
    private String description;
    @NonNull
    @Column(name = "price")
    private BigDecimal price;
    @Column(name = "creation_date")
    private LocalDateTime creationDate;
    @Column(name="modification_date")
    private LocalDateTime modificationDate;
    @ManyToMany(cascade = {CascadeType.PERSIST,CascadeType.MERGE}, fetch = FetchType.LAZY)
    @JoinTable(name = "service_tag",
            joinColumns = {@JoinColumn(name = "service_id", nullable = false)},
            inverseJoinColumns = {@JoinColumn(name = "tag_id", nullable = false)})
    private List<Tag> tags;
    @ManyToOne(fetch = FetchType.EAGER, cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    @JoinColumn(name = "creator_id", nullable = false)
    private User creator;
    @ManyToOne(fetch = FetchType.EAGER, cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    @JoinColumn(name = "developer_id", nullable = true)
    private User developer;
    @ManyToMany(mappedBy = "desiredServices")
    private List<User> desiredDevelopers;
}

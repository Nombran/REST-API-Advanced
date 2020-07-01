package com.epam.esm.certificate.model;

import com.epam.esm.tag.model.Tag;
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
@Table(name = "certificate")
public class Certificate {
    @Id
    @GeneratedValue(strategy = GenerationType.TABLE)
    private long id;
    @NonNull
    @Column(name = "name", unique = true)
    private String name;
    @NonNull
    @Column(name = "description")
    private String description;
    @NonNull
    @Column(name = "price")
    private BigDecimal price;
    @Column(name = "creation_date")
    private LocalDateTime creationDate;
    @Column(name="modification_date")
    private LocalDateTime modificationDate;
    @NonNull
    @Column(name = "duration")
    private int duration;
    @ManyToMany(cascade = {CascadeType.PERSIST,CascadeType.MERGE}, fetch = FetchType.LAZY)
    @JoinTable(name = "certificate_tag",
            joinColumns = {@JoinColumn(name = "certificate_id", nullable = false)},
            inverseJoinColumns = {@JoinColumn(name = "tag_id", nullable = false)})
    private List<Tag> tags;
}

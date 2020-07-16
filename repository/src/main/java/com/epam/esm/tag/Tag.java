package com.epam.esm.tag;

import com.epam.esm.certificate.Certificate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.EqualsAndHashCode;
import lombok.NonNull;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@RequiredArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@Entity
@Table(name = "tag")
public class Tag {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    @NonNull
    @NotBlank
    @Column(name = "name", unique = true)
    private String name;
    @ManyToMany(mappedBy = "tags", fetch = FetchType.LAZY)
    private List<Certificate> certificates;
}

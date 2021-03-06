package com.epam.esm.tag;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import org.springframework.hateoas.RepresentationModel;

import javax.persistence.Column;
import javax.validation.constraints.Size;

@JsonIgnoreProperties(ignoreUnknown = true)
@AllArgsConstructor
@NoArgsConstructor
@RequiredArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(callSuper = false)
public class TagDto extends RepresentationModel<TagDto> {
    private long id;
    @NonNull
    @Size(min = 3,max = 50)
    @Column(name = "name", unique = true)
    private String name;
}

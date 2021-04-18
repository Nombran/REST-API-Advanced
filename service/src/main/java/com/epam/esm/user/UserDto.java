package com.epam.esm.user;

import com.epam.esm.review.ReviewDto;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import org.springframework.hateoas.RepresentationModel;

import javax.persistence.Column;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@AllArgsConstructor
@NoArgsConstructor
@RequiredArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(callSuper = false)
@ToString
public class UserDto extends RepresentationModel<UserDto> {
    private long id;
    @NonNull
    @NotBlank
    @Size(min = 2, max = 25)
    private String firstName;
    @NonNull
    @NotBlank
    @Size(min = 2, max = 25)
    private String lastName;
    @NonNull
    @NotBlank
    @Size(min = 5, max = 15)
    private String login;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @NotBlank
    @Size(min = 5, max = 15)
    private String password;
    private String contacts;
    private List<String> skills;
    private String specialization;
    private String activity;
    private int salary;
    private String about;
    private int allServicesCount;
    private int processingServicesCount;
    private LocalDateTime registrationDate;
    private List<ReviewDto> reviews;
}

package com.epam.esm.user.dto;

import com.epam.esm.order.model.Order;
import com.epam.esm.user.model.Role;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@AllArgsConstructor
@NoArgsConstructor
@RequiredArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class UserDto {
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
}

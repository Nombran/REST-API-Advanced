package com.epam.esm.review;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import java.time.LocalDateTime;

@JsonIgnoreProperties(ignoreUnknown = true)
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(callSuper = false)
public class ReviewDto {
    private long id;
    public String comment;
    public int rating;
    public LocalDateTime reviewDate;
    private long developerId;
    private long creatorId;
}

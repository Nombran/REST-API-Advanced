package com.epam.esm.user;

import com.epam.esm.service.ServiceDto;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class UserServicesDto {
    private List<ServiceDto> takenServices;
    private List<ServiceDto> desiredServices;
    private List<ServiceDto> createdServices;
}

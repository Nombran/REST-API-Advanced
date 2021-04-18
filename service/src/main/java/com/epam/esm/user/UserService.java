package com.epam.esm.user;

import com.epam.esm.service.ServiceConflictException;
import com.epam.esm.service.ServiceDto;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.PagedModel;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional
public class UserService {
    private final UserDao userDao;
    private final ModelMapper modelMapper;

    @Autowired
    public UserService(UserDao userDao,
                       ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
        this.userDao = userDao;
    }

    public UserDto create(UserDto userDto) {
            Optional<User> userWithLogin = userDao.findUserByLogin(userDto.getLogin());
            if (userWithLogin.isPresent()) {
                throw new ServiceConflictException("login already exists");
            }
            userDto.setId(0);
            User user = modelMapper.map(userDto, User.class);
            user.setRegistrationDate(LocalDateTime.now());
            userDao.create(user);
            return modelMapper.map(user, UserDto.class);
    }

    public UserDto update(UserDto userDto, long id) {
        userDto.setId(id);
        if (!userDao.find(id).isPresent()) {
            throw new UserNotFoundException("User with id " + userDto.getId() + " doesn't exist");
        }
        User user = modelMapper.map(userDto, User.class);
        userDao.update(user);
        return modelMapper.map(user, UserDto.class);
    }

    public UserDto find(long id) {
        Optional<User> user = userDao.find(id);
        if (user.isPresent()) {
            return modelMapper.map(user.get(), UserDto.class);
        } else {
            throw new UserNotFoundException("User with id " + id + " doesn't exist");
        }
    }

    public PagedModel<UserDto> findUsers(int page, int perPage) {
        List<UserDto> users = userDao.findUsers(page, perPage)
                .stream()
                .map(user -> modelMapper.map(user, UserDto.class))
                .collect(Collectors.toList());
        int totalUsersCount = userDao.findAllUserCount().intValue();
        PagedModel.PageMetadata pageMetadata = new PagedModel.PageMetadata(perPage, page, totalUsersCount);
        return PagedModel.of(users, pageMetadata);
    }

    public UserServicesDto findUserServices(long userId) {
        User user = userDao.find(userId).orElseThrow(() ->
                new UserNotFoundException("user with id " + userId + "not found")
        );
        UserServicesDto userServicesDto = new UserServicesDto();
        userServicesDto.setTakenServices(user.getTakenServices()
                .stream()
                .map(service -> modelMapper.map(service, ServiceDto.class))
                .collect(Collectors.toList()));
        userServicesDto.setCreatedServices(user.getCreatedServices()
                .stream()
                .map(service -> modelMapper.map(service, ServiceDto.class))
                .collect(Collectors.toList()));
        userServicesDto.setDesiredServices(user.getDesiredServices()
                .stream()
                .map(service -> modelMapper.map(service, ServiceDto.class))
                .collect(Collectors.toList()));
        return userServicesDto;
    }
}

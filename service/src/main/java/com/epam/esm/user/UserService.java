package com.epam.esm.user;

import com.epam.esm.exception.ServiceConflictException;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.hateoas.PagedModel;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
            userDto.setId(0);
            User user = modelMapper.map(userDto, User.class);
                userDao.create(user);
                return modelMapper.map(user, UserDto.class);
    }

    public UserDto update(UserDto userDto, long id) {
        userDto.setId(id);
        if (!userDao.find(id).isPresent()) {
            throw new UserNotFoundException("User with id " + userDto.getId() + " doesn't exist");
        }
        User user = modelMapper.map(userDto, User.class);
        try {
            userDao.update(user);
            return modelMapper.map(user, UserDto.class);
        } catch (DataIntegrityViolationException ex) {
            log.error("User with login '" + user.getLogin() + "' already exists");
            throw new ServiceConflictException("User with login '" + user.getLogin() + "' already exists");
        }
    }

    public void delete(long userId) {
        Optional<User> user = userDao.find(userId);
        if (!user.isPresent()) {
            throw new UserNotFoundException("User with id " + userId + " doesn't exist");
        }
        userDao.delete(user.get());
    }

    public UserDto find(long id) {
        Optional<User> user = userDao.find(id);
        if(user.isPresent()) {
            return modelMapper.map(user.get(), UserDto.class);
        } else {
            throw new UserNotFoundException("User with id " + id + " doesn't exist");
        }
    }

    public PagedModel<UserDto> findUsers(int page, int perPage) {
        List<UserDto> users = userDao.findUsers(page, perPage).stream()
                .map(user -> modelMapper.map(user, UserDto.class))
                .collect(Collectors.toList());
        int totalUsersCount = userDao.findAllUserCount().intValue();
        PagedModel.PageMetadata pageMetadata = new PagedModel.PageMetadata(perPage, page, totalUsersCount);
        return PagedModel.of(users, pageMetadata);
    }
}

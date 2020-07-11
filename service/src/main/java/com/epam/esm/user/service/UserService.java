package com.epam.esm.user.service;

import com.epam.esm.certificate.dto.CertificateDto;
import com.epam.esm.exception.ServiceConflictException;
import com.epam.esm.user.dao.UserDao;
import com.epam.esm.user.dto.UserDto;
import com.epam.esm.user.exception.UserNotFoundException;
import com.epam.esm.user.model.Role;
import com.epam.esm.user.model.User;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserService {
    private final UserDao userDao;
    private final ModelMapper modelMapper;

    @Autowired
    public UserService(UserDao userDao,
                       ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
        this.userDao = userDao;
    }

    public void create(UserDto userDto) {
        userDto.setId(0);
        User user = modelMapper.map(userDto, User.class);
        try {
            userDao.create(user);
        } catch (DataIntegrityViolationException ex) {
            log.error("User with login '" + user.getLogin() + "' already exists");
            throw new ServiceConflictException("User with login '" + user.getLogin() + "' already exists");
        }
    }

    public void update(UserDto userDto) {
        if (!userDao.find(userDto.getId()).isPresent()) {
            throw new UserNotFoundException("User with id " + userDto.getId() + " doesn't exist");
        }
        User user = modelMapper.map(userDto, User.class);
        try {
            userDao.update(user);
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

    public List<UserDto> findUsers(int page, int perPage) {
        return userDao.findUsers(page, perPage).stream()
                .map(user -> modelMapper.map(user, UserDto.class))
                .collect(Collectors.toList());
    }
}

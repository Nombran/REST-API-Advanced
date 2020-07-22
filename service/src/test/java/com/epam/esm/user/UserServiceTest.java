package com.epam.esm.user;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.modelmapper.ModelMapper;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class UserServiceTest {
    @InjectMocks
    UserService userService;
    @Mock
    UserDao userDao;
    @Spy
    ModelMapper modelMapper;

    @BeforeEach
    public void init() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void create_correctUser_shouldReturnUserWithId() {
        //Given
        doAnswer(invocation -> {
            User user = invocation.getArgument(0);
            user.setId(1);
            return user ;
        }).when(userDao).create(any(User.class));
        UserDto userDto = new UserDto("name", "lastName", "login");

        //When
        userDto = userService.create(userDto);

        //Then
        assertEquals(1, userDto.getId());
    }

    @Test
    public void create_correctUser_shouldCallDaoCreate() {
        //Given
        doAnswer(invocation -> {
            User user = invocation.getArgument(0);
            user.setId(1);
            return user ;
        }).when(userDao).create(any(User.class));
        UserDto userDto = new UserDto("name", "lastName", "login");

        //When
        userService.create(userDto);

        //Then
        verify(userDao, times(1)).create(notNull());
    }

    @Test
    public void update_nonexistentUserId_shouldTrowException() {
        //Given
        doAnswer(invocation -> Optional.empty()).when(userDao)
                .find(anyLong());
        UserDto userDto = new UserDto();
        long userId = 1;

        //When Then
        Assertions.assertThrows(UserNotFoundException.class,
                ()-> userService.update(userDto, userId));
    }

    @Test
    public void update_correctUser_shouldCallDaoCreate() {
        //Given
        doAnswer(invocation -> Optional.of(new User())).when(userDao).find(anyLong());
        UserDto userDto = new UserDto("name", "lastName", "login");

        //When
        userService.update(userDto, 1L);

        //Then
        verify(userDao, times(1)).update(notNull());
    }

    @Test
    public void find_nonexistentId_shouldThrowException() {
        //Given
        doAnswer(invocation -> Optional.empty()).when(userDao)
                .find(anyLong());

        //When Then
        Assertions.assertThrows(UserNotFoundException.class,
                ()->userService.find(1));
    }
}

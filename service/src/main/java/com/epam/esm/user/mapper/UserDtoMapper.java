package com.epam.esm.user.mapper;

import com.epam.esm.order.dao.OrderDao;
import com.epam.esm.order.model.Order;
import com.epam.esm.user.dto.UserDto;
import com.epam.esm.user.model.Role;
import com.epam.esm.user.model.User;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;

@Component
public class UserDtoMapper {
    private final ModelMapper mapper;
    private final OrderDao orderDao;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserDtoMapper(ModelMapper modelMapper,
                                OrderDao orderDao,
                         PasswordEncoder passwordEncoder) {
        this.mapper = modelMapper;
        this.orderDao = orderDao;
        this.passwordEncoder = passwordEncoder;
    }

    @PostConstruct
    public void setupMapper() {
        mapper.createTypeMap(User.class, UserDto.class)
                .addMappings(m -> m.skip(UserDto::setPassword)).setPostConverter(toDtoConverter());
        mapper.createTypeMap(UserDto.class, User.class)
                .addMappings(m-> m.skip(User::setOrders)).setPostConverter(toEntityConverter());
    }

    public Converter<User, UserDto> toDtoConverter() {
        return context -> {
            User source = context.getSource();
            UserDto destination = context.getDestination();
            mapSpecificFields(source, destination);
            return context.getDestination();
        };
    }

    public Converter<UserDto, User> toEntityConverter() {
        return context -> {
            UserDto source = context.getSource();
            User destination = context.getDestination();
            mapSpecificFields(source, destination);
            return context.getDestination();
        };
    }

    public void mapSpecificFields(User source, UserDto destination) {
        destination.setPassword(null);
    }

    public void mapSpecificFields(UserDto source, User destination) {
        long userId = source.getId();
        if(source.getId() != 0) {
            List<Order> userOrders = orderDao.getOrdersByUserId(userId);
            destination.setOrders(userOrders);
        }
        String password = source.getPassword();
        if(password != null) {
            destination.setPassword(passwordEncoder.encode(password));
        }
        destination.setRole(Role.USER);
    }
}
package com.epam.esm.review;

import com.epam.esm.service.Service;
import com.epam.esm.service.ServiceDto;
import com.epam.esm.service.ServiceNotFoundException;
import com.epam.esm.service.ServiceStatus;
import com.epam.esm.tag.Tag;
import com.epam.esm.user.User;
import com.epam.esm.user.UserDao;
import com.epam.esm.user.UserDto;
import com.epam.esm.user.UserNotFoundException;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ReviewDtoMapper {
    private final UserDao userDao;
    private final ModelMapper mapper;

    @Autowired
    public ReviewDtoMapper(UserDao userDao,
                           ModelMapper mapper) {
        this.mapper = mapper;
        this.userDao = userDao;
    }

    @PostConstruct
    public void setupMapper() {
        mapper.createTypeMap(Review.class, ReviewDto.class)
                .setPostConverter(toDtoConverter());
        mapper.createTypeMap(ReviewDto.class, Review.class)
                .addMappings(m-> m.skip(Review::setCreator))
                .addMappings(m -> m.skip(Review::setDeveloper))
                .setPostConverter(toEntityConverter());
    }

    public Converter<Review, ReviewDto> toDtoConverter() {
        return context -> {
            Review source = context.getSource();
            ReviewDto destination = context.getDestination();
            mapSpecificFields(source, destination);
            return context.getDestination();
        };
    }

    public Converter<ReviewDto, Review> toEntityConverter() {
        return context -> {
            ReviewDto source = context.getSource();
            Review destination = context.getDestination();
            mapSpecificFields(source, destination);
            return context.getDestination();
        };
    }

    public void mapSpecificFields(Review source, ReviewDto destination) {
        long developerId = source.getDeveloper().getId();
        long creatorId = source.getCreator().getId();
        destination.setCreatorId(creatorId);
        destination.setDeveloperId(developerId);
    }

    public void mapSpecificFields(ReviewDto source, Review destination) {
        User creator = userDao.find(source.getCreatorId()).orElseThrow(() ->
                new UserNotFoundException("creator not found")
        );
        User developer = userDao.find(source.getDeveloperId()).orElseThrow(() ->
                new UserNotFoundException("developer not found")
        );
        destination.setCreator(creator);
        destination.setDeveloper(developer);
    }

}

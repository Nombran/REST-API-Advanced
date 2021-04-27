package com.epam.esm.user;

import com.epam.esm.review.ReviewDao;
import com.epam.esm.review.ReviewDto;
import com.epam.esm.tag.Tag;
import com.epam.esm.tag.TagDao;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class UserDtoMapper {
    private final ModelMapper mapper;
    private final PasswordEncoder passwordEncoder;
    private final TagDao tagDao;
    private final ReviewDao reviewDao;

    @Autowired
    public UserDtoMapper(ModelMapper modelMapper,
                         PasswordEncoder passwordEncoder,
                         TagDao tagDao,
                         ReviewDao reviewDao) {
        this.mapper = modelMapper;
        this.passwordEncoder = passwordEncoder;
        this.tagDao = tagDao;
        this.reviewDao = reviewDao;
    }

    @PostConstruct
    public void setupMapper() {
        mapper.createTypeMap(User.class, UserDto.class)
                .addMappings(m -> m.skip(UserDto::setPassword))
                .addMappings(m -> m.skip(UserDto::setSkills))
                .addMappings(m -> m.skip(UserDto::setReviews))
                .setPostConverter(toDtoConverter());
        mapper.createTypeMap(UserDto.class, User.class)
                .addMappings(m-> m.skip(User::setSkills))
                .setPostConverter(toEntityConverter());
    }

    public Converter<User, UserDto> toDtoConverter() {
        return context -> {
            UserDto destination = context.getDestination();
            mapSpecificFields(context.getSource(), destination);
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
        List<Tag> skills = source.getSkills();
        List<String> stringSkills = skills.stream().map(Tag::getName).collect(Collectors.toList());
        destination.setSkills(stringSkills);
        destination.setPassword(null);
        if(source.getCreatedServices() != null) {
            int allServicesCount = source.getCreatedServices().size();
            destination.setAllServicesCount(allServicesCount);
            int processingServicesCount = (int)source.getCreatedServices().stream().filter(service -> service.getDeveloper() != null).count();
            destination.setProcessingServicesCount(processingServicesCount);
        }
        List<ReviewDto> reviewDtos = reviewDao.findByDeveloperId((int)source.getId())
                .stream()
                .map(review -> mapper.map(review, ReviewDto.class))
                .collect(Collectors.toList());
        destination.setReviews(reviewDtos);
    }

    public void mapSpecificFields(UserDto source, User destination) {
        List<String> skills = source.getSkills();
        List<Tag> tagSkills = skills.stream().map(skill -> tagDao.findByName(skill).orElseGet(() -> {
            Tag newTag = new Tag(skill);
            tagDao.create(newTag);
            return newTag;
        })).collect(Collectors.toList());
        destination.setSkills(tagSkills);
        String password = source.getPassword();
        if(password != null) {
            destination.setPassword(passwordEncoder.encode(password));
        }
        destination.setRole(Role.ROLE_ADMIN);
    }

    public static void main(String[] args) {
        System.out.println(new BCryptPasswordEncoder().encode("password"));
    }
}

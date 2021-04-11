package com.epam.esm.service;

import com.epam.esm.tag.Tag;
import com.epam.esm.tag.TagDao;
import com.epam.esm.user.User;
import com.epam.esm.user.UserDao;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ServiceDtoMapper {
    private final ModelMapper mapper;
    private final TagDao tagDao;
    private final UserDao userDao;

    @Autowired
    public ServiceDtoMapper(ModelMapper modelMapper,
                            TagDao tagDao,
                            UserDao userDao) {
        this.mapper = modelMapper;
        this.tagDao = tagDao;
        this.userDao = userDao;
    }

    @PostConstruct
    public void setupMapper() {
        mapper.createTypeMap(Service.class, ServiceDto.class)
                .addMappings(m -> m.skip(ServiceDto::setTags))
                .setPostConverter(toDtoConverter());
        mapper.createTypeMap(ServiceDto.class, Service.class)
                .addMappings(m-> m.skip(Service::setTags))
                .addMappings(m -> m.skip(Service::setCreator))
                .setPostConverter(toEntityConverter());
    }

    public Converter<Service, ServiceDto> toDtoConverter() {
        return context -> {
            Service source = context.getSource();
            ServiceDto destination = context.getDestination();
            mapSpecificFields(source, destination);
            return context.getDestination();
        };
    }

    public Converter<ServiceDto, Service> toEntityConverter() {
        return context -> {
            ServiceDto source = context.getSource();
            Service destination = context.getDestination();
            mapSpecificFields(source, destination);
            return context.getDestination();
        };
    }

    public void mapSpecificFields(Service source, ServiceDto destination) {
        List<Tag> certificateTags = tagDao.findByCertificateId(source.getId());
        destination.setTags(certificateTags.stream()
                .map(Tag::getName)
                .collect(Collectors.toList()));
        long creatorId = source.getCreator().getId();
        destination.setCreatorId(creatorId);
        User developer = source.getDeveloper();
        if(developer != null) {
            destination.setDeveloperId(developer.getId());
        }
    }

    public void mapSpecificFields(ServiceDto source, Service destination) throws ServiceNotFoundException {
        long creatorId = source.getCreatorId();
        User creator = userDao.find(creatorId).orElseThrow(() ->
            new ServiceNotFoundException("user with id " + creatorId + "not found")
        );
        destination.setCreator(creator);
        long developerId = source.getDeveloperId();
        if(developerId != 0) {
            User developer = userDao.find(developerId).orElseThrow(() ->
                new ServiceNotFoundException("user with id " + creatorId + "not found")
            );
            destination.setDeveloper(developer);
        }
        List<String> tagsAsString = source.getTags()
                .stream()
                .distinct()
                .collect(Collectors.toList());
        List<Tag> tagsAsObjects = tagsAsString.stream()
                .map(name -> tagDao.findByName(name).orElse(new Tag(name)))
                .collect(Collectors.toList());
        destination.setTags(tagsAsObjects);
    }
}

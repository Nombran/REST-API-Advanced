package com.epam.esm.service;

import com.epam.esm.tag.Tag;
import com.epam.esm.tag.TagDao;
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
public class ServiceDtoMapper {
    private final ModelMapper mapper;
    private final TagDao tagDao;
    private final UserDao userDao;
    private final ServiceDao serviceDao;

    @Autowired
    public ServiceDtoMapper(ModelMapper modelMapper,
                            TagDao tagDao,
                            UserDao userDao,
                            ServiceDao serviceDao) {
        this.mapper = modelMapper;
        this.tagDao = tagDao;
        this.userDao = userDao;
        this.serviceDao = serviceDao;
    }

    @PostConstruct
    public void setupMapper() {
        mapper.createTypeMap(Service.class, ServiceDto.class)
                .addMappings(m -> m.skip(ServiceDto::setTags))
                .addMappings(m -> m.skip(ServiceDto::setDesiredDevelopers))
                .setPostConverter(toDtoConverter());
        mapper.createTypeMap(ServiceDto.class, Service.class)
                .addMappings(m-> m.skip(Service::setTags))
                .addMappings(m -> m.skip(Service::setCreator))
                .addMappings(m -> m.skip(Service::setDesiredDevelopers))
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
            UserDto dtoDev = mapper.map(developer, UserDto.class);
            destination.setDeveloper(dtoDev);
        }
        destination.setStatus(source.getStatus().name());
        List<User> desiredDevelopers = source.getDesiredDevelopers();
        if(desiredDevelopers != null) {
            destination.setDesiredDevelopers(desiredDevelopers.stream().map(dev -> mapper.map(dev, UserDto.class)).collect(Collectors.toList()));
        }
    }

    public void mapSpecificFields(ServiceDto source, Service destination) throws ServiceNotFoundException {
        if(source.getId() != 0) {
            Service service = serviceDao.find(source.getId()).orElseThrow(()->
                    new ServiceNotFoundException("not found"));
            service.setName(source.getName());
            service.setDescription(source.getDescription());
            service.setPrice(source.getPrice());
            List<String> tagsAsString = source.getTags()
                    .stream()
                    .distinct()
                    .collect(Collectors.toList());
            List<Tag> tagsAsObjects = tagsAsString.stream()
                    .map(name -> tagDao.findByName(name).orElse(new Tag(name)))
                    .collect(Collectors.toList());
            service.setTags(tagsAsObjects);
        } else {
            long creatorId = source.getCreatorId();
            User creator = userDao.find(creatorId).orElseThrow(() ->
                    new ServiceNotFoundException("user with id " + creatorId + "not found")
            );
            destination.setCreator(creator);
            List<String> tagsAsString = source.getTags()
                    .stream()
                    .distinct()
                    .collect(Collectors.toList());
            List<Tag> tagsAsObjects = tagsAsString.stream()
                    .map(name -> tagDao.findByName(name).orElse(new Tag(name)))
                    .collect(Collectors.toList());
            destination.setTags(tagsAsObjects);
            destination.setStatus(ServiceStatus.valueOf(source.getStatus()));
        }
    }
}

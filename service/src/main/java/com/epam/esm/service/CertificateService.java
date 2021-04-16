package com.epam.esm.service;

import com.epam.esm.tag.Tag;
import com.epam.esm.tag.TagDao;
import com.epam.esm.tag.TagDto;
import com.epam.esm.user.User;
import com.epam.esm.user.UserDao;
import com.epam.esm.user.UserDto;
import com.epam.esm.user.UserNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.hateoas.PagedModel;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@org.springframework.stereotype.Service
@Transactional
public class CertificateService {
    private final ServiceDao serviceDao;
    private final TagDao tagDao;
    private final ModelMapper modelMapper;
    private UserDao userDao;
    public CertificateService(TagDao tagDao,
                              ServiceDao serviceDao,
                              ModelMapper modelMapper,
                              UserDao userDao) {
        this.tagDao = tagDao;
        this.serviceDao = serviceDao;
        this.modelMapper = modelMapper;
        this.userDao = userDao;
    }

    public ServiceDto create(ServiceDto serviceDto) {
        String name = serviceDto.getName();
        Optional<Service> certificateWithSuchName = serviceDao.findCertificateByName(name);
        if(certificateWithSuchName.isPresent()) {
            throw new ServiceConflictException("Certificate with name '" + name + "' already exists");
        }
        Service service = modelMapper.map(serviceDto, Service.class);
        serviceDao.create(service);
        return modelMapper.map(service, ServiceDto.class);
    }

    public void update(ServiceDto serviceDto) {
        Service service = modelMapper.map(serviceDto, Service.class);
        serviceDao.update(service);
    }

    public void patch(long id, ServiceDto changes) {
        Service service = serviceDao.find(id).orElseThrow(() ->
                new ServiceNotFoundException("Certificate with id = " + id + " doesn't exist")
        );
        ServiceDto serviceDto = modelMapper.map(service, ServiceDto.class);

        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        Set<ConstraintViolation<ServiceDto>> violations = validator.validate(serviceDto);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }
        update(serviceDto);
    }

    public ServiceDto find(long id) {
        Optional<Service> certificate = serviceDao.find(id);
        if (certificate.isPresent()) {
            return modelMapper.map(certificate.get(), ServiceDto.class);
        } else {
            throw new ServiceNotFoundException("Certificate with id = " + id + " doesn't exist");
        }
    }

    public PagedModel<ServiceDto> findCertificates(ServiceParamWrapper wrapper) {
        String orderBy = wrapper.getOrderBy();
        boolean isOrderByCorrect = Stream.of(ServiceOrderBy.values())
                .anyMatch(value -> value.getOrderByFieldName()
                        .equals(orderBy));
        if(!isOrderByCorrect) {
            throw new IllegalArgumentException("Invalid orderBy param");
        }
        List<ServiceDto> resultList = serviceDao.findCertificates(wrapper)
                .stream()
                .map(certificate -> modelMapper.map(certificate, ServiceDto.class))
                .collect(Collectors.toList());
        int totalElements = serviceDao.getTotalElementsCountFromCertificateSearch(wrapper);
        int page = wrapper.getPage();
        int perPage = wrapper.getPerPage();
        PagedModel.PageMetadata pageMetadata = new PagedModel.PageMetadata(perPage, page, totalElements);
        long totalPages = pageMetadata.getTotalPages();
        if(totalPages < page && totalPages != 0) {
            throw new IllegalArgumentException("Invalid page number. There is only " + totalPages + " page/pages");
        }
        return PagedModel.of(resultList, pageMetadata);
    }

    public void addCertificateTag(TagDto tagDto, long certificateId) {
        Service service = serviceDao.find(certificateId)
                .orElseThrow(() -> new ServiceNotFoundException("Certificate with id "
                        + certificateId + " doesn't exist"));
        Tag tag = modelMapper.map(tagDto, Tag.class);
        Tag tagToAdd = tagDao.findByName(tag.getName()).orElseGet(() -> {
            tagDao.create(tag);
            return tag;
        });
        if (service.getTags().contains(tagToAdd)) {
            throw new ServiceConflictException("The certificate already has this tag");
        } else {
            service.getTags().add(tagToAdd);
            serviceDao.update(service);
        }
    }

    public void deleteCertificateTag(long certificateId, long tagId) {
        Service service = serviceDao.find(certificateId)
                .orElseThrow(() -> new ServiceNotFoundException("Certificate with id "
                        + certificateId + " doesn't exist"));
        List<Tag> certificateTags = service.getTags();
        if (certificateTags.stream()
                .anyMatch(tag -> tag.getId() == tagId)) {
            certificateTags.removeIf(tag -> tag.getId() == tagId);
        }
        serviceDao.update(service);
    }

    public PagedModel<ServiceDto> getUserCreatedServices(long userId) {
        User user = userDao.find(userId).orElseThrow(()->
                new ServiceNotFoundException("user not found"));
        List<Service> createdServices = user.getCreatedServices();
        List<ServiceDto> serviceDtos = createdServices.stream()
                .map(service -> modelMapper.map(service, ServiceDto.class))
                .collect(Collectors.toList());
        PagedModel.PageMetadata pageMetadata = new PagedModel.PageMetadata(10, 1, 10);
        return PagedModel.of(serviceDtos, pageMetadata);
    }

    public List<ServiceDto> findFreeServices() {
        return serviceDao.findCertificates(new ServiceParamWrapper(null, null, null, 1, 100))
                .stream()
                .map(certificate -> modelMapper.map(certificate, ServiceDto.class))
                .collect(Collectors.toList());
    }

    public List<UserDto> findServiceDesiredDevs(int id) {
        Service service = serviceDao.find(id).orElseThrow(()->
            new ServiceNotFoundException("service with id" + id + "not found")
        );
        return service.getDesiredDevelopers().stream()
                .map(user -> modelMapper.map(user, UserDto.class))
                .collect(Collectors.toList());
    }

    public void addDesiredDev(int id, int devId) {
        Service service = serviceDao.find(id).orElseThrow(()->
                new ServiceNotFoundException("service with id" + id + "not found")
        );
        User dev = userDao.find(devId).orElseThrow(() ->
                new UserNotFoundException("user with id" + devId + "not found")
        );
        List<User> desiredDevs = service.getDesiredDevelopers();
        if(desiredDevs == null) {
            desiredDevs = new ArrayList<>();
        }
        desiredDevs.add(dev);
        service.setDesiredDevelopers(desiredDevs);
        serviceDao.update(service);
    }

    public void deleteDesiredDev(int id, int devId) {
        Service service = serviceDao.find(id).orElseThrow(()->
                new ServiceNotFoundException("service with id" + id + "not found")
        );
        List<User> desiredDevs = service.getDesiredDevelopers();
        if(desiredDevs == null) {
            throw new ServiceNotFoundException("user with id" + devId + "not found");
        }
        service.setDesiredDevelopers(desiredDevs.stream().filter(user -> user.getId() != devId).collect(Collectors.toList()));
        serviceDao.update(service);
    }
}

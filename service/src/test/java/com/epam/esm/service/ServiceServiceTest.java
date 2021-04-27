package com.epam.esm.service;

import com.epam.esm.tag.Tag;
import com.epam.esm.tag.TagDao;
import com.epam.esm.tag.TagDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.modelmapper.ModelMapper;

import javax.validation.ConstraintViolationException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.notNull;
import static org.mockito.Mockito.*;

public class ServiceServiceTest {
    @InjectMocks
    CertificateService certificateService;
    @Mock
    ServiceDao serviceDao;
    @Mock
    TagDao tagDao;
    @Spy
    ModelMapper modelMapper;

    @BeforeEach
    public void init() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void create_certificateWithTags_shouldReturnCertificateWithId() {
        //Given
        Mockito.when(serviceDao.findCertificateByName("name")).thenReturn(Optional.empty());
        doAnswer(invocation -> {
            Service service = invocation.getArgument(0);
            service.setId(1);
            return null;
        }).when(serviceDao).create(notNull());

        ServiceDto serviceDto = new ServiceDto("name", "description", new BigDecimal("12.6"),
                5, ServiceStatus.PUBLISHED, Arrays.asList("tagOne", "tagTwo"));

        //When
        ServiceDto result = certificateService.create(serviceDto);

        //Then
        assertEquals(1, result.getId());
    }

    @Test
    public void create_certificateWithTags_shouldCallDaoCreateMethod() {
        //Given
        doNothing().when(serviceDao).create(any(Service.class));
        ServiceDto serviceDto = new ServiceDto("name", "description", new BigDecimal("12.6"),
                5, ServiceStatus.PUBLISHED, Arrays.asList("tagOne", "tagTwo"));

        //When
        certificateService.create(serviceDto);

        //Then
        verify(serviceDao, times(1)).create(notNull());
    }

    @Test
    public void create_certificateWithTags_shouldThrowConflictException() {
        //Given
        doAnswer((invocation) -> Optional.of(new Service()))
                .when(serviceDao)
                .findCertificateByName(anyString());
        ServiceDto serviceDto = new ServiceDto("name", "description", new BigDecimal("12.6"),
                5, ServiceStatus.PUBLISHED, Arrays.asList("tagOne", "tagTwo"));

        //When
        ServiceConflictException result = Assertions.assertThrows(ServiceConflictException.class,
                () -> certificateService.create(serviceDto));

        //Then
        assertEquals("Certificate with name 'name' already exists", result.getMessage());
    }

    @Test
    public void update_inactiveCertificate_shouldThrowConflictException() {
        //Given
        doAnswer((invocation)-> {
            Service service = new Service();
            service.setStatus(ServiceStatus.INACTIVE);
            return Optional.of(service);
        }).when(serviceDao).find(anyLong());
        ServiceDto serviceDto = new ServiceDto("name", "description", new BigDecimal("12.6"),
                5, ServiceStatus.PUBLISHED, Arrays.asList("tagOne", "tagTwo"));

        //When
        ServiceConflictException exception = assertThrows(ServiceConflictException.class,()->
                certificateService.update(serviceDto));

        //Then
        assertEquals("Cannot update certificate with status INACTIVE", exception.getMessage());
    }

    @Test
    public void update_activeCertificate_shouldThrowConflictException() {
        //Given
        doAnswer((invocation)-> {
            Service service = new Service("name", "description", new BigDecimal("12.6"),
                    5);
            service.setStatus(ServiceStatus.ACTIVE);
            service.setTags(Collections.emptyList());
            return Optional.of(service);
        }).when(serviceDao).find(anyLong());
        ServiceDto serviceDto = new ServiceDto("name", "description", new BigDecimal("12.6"),
                5, ServiceStatus.PUBLISHED, Arrays.asList("tagOne", "tagTwo"));

        //When
        ServiceConflictException exception = assertThrows(ServiceConflictException.class,()->
                certificateService.update(serviceDto));

        //Then
        assertEquals("Certificate with status ACTIVE can be only set to INACTIVE", exception.getMessage());
    }

    @Test
    public void update_publishedCertificateWithTags_shouldCallDaoUpdate() {
        //Given
        doAnswer((invocation)-> {
            Service service = new Service("name", "description", new BigDecimal("12.6"),
                    5);
            service.setStatus(ServiceStatus.PUBLISHED);
            service.setTags(Collections.emptyList());
            return Optional.of(service);
        }).when(serviceDao).find(anyLong());
        ServiceDto serviceDto = new ServiceDto("name", "description", new BigDecimal("12.6"),
                5, ServiceStatus.PUBLISHED, Arrays.asList("tagOne", "tagTwo"));

        //When
        certificateService.update(serviceDto);

        //Then
        verify(serviceDao, times(1)).update(any(Service.class));
    }

    @Test
    public void update_certificateWithNonUniqueName_shouldThrowConflictException(){
        //Given
        doAnswer((invocation)-> {
            Service service = new Service("name 2", "description", new BigDecimal("12.6"),
                    5);
            service.setStatus(ServiceStatus.PUBLISHED);
            service.setTags(Collections.emptyList());
            return Optional.of(service);
        }).when(serviceDao).find(anyLong());
        doAnswer((invocation -> Optional.of(new Service())))
                .when(serviceDao)
                .findCertificateByName("name");
        ServiceDto serviceDto = new ServiceDto("name", "description", new BigDecimal("12.6"),
                5, ServiceStatus.PUBLISHED, Arrays.asList("tagOne", "tagTwo"));

        //When
        ServiceConflictException exception = Assertions.assertThrows(ServiceConflictException.class,
                ()-> certificateService.update(serviceDto));

        //Then
        assertEquals("Certificate with name 'name' already exists", exception.getMessage());
    }

    @Test
    public void patch_certificateWithNonexistentId_shouldThrowException() {
        //Given
        doAnswer((invocation -> Optional.empty()))
                .when(serviceDao)
                .find(anyLong());
        ServiceDto serviceDto = new ServiceDto();

        //When
        ServiceNotFoundException exception = Assertions.assertThrows(ServiceNotFoundException.class,
                ()-> certificateService.patch(1, serviceDto));

        //Then
        assertEquals("Certificate with id = " + 1 + " doesn't exist", exception.getMessage());
    }

    @Test
    public void patch_invalidName_shouldThrowException() {
        //Given
        doAnswer(invocation -> Optional.of(new Service(1,"name",
                "description", new BigDecimal("12.6"), LocalDateTime.now(),LocalDateTime.now(),
                5, ServiceStatus.PUBLISHED, Collections.emptyList())))
                .when(serviceDao)
                .find(anyLong());
        ServiceDto changes = new ServiceDto();
        changes.setName("a");

        //When Then
        Assertions.assertThrows(ConstraintViolationException.class,
                ()-> certificateService.patch(1, changes));
    }

    @Test
    public void patch_correctCertificateChanges_shouldCallDaoUpdate() {
        //Given
        doAnswer(invocation -> Optional.of(new Service(1,"name",
                "description", new BigDecimal("12.6"), LocalDateTime.now(),LocalDateTime.now(),
                5, ServiceStatus.PUBLISHED, Collections.emptyList())))
                .when(serviceDao)
                .find(anyLong());
        ServiceDto changes = new ServiceDto();
        changes.setName("new name for certificate");

        //When
        certificateService.patch(1, changes);

        //Then
        verify(serviceDao, times(1)).update(any(Service.class));
    }

    @Test
    public void find_NonexistentCertificateId_shouldThrowException() {
        //Given
        doAnswer(invocation -> Optional.empty())
                .when(serviceDao)
                .find(anyLong());

        //When Then
        Assertions.assertThrows(ServiceNotFoundException.class,
                ()->certificateService.find(1));
    }

//    @Test
//    public void findCertificates_defaultParams_shouldReturnCorrectPageMetadata() {
//        //Given
//        doAnswer(invocation -> 2)
//                .when(certificateDao).getTotalElementsCountFromCertificateSearch(notNull());
//        int page = 1;
//        int perPage = 50;
//        doAnswer(invocation -> {
//            Certificate certificateOne = new Certificate("name", "description", new BigDecimal("12.6"),
//                    5);
//            certificateOne.setStatus(CertificateStatus.PUBLISHED);
//            certificateOne.setTags(Collections.emptyList());
//            Certificate certificateTwo = new Certificate("name 2", "description 2", new BigDecimal("12.6"),
//                    10);
//            certificateTwo.setStatus(CertificateStatus.PUBLISHED);
//            certificateTwo.setTags(Collections.emptyList());
//            return Arrays.asList(certificateOne, certificateTwo);
//        })
//                .when(certificateDao).findCertificates(notNull());
//        PagedModel.PageMetadata pageMetadata = new PagedModel.PageMetadata(perPage, page, 2);
//        CertificateParamWrapper wrapper = new CertificateParamWrapper(new String[0], "text",
//                "price", page, perPage);
//
//        //When
//        PagedModel<CertificateDto> model = certificateService.findCertificates(wrapper);
//
//        //Then
//        assertEquals(pageMetadata, model.getMetadata());
//    }

    @Test
    public void addCertificateTag_newTag_shouldCallDaoUpdate() {
        //Given
        doAnswer(invocation -> {
            Service service = new Service("name", "description", new BigDecimal("2.5"),
                5);
            service.setId(1);
            service.setStatus(ServiceStatus.PUBLISHED);
            service.setTags(new ArrayList<>());
            return Optional.of(service);
        }).when(serviceDao).find(1L);
        TagDto newTag = new TagDto("new Tag");

        //When
        certificateService.addCertificateTag(newTag, 1);

        //Then
        verify(serviceDao, times(1)).update(notNull());
    }

    @Test
    public void addCertificateTag_existentTag_shouldThrowException() {
        //Given
        doAnswer(invocation -> {
            Tag tag = new Tag("new Tag");
            tag.setId(1);
            List<Tag> tags = new ArrayList<>();
            tags.add(tag);
            Service service = new Service("name", "description", new BigDecimal("2.5"),
                    5);
            service.setId(1L);
            service.setStatus(ServiceStatus.PUBLISHED);
            service.setTags(tags);
            return Optional.of(service);
        }).when(serviceDao).find(1L);
        doAnswer(invocation -> {
            Tag tag = new Tag("new Tag");
            tag.setId(1);
            return tag;
        })
                .when(tagDao)
                .find(1L);
        TagDto newTag = new TagDto(1L,"new Tag");

        //When Then
        Assertions.assertThrows(ServiceConflictException.class,
                ()->certificateService.addCertificateTag(newTag, 1L));
    }

    @Test
    public void addCertificateTag_nonPublishedCertificate_shouldThrowException() {
        //Given
        doAnswer(invocation -> {
            Service service = new Service("name", "description", new BigDecimal("2.5"),
                    5);
            service.setId(1L);
            service.setStatus(ServiceStatus.ACTIVE);
            return Optional.of(service);
        }).when(serviceDao).find(1L);
        TagDto newTag = new TagDto("new Tag");

        //When Then
        Assertions.assertThrows(ServiceConflictException.class,
                ()-> certificateService.addCertificateTag(newTag, 1L));
    }

    @Test
    public void deleteCertificateTag_existentCertificateTag_shouldDeleteTag() {
        //Given
        Tag tag = new Tag("new Tag");
        tag.setId(1);
        List<Tag> tags = new ArrayList<>();
        tags.add(tag);
        Service service = new Service("name", "description", new BigDecimal("2.5"),
                5);
        service.setId(1L);
        service.setStatus(ServiceStatus.PUBLISHED);
        service.setTags(tags);
        doAnswer(invocation -> Optional.of(service)).when(serviceDao).find(1L);
        doAnswer(invocation -> tag)
                .when(tagDao)
                .find(1L);

        //When
        certificateService.deleteCertificateTag(1L, 1L);

        //Then
        assertEquals(0, service.getTags().size());
    }
}

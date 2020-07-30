package com.epam.esm.certificate;

import com.epam.esm.tag.Tag;
import com.epam.esm.tag.TagDao;
import com.epam.esm.tag.TagDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.modelmapper.ModelMapper;
import org.springframework.hateoas.PagedModel;

import javax.validation.ConstraintViolationException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.notNull;
import static org.mockito.Mockito.*;

public class CertificateServiceTest {
    @InjectMocks
    CertificateService certificateService;
    @Mock
    CertificateDao certificateDao;
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
        Mockito.when(certificateDao.findNonInactiveCertificateByName("name")).thenReturn(Optional.empty());
        doAnswer(invocation -> {
            Certificate certificate = invocation.getArgument(0);
            certificate.setId(1);
            return null;
        }).when(certificateDao).create(notNull());

        CertificateDto certificateDto = new CertificateDto("name", "description", new BigDecimal("12.6"),
                5, CertificateStatus.PUBLISHED, Arrays.asList("tagOne", "tagTwo"));

        //When
        CertificateDto result = certificateService.create(certificateDto);

        //Then
        assertEquals(1, result.getId());
    }

    @Test
    public void create_certificateWithTags_shouldCallDaoCreateMethod() {
        //Given
        doNothing().when(certificateDao).create(any(Certificate.class));
        CertificateDto certificateDto = new CertificateDto("name", "description", new BigDecimal("12.6"),
                5, CertificateStatus.PUBLISHED, Arrays.asList("tagOne", "tagTwo"));

        //When
        certificateService.create(certificateDto);

        //Then
        verify(certificateDao, times(1)).create(notNull());
    }

    @Test
    public void create_certificateWithTags_shouldThrowConflictException() {
        //Given
        doAnswer((invocation) -> Optional.of(new Certificate()))
                .when(certificateDao)
                .findNonInactiveCertificateByName(anyString());
        CertificateDto certificateDto = new CertificateDto("name", "description", new BigDecimal("12.6"),
                5, CertificateStatus.PUBLISHED, Arrays.asList("tagOne", "tagTwo"));

        //When
        CertificateConflictException result = Assertions.assertThrows(CertificateConflictException.class,
                () -> certificateService.create(certificateDto));

        //Then
        assertEquals("Certificate with name 'name' already exists", result.getMessage());
    }

    @Test
    public void update_inactiveCertificate_shouldThrowConflictException() {
        //Given
        doAnswer((invocation)-> {
            Certificate certificate = new Certificate();
            certificate.setStatus(CertificateStatus.INACTIVE);
            return Optional.of(certificate);
        }).when(certificateDao).find(anyLong());
        CertificateDto certificateDto = new CertificateDto("name", "description", new BigDecimal("12.6"),
                5, CertificateStatus.PUBLISHED, Arrays.asList("tagOne", "tagTwo"));

        //When
        CertificateConflictException exception = assertThrows(CertificateConflictException.class,()->
                certificateService.update(certificateDto));

        //Then
        assertEquals("Cannot update certificate with status INACTIVE", exception.getMessage());
    }

    @Test
    public void update_activeCertificate_shouldThrowConflictException() {
        //Given
        doAnswer((invocation)-> {
            Certificate certificate = new Certificate("name", "description", new BigDecimal("12.6"),
                    5);
            certificate.setStatus(CertificateStatus.ACTIVE);
            certificate.setTags(Collections.emptyList());
            return Optional.of(certificate);
        }).when(certificateDao).find(anyLong());
        CertificateDto certificateDto = new CertificateDto("name", "description", new BigDecimal("12.6"),
                5, CertificateStatus.PUBLISHED, Arrays.asList("tagOne", "tagTwo"));

        //When
        CertificateConflictException exception = assertThrows(CertificateConflictException.class,()->
                certificateService.update(certificateDto));

        //Then
        assertEquals("Certificate with status ACTIVE can be only set to INACTIVE", exception.getMessage());
    }

    @Test
    public void update_publishedCertificateWithTags_shouldCallDaoUpdate() {
        //Given
        doAnswer((invocation)-> {
            Certificate certificate = new Certificate("name", "description", new BigDecimal("12.6"),
                    5);
            certificate.setStatus(CertificateStatus.PUBLISHED);
            certificate.setTags(Collections.emptyList());
            return Optional.of(certificate);
        }).when(certificateDao).find(anyLong());
        CertificateDto certificateDto = new CertificateDto("name", "description", new BigDecimal("12.6"),
                5, CertificateStatus.PUBLISHED, Arrays.asList("tagOne", "tagTwo"));

        //When
        certificateService.update(certificateDto);

        //Then
        verify(certificateDao, times(1)).update(any(Certificate.class));
    }

    @Test
    public void update_certificateWithNonUniqueName_shouldThrowConflictException(){
        //Given
        doAnswer((invocation)-> {
            Certificate certificate = new Certificate("name 2", "description", new BigDecimal("12.6"),
                    5);
            certificate.setStatus(CertificateStatus.PUBLISHED);
            certificate.setTags(Collections.emptyList());
            return Optional.of(certificate);
        }).when(certificateDao).find(anyLong());
        doAnswer((invocation -> Optional.of(new Certificate())))
                .when(certificateDao)
                .findNonInactiveCertificateByName("name");
        CertificateDto certificateDto = new CertificateDto("name", "description", new BigDecimal("12.6"),
                5, CertificateStatus.PUBLISHED, Arrays.asList("tagOne", "tagTwo"));

        //When
        CertificateConflictException exception = Assertions.assertThrows(CertificateConflictException.class,
                ()-> certificateService.update(certificateDto));

        //Then
        assertEquals("Certificate with name 'name' already exists", exception.getMessage());
    }

    @Test
    public void patch_certificateWithNonexistentId_shouldThrowException() {
        //Given
        doAnswer((invocation -> Optional.empty()))
                .when(certificateDao)
                .find(anyLong());
        CertificateDto certificateDto = new CertificateDto();

        //When
        CertificateNotFoundException exception = Assertions.assertThrows(CertificateNotFoundException.class,
                ()-> certificateService.patch(1,certificateDto));

        //Then
        assertEquals("Certificate with id = " + 1 + " doesn't exist", exception.getMessage());
    }

    @Test
    public void patch_invalidName_shouldThrowException() {
        //Given
        doAnswer(invocation -> Optional.of(new Certificate(1,"name",
                "description", new BigDecimal("12.6"), LocalDateTime.now(),LocalDateTime.now(),
                5, CertificateStatus.PUBLISHED, Collections.emptyList())))
                .when(certificateDao)
                .find(anyLong());
        CertificateDto changes = new CertificateDto();
        changes.setName("a");

        //When Then
        Assertions.assertThrows(ConstraintViolationException.class,
                ()-> certificateService.patch(1, changes));
    }

    @Test
    public void patch_correctCertificateChanges_shouldCallDaoUpdate() {
        //Given
        doAnswer(invocation -> Optional.of(new Certificate(1,"name",
                "description", new BigDecimal("12.6"), LocalDateTime.now(),LocalDateTime.now(),
                5, CertificateStatus.PUBLISHED, Collections.emptyList())))
                .when(certificateDao)
                .find(anyLong());
        CertificateDto changes = new CertificateDto();
        changes.setName("new name for certificate");

        //When
        certificateService.patch(1, changes);

        //Then
        verify(certificateDao, times(1)).update(any(Certificate.class));
    }

    @Test
    public void find_NonexistentCertificateId_shouldThrowException() {
        //Given
        doAnswer(invocation -> Optional.empty())
                .when(certificateDao)
                .find(anyLong());

        //When Then
        Assertions.assertThrows(CertificateNotFoundException.class,
                ()->certificateService.find(1));
    }

    @Test
    public void findCertificates_defaultParams_shouldReturnCorrectPageMetadata() {
        //Given
        doAnswer(invocation -> 2)
                .when(certificateDao).getTotalElementsCountFromCertificateSearch(notNull());
        int page = 1;
        int perPage = 50;
        doAnswer(invocation -> {
            Certificate certificateOne = new Certificate("name", "description", new BigDecimal("12.6"),
                    5);
            certificateOne.setStatus(CertificateStatus.PUBLISHED);
            certificateOne.setTags(Collections.emptyList());
            Certificate certificateTwo = new Certificate("name 2", "description 2", new BigDecimal("12.6"),
                    10);
            certificateTwo.setStatus(CertificateStatus.PUBLISHED);
            certificateTwo.setTags(Collections.emptyList());
            return Arrays.asList(certificateOne, certificateTwo);
        })
                .when(certificateDao).findCertificates(notNull());
        PagedModel.PageMetadata pageMetadata = new PagedModel.PageMetadata(perPage, page, 2);
        CertificateParamWrapper wrapper = new CertificateParamWrapper(new String[0], "text",
                "price", page, perPage);

        //When
        PagedModel<CertificateDto> model = certificateService.findCertificates(wrapper);

        //Then
        assertEquals(pageMetadata, model.getMetadata());
    }

    @Test
    public void addCertificateTag_newTag_shouldCallDaoUpdate() {
        //Given
        doAnswer(invocation -> {
            Certificate certificate = new Certificate("name", "description", new BigDecimal("2.5"),
                5);
            certificate.setId(1);
            certificate.setStatus(CertificateStatus.PUBLISHED);
            certificate.setTags(new ArrayList<>());
            return Optional.of(certificate);
        }).when(certificateDao).find(1L);
        TagDto newTag = new TagDto("new Tag");

        //When
        certificateService.addCertificateTag(newTag, 1);

        //Then
        verify(certificateDao, times(1)).update(notNull());
    }

    @Test
    public void addCertificateTag_existentTag_shouldThrowException() {
        //Given
        doAnswer(invocation -> {
            Tag tag = new Tag("new Tag");
            tag.setId(1);
            List<Tag> tags = new ArrayList<>();
            tags.add(tag);
            Certificate certificate = new Certificate("name", "description", new BigDecimal("2.5"),
                    5);
            certificate.setId(1L);
            certificate.setStatus(CertificateStatus.PUBLISHED);
            certificate.setTags(tags);
            return Optional.of(certificate);
        }).when(certificateDao).find(1L);
        doAnswer(invocation -> {
            Tag tag = new Tag("new Tag");
            tag.setId(1);
            return tag;
        })
                .when(tagDao)
                .find(1L);
        TagDto newTag = new TagDto(1L,"new Tag");

        //When Then
        Assertions.assertThrows(CertificateConflictException.class,
                ()->certificateService.addCertificateTag(newTag, 1L));
    }

    @Test
    public void addCertificateTag_nonPublishedCertificate_shouldThrowException() {
        //Given
        doAnswer(invocation -> {
            Certificate certificate = new Certificate("name", "description", new BigDecimal("2.5"),
                    5);
            certificate.setId(1L);
            certificate.setStatus(CertificateStatus.ACTIVE);
            return Optional.of(certificate);
        }).when(certificateDao).find(1L);
        TagDto newTag = new TagDto("new Tag");

        //When Then
        Assertions.assertThrows(CertificateConflictException.class,
                ()-> certificateService.addCertificateTag(newTag, 1L));
    }

    @Test
    public void deleteCertificateTag_existentCertificateTag_shouldDeleteTag() {
        //Given
        Tag tag = new Tag("new Tag");
        tag.setId(1);
        List<Tag> tags = new ArrayList<>();
        tags.add(tag);
        Certificate certificate = new Certificate("name", "description", new BigDecimal("2.5"),
                5);
        certificate.setId(1L);
        certificate.setStatus(CertificateStatus.PUBLISHED);
        certificate.setTags(tags);
        doAnswer(invocation -> Optional.of(certificate)).when(certificateDao).find(1L);
        doAnswer(invocation -> tag)
                .when(tagDao)
                .find(1L);

        //When
        certificateService.deleteCertificateTag(1L, 1L);

        //Then
        assertEquals(0, certificate.getTags().size());
    }
}

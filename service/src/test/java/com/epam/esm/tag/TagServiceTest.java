package com.epam.esm.tag;

import com.epam.esm.certificate.CertificateDao;
import com.epam.esm.certificate.CertificateNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.modelmapper.ModelMapper;
import org.springframework.hateoas.PagedModel;

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.notNull;
import static org.mockito.Mockito.*;

public class TagServiceTest {
    @InjectMocks
    TagService tagService;
    @Mock
    TagDao tagDao;
    @Mock
    CertificateDao certificateDao;
    @Spy
    ModelMapper modelMapper;
    @BeforeEach
    public void init() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void create_nonexistentTag_shouldReturnTagWithId() {
        //Given
        doAnswer(invocation -> {
            Tag tag = invocation.getArgument(0);
            tag.setId(1);
            return tag;
        }).when(tagDao).create(notNull());
        TagDto tag = new TagDto("new tag");

        //When
        tag = tagService.create(tag);

        //Then
        assertEquals(1,tag.getId());
    }

    @Test
    public void create_nonexistentTag_shouldCallDaoCreate() {
        //Given
        doAnswer(invocation -> {
            Tag tag = invocation.getArgument(0);
            tag.setId(1);
            return tag;
        }).when(tagDao).create(notNull());
        TagDto tag = new TagDto("new tag");

        //When
        tagService.create(tag);

        //Then
        verify(tagDao, times(1)).create(notNull());
    }

    @Test
    public void delete_nonexistentTag_shouldThrowException() {
        //Given
        doAnswer(invocation -> Optional.empty())
                .when(tagDao).find(anyLong());
        long idForDelete = 1;

        //When Then
        Assertions.assertThrows(TagNotFoundException.class,
                ()-> tagService.delete(idForDelete));
    }

    @Test
    public void delete_existentTag_shouldCallDaoDelete() {
        //Given
        doAnswer(invocation -> Optional.of(new Tag()))
                .when(tagDao).find(anyLong());
        long idForDelete = 1;

        //When
        tagService.delete(idForDelete);

        //Then
        verify(tagDao, times(1)).delete(notNull());
    }

    @Test
    public void find_nonexistentTag_shouldThrowException() {
        //Given
        doAnswer(invocation -> Optional.empty())
                .when(tagDao).find(anyLong());
        long idForFind = 1;

        //When //Then
        Assertions.assertThrows(TagNotFoundException.class,
                ()->tagService.find(idForFind));
    }

    @Test
    public void find_existentTag_shouldReturnTag() {
        //Given
        doAnswer(invocation -> {
            Tag tag = new Tag(1, "name", Collections.emptyList());
            return Optional.of(tag);
        }).when(tagDao).find(anyLong());
        long idForFind = 1;

        //When
        TagDto result = tagService.find(idForFind);

        //Then
        assertNotNull(result);
    }

    @Test
    public void findTags_defaultParams_shouldReturnCorrectPageMetadata() {
        //Given
        doAnswer(invocation -> {
            Tag tagOne = new Tag(1, "first", Collections.emptyList());
            Tag tagTwo = new Tag(2, "second", Collections.emptyList());
            return Arrays.asList(tagOne, tagTwo);
        }).when(tagDao).findTags(anyInt(), anyInt(), anyString());
        int page = 1;
        int perPage = 50;
        doAnswer(invocation -> 2L).when(tagDao)
                .getCountOfTags(null);
        PagedModel.PageMetadata expected = new PagedModel.PageMetadata(perPage, page, 2);

        //When
        PagedModel<TagDto> model = tagService.findTags(page, perPage, null);

        //Then
        assertEquals(expected, model.getMetadata());
    }

    @Test
    public void findTagsByCertificateId_nonexistentId_shouldThrowException() {
        //Given
        doAnswer(invocation -> Optional.empty()).when(certificateDao)
                .find(anyLong());
        long idForFind = 1;

        //When Then
        Assertions.assertThrows(CertificateNotFoundException.class,
                ()->tagService.findTagsByCertificateId(idForFind));
    }
}

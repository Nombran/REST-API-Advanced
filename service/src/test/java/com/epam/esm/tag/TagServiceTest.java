package com.epam.esm.tag;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;

public class TagServiceTest {
//    @InjectMocks
//    TagService tagService;
//    @Mock
//    CertificateDao certificateDao;
//    @Mock
//    TagDao tagDao;
//
//    @Before
//    public void init() {
//        MockitoAnnotations.initMocks(this);
//    }
//
//    @Test
//    public void create_correctTag_shouldEndWithoutException() {
//        //Given
//        Tag tag = new Tag("Tag for test");
//        Mockito.when(tagDao.create(tag)).thenReturn(tag);
//
//        //When
//        tagService.create(tag);
//
//        //Then
//        verify(tagDao, times(1)).create(tag);
//    }
//
//    @Test
//    public void delete_correctCertificateId_shouldEndWithoutException() {
//        //Given
//        long tagId = 1;
//        Mockito.when(tagDao.delete(1)).thenReturn(true);
//
//        //When
//        tagService.delete(tagId);
//
//        //Then
//        verify(tagDao, times(1)).delete(tagId);
//    }
//
//    @Test
//    public void find_existentTagId_shouldReturnCorrectTag() {
//        //Given
//        long tagId = 1;
//        Tag tag = new Tag(1, "Tag for test");
//        Mockito.when(tagDao.find(1)).thenReturn(Optional.of(tag));
//
//        //When
//        Tag result = tagService.find(tagId);
//
//        //Then
//        verify(tagDao, times(1)).find(tagId);
//        assertEquals(tag, result);
//    }
//
//    @Test
//    public void findAll_shouldReturnAllTags() {
//        //Given
//        Tag tagOne = new Tag(1, "tagOne");
//        Tag tagTwo = new Tag(2, "tagTwo");
//        Tag tagThree = new Tag(3, "tagThree");
//        List<Tag> tagList = Arrays.asList(tagOne, tagTwo, tagThree);
//        Mockito.when(tagDao.findAll()).thenReturn(tagList);
//
//        //When
//        List<Tag> result = tagService.findAll();
//
//        //Then
//        assertEquals(tagList, result);
//        verify(tagDao, times(1)).findAll();
//    }
//
//    @Test
//    public void findTagsByCertificateId_existentCertificateId_shouldReturnNonEmptyList() {
//        //Given
//        Tag tagOne = new Tag(1, "tagOne");
//        Tag tagTwo = new Tag(2, "tagTwo");
//        Tag tagThree = new Tag(3, "tagThree");
//        List<Tag> tagList = Arrays.asList(tagOne, tagTwo, tagThree);
//        Mockito.when(tagDao.findByCertificateId(anyLong())).thenReturn(tagList);
//        Mockito.when(certificateDao.find(anyLong()))
//                .thenReturn(Optional.of(new Certificate()));
//
//        //When
//        List<Tag> result = tagService.findTagsByCertificateId(anyLong());
//
//        //Then
//        verify(tagDao, times(1)).findByCertificateId(anyLong());
//        assertEquals(tagList, result);
//    }
}

package com.epam.esm.certificate;

import static org.mockito.ArgumentMatchers.any;

public class CertificateServiceTest {
//    @InjectMocks
//    CertificateService certificateService;
//    @Mock
//    CertificateDao certificateDao;
//    @Mock
//    TagDao tagDao;
//    @Mock
//    CertificateTagDao certificateTagDao;
//    @Spy
//    ModelMapper modelMapper;
//
//    @Before
//    public void init() {
//        MockitoAnnotations.initMocks(this);
//    }
//
//    @Test
//    public void create_certificateWithTags_shouldEndWithoutException() {
//        //Given
//        List<Certificate> certificatesDB = new ArrayList<>();
//        Tag tagOne = new Tag(1, "tagOne");
//        Tag tagTwo = new Tag(2, "tagTwo");
//        List<Tag> tagsInDB = Arrays.asList(tagOne, tagTwo);
//        Mockito.when(certificateTagDao.create(anyLong(), anyLong())).thenReturn(true);
//        Mockito.when(tagDao.findByName(anyString())).thenAnswer(invocation -> {
//            String tagName = invocation.getArgument(0);
//            return tagsInDB.stream()
//                    .filter(tag -> tag.getName().equals(tagName))
//                    .findFirst();
//        });
//        CertificateDto certificateDto = new CertificateDto("name", "description", new BigDecimal("12.6"),
//                5, Arrays.asList("tagOne", "tagTwo"));
//        Mockito.when(certificateDao.create(any(Certificate.class))).thenAnswer(invocation -> {
//            Certificate certificate = invocation.getArgument(0);
//            certificate.setId(1);
//            certificatesDB.add(certificate);
//            return certificate;
//        });
//
//        //When
//        certificateService.create(certificateDto);
//
//        //Then
//        assertEquals(1,certificatesDB.size());
//        verify(certificateDao, times(1)).create(any(Certificate.class));
//        verify(tagDao, times(2)).findByName(anyString());
//        verify(certificateTagDao, times(2)).create(anyLong(), anyLong());
//    }
//
//    @Test(expected = ServiceConflictException.class)
//    public void create_certificateWithDuplicateName_shouldThrowException() {
//        CertificateDto certificateDto = new CertificateDto("name", "description", new BigDecimal("12.6"),
//                5, Collections.emptyList());
//        Mockito.when(certificateTagDao.create(anyLong(), anyLong())).thenReturn(true);
//        Mockito.when(tagDao.findByName(anyString())).thenReturn(Optional.empty());
//        Mockito.when(certificateDao.create(any(Certificate.class)))
//                .thenThrow(new DuplicateKeyException("message"));
//
//        certificateService.create(certificateDto);
//    }
//
//    @Test
//    public void update_certificateWithUpdatedName_shouldEndWithoutException() {
//        //Given
//        Certificate certificateBeforeUpdate = new Certificate(1, "name", "description",
//                new BigDecimal("12.6"), LocalDateTime.now(), LocalDateTime.now().plusDays(1),
//                5);
//        List<Certificate> certificatesInDB = Collections.singletonList(certificateBeforeUpdate);
//        Mockito.when(certificateDao.update(any(Certificate.class))).thenAnswer(invocation -> {
//            Certificate certificate = invocation.getArgument(0);
//            certificatesInDB.forEach(item -> {
//                if(item.getId() == certificate.getId()) {
//                    item = certificate;
//                }
//            });
//            return true;
//        });
//        CertificateDto certificateDto = new CertificateDto(1, "updated name", "description",
//                new BigDecimal("12.6"), LocalDateTime.now(), LocalDateTime.now().plusDays(1),
//                5, Arrays.asList("tagOne", "tagThree"));
//        Certificate certificate = modelMapper.map(certificateDto, Certificate.class);
//        Tag tagOne = new Tag(1, "tagOne");
//        Tag tagTwo = new Tag(2, "tagTwo");
//        List<Tag> pastCertificateTags = Arrays.asList(tagOne, tagTwo);
//        List<Tag> tagsInDB = Arrays.asList(tagOne, tagTwo);
//        Mockito.when(tagDao.create(any(Tag.class))).thenAnswer(invocation -> {
//            Tag tag = invocation.getArgument(0);
//            tag.setId(3);
//            return tag;
//        });
//        Mockito.when(tagDao.findByCertificateId(1)).thenReturn(pastCertificateTags);
//        Mockito.when(certificateTagDao.create(anyLong(), anyLong())).thenReturn(true);
//        Mockito.when(certificateTagDao.delete(anyLong(), anyLong())).thenReturn(true);
//        Mockito.when(tagDao.findByName(anyString())).thenAnswer(invocation -> {
//            String tagName = invocation.getArgument(0);
//            return tagsInDB.stream()
//                    .filter(tag -> tag.getName().equals(tagName))
//                    .findFirst();
//        });
//        certificateBeforeUpdate.setName("updated name");
//
//        //When
//        certificateService.update(certificateDto);
//
//        //Then
//        assertEquals(certificateBeforeUpdate, certificatesInDB.get(0));
//        verify(certificateDao, times(1)).update(certificate);
//        verify(tagDao, times(1)).findByCertificateId(1);
//        verify(certificateTagDao, times(1)).delete(1,2);
//        verify(tagDao,times(1)).findByName("tagThree");
//        verify(tagDao, times(1)).create(any(Tag.class));
//        verify(certificateTagDao, times(1)).create(1,3);
//    }
//
//    @Test
//    public void delete_certificateWithCorrectId_shouldEndWithoutException() {
//        //Given
//        Mockito.when(certificateTagDao.delete(anyLong(),anyLong())).thenReturn(true);
//        Mockito.when(certificateDao.delete(1)).thenAnswer((invocation)-> {
//            return (Long) invocation.getArgument(0) == 1;
//        });
//
//        //When
//        certificateService.delete(1);
//
//        //Then
//        verify(certificateTagDao,times(1)).deleteByCertificateId(anyLong());
//        verify(certificateDao, times(1)).delete(1);
//    }
//
//    @Test(expected = CertificateNotFoundException.class)
//    public void delete_certificateWithNonexistentId_shouldEndWithException() {
//        //Given
//        Mockito.when(certificateTagDao.delete(anyLong(),anyLong())).thenReturn(true);
//        Mockito.when(certificateDao.delete(1)).thenAnswer((invocation)-> {
//            return (Long) invocation.getArgument(0) == 1;
//        });
//
//        //When
//        certificateService.delete(2);
//
//        //Then
//        verify(certificateTagDao,times(1)).deleteByCertificateId(anyLong());
//        verify(certificateDao, times(1)).delete(2);
//    }
//
//    @Test
//    public void find_correctCertificateId_shouldReturnCertificate() {
//        //Given
//        Certificate certificate = new Certificate(1, "name", "description",
//                new BigDecimal("12.6"), LocalDateTime.now(), null, 12);
//        Tag tagOne = new Tag(1, "tagOne");
//        Tag tagTwo = new Tag(2, "tagTwo");
//        List<Tag> certificateTags = Arrays.asList(tagOne, tagTwo);
//        Mockito.when(certificateDao.find(1)).thenReturn(Optional.of(certificate));
//        Mockito.when(tagDao.findByCertificateId(1)).thenReturn(certificateTags);
//        List<String> tagsInDto = certificateTags.stream()
//                .map(Tag::getName)
//                .collect(Collectors.toList());
//        CertificateDto expected = new CertificateDto(1, "name", "description",
//                new BigDecimal("12.6"), LocalDateTime.now(), null, 12, tagsInDto);
//        Mockito.when(modelMapper.map(certificate, CertificateDto.class)).thenReturn(expected);
//
//        //When
//        CertificateDto result = certificateService.find(1);
//
//        //Then
//        assertEquals(expected, result);
//        verify(certificateDao, times(1)).find(1);
//    }
//
//    @Test(expected = CertificateNotFoundException.class)
//    public void find_nonexistentCertificateId_shouldEndWithException() {
//        //Given
//        Mockito.when(certificateDao.find(1)).thenReturn(Optional.empty());
//
//        //When
//        CertificateDto result = certificateService.find(1);
//
//        //Then
//        verify(certificateDao, times(1)).find(1);
//    }
//
//    @Test()
//    public void findCertificates_tagNameAndDescription_shouldReturnCertificatesList() {
//        //Given
//        Certificate certificate = new Certificate(1, "name", "description",
//                new BigDecimal("12.6"), LocalDateTime.now(), null, 12);
//        Mockito.when(certificateDao.findCertificates(anyString(), any(MapSqlParameterSource.class)))
//                .thenReturn(Collections.singletonList(certificate));
//        Mockito.when(tagDao.findByCertificateId(1)).thenReturn(Collections.emptyList());
//        List<CertificateDto> expected = Stream.of(certificate)
//                .map(item -> modelMapper.map(item, CertificateDto.class))
//                .collect(Collectors.toList());
//
//        //When
//        List<CertificateDto> result =  certificateService.findCertificates("tagName",
//                "descriptionPart",null);
//
//        //Then
//        assertEquals(expected, result);
//        verify(certificateDao,times(1)).findCertificates(anyString(),
//                any(MapSqlParameterSource.class));
//    }
//
//    @Test(expected = IllegalArgumentException.class)
//    public void findCertificates_invalidOrderByParameter_shouldThrowException() {
//        certificateService.findCertificates(null, null, "Invalid");
//    }
//
//    @Test
//    public void addCertificateTag_newTag_shouldEndWithoutException() {
//        //Given
//        Tag tagOne = new Tag(1, "tagOne");
//        Mockito.when(tagDao.findByName("tagOne")).thenReturn(Optional.of(tagOne));
//        Mockito.when(certificateTagDao.create(anyLong(), anyLong())).thenReturn(true);
//
//        //When
//        certificateService.addCertificateTag(tagOne,1);
//
//        //Then
//        verify(tagDao, times(1)).findByName("tagOne");
//        verify(certificateTagDao, times(1)).create(anyLong(), anyLong());
//    }
}

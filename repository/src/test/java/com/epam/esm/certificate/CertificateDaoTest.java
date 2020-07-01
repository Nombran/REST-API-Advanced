package com.epam.esm.certificate;

public class CertificateDaoTest {
//    CertificateDao certificateDao;
//    EmbeddedDatabase embeddedDatabase;
//
//    @Before
//    public void init() {
//        embeddedDatabase = new EmbeddedDatabaseBuilder()
//                .generateUniqueName(true)
//                .setType(H2)
//                .setScriptEncoding("UTF-8")
//                .ignoreFailedDrops(true)
//                .addScript("schema.sql")
//                .addScripts("insert-data.sql")
//                .build();
//        certificateDao = new CertificateDao(embeddedDatabase);
//    }
//
//    @Test
//    public void create_uniqueCertificate_shouldReturnWithIdAndCreationDate() {
//        //Given
//        Certificate certificate = new Certificate("name", "description",
//                new BigDecimal("12.6"), 12);
//
//        //When
//        Certificate expected = certificateDao.create(certificate);
//
//        //Then
//        assertTrue(expected.getId() > 0);
//        assertNotNull(expected.getCreationDate());
//    }
//
//    @Test
//    public void update_existentCertificateWithUpdates_shouldReturnTrue() {
//        //Given
//        Certificate certificate = certificateDao.find(1).get();
//        certificate.setName("new name");
//        certificate.setPrice(new BigDecimal("9.99"));
//
//        //When
//        boolean result = certificateDao.update(certificate);
//
//        //Then
//        Certificate updated = certificateDao.find(1).get();
//        assertTrue(result);
//        assertEquals(updated.getName(), "new name");
//        assertNotNull(updated.getModificationDate());
//        assertEquals(updated.getPrice(), new BigDecimal("9.99"));
//    }
//
//    @Test
//    public void delete_existentCertificateId_shouldReturnTrue() {
//        //Given
//        int expectedSize = certificateDao.findAll().size() - 1;
//
//        //When
//        boolean result = certificateDao.delete(4);
//
//        //Then
//        int actualSize = certificateDao.findAll().size();
//        assertTrue(result);
//        assertEquals(expectedSize, actualSize);
//    }
//
//    @Test(expected = DataIntegrityViolationException.class)
//    public void delete_certificateWithTags_shouldThrowException() {
//        //When
//        certificateDao.delete(1);
//    }
//
//    @Test
//    public void find_existentCertificateId_shouldReturnExpected() {
//        //Given
//        LocalDateTime expectedDateTime = LocalDateTime.of(2020, 6, 9, 0, 0);
//        Certificate expected = new Certificate(1, "certificate one", "description",
//                new BigDecimal("12.5"), expectedDateTime, null, 5);
//
//        //When
//        Certificate result = certificateDao.find(1).get();
//
//
//        //Then
//        assertEquals(expected, result);
//    }
//
//    @Test
//    public void findAll_shouldReturnNonEmptyList() {
//        //Given
//        int expectedListSize = 4;
//
//        //When
//        List<Certificate> certificates = certificateDao.findAll();
//
//        //Then
//        int actualListSize = certificates.size();
//        assertEquals(expectedListSize, actualListSize);
//    }
//
//    @Test
//    public void findCertificates_tagName_shouldReturnOneTag() {
//        //Given
//        String tagName = "fifth tag";
//        String query = new CertificateSearchSqlBuilder("fifth tag", null,null).getSqlQuery();
//        MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource()
//                .addValue("tag_name", tagName)
//                .addValue("perPage", 50)
//                .addValue("page", 0);
//        LocalDateTime expectedDateTime = LocalDateTime.of(2021,9,17,10,10);
//        Certificate expectedCertificate = new Certificate(3, "certificate three", "third row",
//                new BigDecimal("2.5"), expectedDateTime, null, 18);
//        int expectedResultSize = 1;
//
//        //When
//        List<Certificate> result = certificateDao.findCertificates(query, mapSqlParameterSource);
//
//        //Then
//        assertEquals(expectedResultSize, result.size());
//        assertEquals(expectedCertificate.getName(), result.get(0).getName());
//    }
}

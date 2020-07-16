package com.epam.esm.certificatetag;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

public class CertificateTagDaoTest {
//    TagDao tagDao;
//    CertificateTagDao certificateTagDao;
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
//        certificateTagDao = new CertificateTagDao(embeddedDatabase);
//        tagDao = new TagDao(embeddedDatabase);
//    }
//
//    @Test
//    public void create_existentTagIdAndCertificateId_shouldReturnNonEmptyList() {
//        //Given
//        int sizeBeforeCreate = tagDao.findByCertificateId(3).size();
//
//        //When
//        boolean result = certificateTagDao.create(3, 1);
//
//        //Then
//        int actualSize = tagDao.findByCertificateId(3).size();
//        assertTrue(result);
//        assertEquals(sizeBeforeCreate + 1, actualSize);
//    }
//
//    @Test
//    public void delete_existentCertificateIdAndTagId_shouldDeleteFromDB() {
//        //When
//        certificateTagDao.delete(1,1);
//
//        //Then
//        Optional<Tag> result = tagDao.findByIdAndCertificateId(1, 1);
//        assertFalse(result.isPresent());
//    }
//
//    @Test
//    public void deleteByCertificateId_certificateId_findByCertificateShouldBeEmpty() {
//        //When
//        certificateTagDao.deleteByCertificateId(1);
//
//        //Then
//        boolean result = tagDao.findByCertificateId(1).size() == 0;
//        assertTrue(result);
//    }
}

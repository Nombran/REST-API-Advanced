package com.epam.esm.tag;

import com.epam.esm.tag.dao.TagDao;
import com.epam.esm.tag.model.Tag;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType.H2;

public class TagDaoTest {
    TagDao tagDao;
    EmbeddedDatabase embeddedDatabase;

    @Before
    public void init() {
        embeddedDatabase = new EmbeddedDatabaseBuilder()
                .generateUniqueName(true)
                .setType(H2)
                .setScriptEncoding("UTF-8")
                .ignoreFailedDrops(true)
                .addScript("schema.sql")
                .addScripts("insert-data.sql")
                .build();
        tagDao = new TagDao(embeddedDatabase);
    }

    @Test
    public void create_nonexistentTag_shouldReturnCreatedTag() {
        //Given
        String expected = "tag for test";
        Tag tag = new Tag(expected);

        //When
        Tag result = tagDao.create(tag);

        //Then
        assertTrue(result.getId() > 0);
        assertEquals(expected, result.getName());
    }

    @Test
    public void delete_existentTagId_shouldReturnTrue() {
        //Given
        int expected = 5;

        //When
        boolean result = tagDao.delete(6);

        //Then
        int tagNumber = tagDao.findAll().size();
        assertTrue(result);
        assertEquals(expected, tagNumber);
        assertFalse(tagDao.find(6).isPresent());
    }

    @Test
    public void find_existentTagId_shouldReturnNonNullTag() {
        //Given
        Tag expected = new Tag(1, "tag one");

        //When
        Tag result = tagDao.find(1).orElseGet(null);

        //Then
        assertEquals(expected, result);
    }

    @Test
    public void findAll_shouldReturnNonEmptyList() {
        //Given
        int expectedSize = 6;

        //When
        List<Tag> result = tagDao.findAll();

        //Then
        assertEquals(expectedSize, result.size());
    }

    @Test
    public void findByName_nameOfExistentTag_shouldReturnNonNullTag() {
        //Given
        Tag expected = new Tag(1, "tag one");

        //When
        Tag result = tagDao.findByName("tag one").get();

        //Then
        assertEquals(expected, result);
    }

    @Test
    public void findByCertificateId_existentCertificateId_shouldReturnNonEmptyList() {
        //Given
        Tag tagOne = new Tag(3, "tag three");
        Tag tagTwo = new Tag(4, "fourth tag");
        List<Tag> expected = Arrays.asList(tagOne, tagTwo);

        //When
        List<Tag> result = tagDao.findByCertificateId(2);

        //Then
        assertEquals(expected, result);
    }

    @Test
    public void findByNameAndCertificateId_existentTagNameAndCertificateId_shouldReturnNonNullTag() {
        //Given
        Tag expected = new Tag(1, "tag one");

        //When
        Tag result = tagDao.findByIdAndCertificateId(1, 1).get();

        //Then
        assertEquals(expected, result);
    }

    @After
    public void tearDown() {
       embeddedDatabase.shutdown();
    }
}

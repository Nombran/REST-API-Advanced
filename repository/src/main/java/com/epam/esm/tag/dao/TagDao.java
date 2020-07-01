package com.epam.esm.tag.dao;

import com.epam.esm.tag.model.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;


import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;

@Slf4j
@Repository
@Transactional
public class TagDao {
    @PersistenceContext
    private final EntityManager em;

    @Autowired
    public TagDao(EntityManager em) {
        this.em = em;
    }

    public void create(Tag tag) {
        em.persist(tag);
    }

    public void delete(Tag tag) {
        em.remove(tag);
    }

    public Optional<Tag> find(long id) {
        return Optional.ofNullable(em.find(Tag.class, id));
    }

    public List<Tag> findAll() {
        TypedQuery<Tag> query = em.createQuery("Select t from Tag t", Tag.class);
        return query.getResultList();
    }

    public List<Tag> findByCertificateId(long id) {
        TypedQuery<Tag> query = em.createQuery(
                "SELECT t from Tag t inner join t.certificates c where c.id =: id",
                Tag.class);
        query.setParameter("id", id);
        return query.getResultList();
    }

    public Optional<Tag> findByName(String name) {
        TypedQuery<Tag> query = em.createQuery(
                "select t from Tag t where t.name =: name",
                Tag.class);
        query.setParameter("name", name);
        List<Tag> result = query.getResultList();
        Tag tag = result.size() == 0 ?
                null : result.get(0);
        return Optional.ofNullable(tag);
    }

    public Optional<Tag> findByIdAndCertificateId(long id, long certificateId) {
        TypedQuery<Tag> query = em.createQuery(
                "select t from Tag t inner join t.certificates c where c.id =: certificateId and t.id =: tagId",
                Tag.class);
        query.setParameter("certificateId", certificateId);
        query.setParameter("tagId", id);
        Tag tag =  query.getSingleResult();
        return Optional.ofNullable(tag);
    }

}

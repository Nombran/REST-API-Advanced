package com.epam.esm.tag.dao;

import com.epam.esm.certificate.model.Certificate;
import com.epam.esm.tag.model.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;


import javax.persistence.*;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
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

    public List<Tag> findTags(Integer page, Integer perPage) {
        return em.createQuery("select t from Tag t", Tag.class)
                .setFirstResult((page-1) * perPage)
                .setMaxResults(perPage)
                .getResultList();
    }

    public long getCountOfTags() {
        return em.createQuery("select count(t) from Tag t",Long.class).getSingleResult();
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

    public Optional<Tag> GetMostWidelyUsedTagOfAUserWithTheHighestCostOfAllOrders() {
        Query query = em.createNativeQuery("select t.id , t.name from tag t\n" +
                "                              inner join certificate_tag ct on t.id = ct.tag_id\n" +
                "                              inner join certificate c on ct.certificate_id = c.id\n" +
                "                              inner join order_certificate oc on c.id = oc.certificate_id\n" +
                "                              inner join orders o on oc.order_id = o.id\n" +
                "                              inner join users u on o.user_id = u.id\n" +
                "where u.id = (select rm.l from (SELECT u.id l, SUM(o.total_price) SumPrice\n" +
                "                                FROM users u\n" +
                "                                         inner join orders o on u.id = o.user_id\n" +
                "                                         inner join order_certificate on o.id = order_certificate.order_id\n" +
                "                                         inner join certificate c on order_certificate.certificate_id = c.id\n" +
                "                                GROUP BY u.id order by SumPrice desc limit 1) rm)  group by t.id order by count(t.id)\n" +
                "    desc limit 1", Tag.class);
        try {
            Tag tag = (Tag) query.getSingleResult();
            return Optional.ofNullable(tag);
        } catch (NoResultException ex) {
            return Optional.empty();
        }
    }

}

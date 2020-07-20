package com.epam.esm.tag;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.*;
import java.util.List;
import java.util.Optional;

@Slf4j
@Repository
public class TagDao {
    @PersistenceContext
    private final EntityManager em;
    private static final String SQL_FIND_TAGS = "select t from Tag t";
    private static final String SQL_FIND_COUNT_OF_TAGS = "select count(t) from Tag t";
    private static final String SQL_FIND_BY_CERTIFICATE_ID = "SELECT t from Tag t" +
            " inner join t.certificates c where c.id =: id";
    private static final String SQL_FIND_BY_NAME = "select t from Tag t where t.name =: name";
    private static final String SQL_VALUED_TAG = "select t.id , t.name from tag t\n" +
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
            "    desc limit 1";

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
        return em.createQuery(SQL_FIND_TAGS, Tag.class)
                .setFirstResult((page-1) * perPage)
                .setMaxResults(perPage)
                .getResultList();
    }

    public long getCountOfTags() {
        return em.createQuery(SQL_FIND_COUNT_OF_TAGS,Long.class).getSingleResult();
    }

    public List<Tag> findByCertificateId(long id) {
        TypedQuery<Tag> query = em.createQuery(
                SQL_FIND_BY_CERTIFICATE_ID,
                Tag.class);
        query.setParameter("id", id);
        return query.getResultList();
    }

    public Optional<Tag> findByName(String name) {
        TypedQuery<Tag> query = em.createQuery(
                SQL_FIND_BY_NAME,
                Tag.class);
        query.setParameter("name", name);
        List<Tag> result = query.getResultList();
        Tag tag = result.size() == 0 ?
                null : result.get(0);
        return Optional.ofNullable(tag);
    }

    public Optional<Tag> GetValuedUsersMostPopularTag() {
        Query query = em.createNativeQuery(SQL_VALUED_TAG, Tag.class);
        try {
            Tag tag = (Tag) query.getSingleResult();
            return Optional.ofNullable(tag);
        } catch (NoResultException ex) {
            return Optional.empty();
        }
    }

}

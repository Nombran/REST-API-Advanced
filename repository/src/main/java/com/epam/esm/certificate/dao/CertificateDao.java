package com.epam.esm.certificate.dao;

import com.epam.esm.certificate.model.Certificate;
import com.epam.esm.certificate.model.Certificate_;
import com.epam.esm.certificate.specification.CertificateOrderBy;
import com.epam.esm.tag.model.Tag;
import com.epam.esm.tag.model.Tag_;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Slf4j
@Repository
@Transactional
public class CertificateDao {
    @PersistenceContext
    private final EntityManager em;

    @Autowired
    public CertificateDao(EntityManager em) {
        this.em = em;
    }


    public void create(Certificate certificate) {
        LocalDateTime creationDate = LocalDateTime.now();
        certificate.setCreationDate(creationDate);
        em.persist(certificate);
    }

    public void update(Certificate certificate) {
        LocalDateTime modificationDate = LocalDateTime.now();
        certificate.setModificationDate(modificationDate);
        em.merge(certificate);
    }

    public void delete(Certificate certificate) {
        em.remove(certificate);
    }

    public Optional<Certificate> find(long id) {
        return Optional.ofNullable(em.find(Certificate.class, id));
    }

    public List<Certificate> findCertificates(List<String> tagNames, String textPart, String orderBy,
                                              Integer page, Integer perPage) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Certificate> query = cb.createQuery(Certificate.class);
        Root<Certificate> root = query.from(Certificate.class);
        query.select(root);

        prepareSearchQuery(query, root, tagNames, textPart);

        if (Stream.of(CertificateOrderBy.values()).anyMatch(value ->
                value.getOrderByFieldName().equals(orderBy))) {
            query.orderBy(cb.asc(root.get(orderBy)));
        }

        return em.createQuery(query)
                .setFirstResult((page - 1) * perPage)
                .setMaxResults(perPage)
                .getResultList();
    }

    public Long getTotalElementsCountFromCertificateSearch(List<String> tagNames, String textPart) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> query = cb.createQuery(Long.class);
        Root<Certificate> root = query.from(Certificate.class);
        query.select(cb.count(root));

        prepareSearchQuery(query, root, tagNames, textPart);

        return em.createQuery(query)
                .getSingleResult();
    }

    public <X> void prepareSearchQuery(CriteriaQuery<X> query, Root<Certificate> root,
                                       List<String> tagNames, String textPart) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        List<Predicate> predicates = new ArrayList<>();

        if (textPart != null && !textPart.isEmpty()) {
            Predicate predicateForName = cb.like(root.get(Certificate_.NAME), "%" + textPart + "%");
            Predicate predicateForDescription = cb.like(root.get(Certificate_.description), "%" + textPart + "%");
            Predicate textPartPredicate = cb.or(predicateForName, predicateForDescription);
            predicates.add(textPartPredicate);
        }

        if (tagNames != null && tagNames.size() != 0) {
            ListJoin<Certificate, Tag> tagJoin = root.join(Certificate_.tags);
            Expression<Long> countOfCertificateTagsInGroup = cb.count(root);
            Predicate predicateCertificateTagsInInputList = tagJoin.get(Tag_.NAME).in(tagNames);
            predicates.add(predicateCertificateTagsInInputList);
            query.where(cb.and(predicates.toArray(new Predicate[0])))
                    .having(cb.equal(countOfCertificateTagsInGroup, tagNames.size()));
        } else {
            query.where(cb.and(predicates.toArray(new Predicate[0])));
        }

    }
}

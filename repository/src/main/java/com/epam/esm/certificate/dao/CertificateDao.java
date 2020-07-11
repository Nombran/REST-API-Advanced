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
import javax.persistence.metamodel.Metamodel;
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
        return Optional.ofNullable(em.find(Certificate.class,id));
    }

    public List<Certificate> findAll() {
        TypedQuery<Certificate> query = em.createQuery(
                "select c from Certificate c",
                Certificate.class);
        return query.getResultList();
    }

    public List<Certificate> findCertificates(List<String> tagNames, String textPart, String orderBy,
                                              Integer page, Integer perPage) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Certificate> query = cb.createQuery(Certificate.class);
        Root<Certificate> root = query.from(Certificate.class);
        query.select(root);
        List<Predicate> predicates = new ArrayList<>();

        if(textPart != null && !textPart.isEmpty()) {
            Predicate predicateForName = cb.like(root.get(Certificate_.NAME), "%" + textPart + "%");
            Predicate predicateForDescription = cb.like(root.get(Certificate_.description), "%" + textPart + "%");
            Predicate textPartPredicate = cb.or(predicateForName, predicateForDescription);
            predicates.add(textPartPredicate);
        }

        if(Stream.of(CertificateOrderBy.values()).anyMatch(value ->
                value.getOrderByFieldName().equals(orderBy))) {
            query.orderBy(cb.asc(root.get(orderBy)));
        }

        if(tagNames != null && tagNames.size() != 0) {
            ListJoin<Certificate, Tag> tagJoin = root.join(Certificate_.tags);
            Expression<List<Tag>> certificateTags = root.get(Certificate_.tags);
            Expression<Integer> countOfCertificateTags = cb.size(certificateTags);
            Expression<Long> countOfCertificateTagsInGroup = cb.count(root);
            Predicate predicateCountOfCertificateTagsEqualsInputListSize =cb.equal(countOfCertificateTags, tagNames.size());
            Predicate predicateCertificateTagsInInputList = tagJoin.get(Tag_.NAME).in(tagNames);
            predicates.add(predicateCountOfCertificateTagsEqualsInputListSize);
            predicates.add(predicateCertificateTagsInInputList);
            query.where(cb.and(predicates.toArray(new Predicate[0])))
                    .groupBy(root)
                    .having(cb.equal(countOfCertificateTagsInGroup,tagNames.size()));
        } else {
            query.where(cb.and(predicates.toArray(new Predicate[0])));
        }

        return em.createQuery(query)
                .setFirstResult((page-1) * perPage)
                .setMaxResults(perPage)
                .getResultList();
    }
}

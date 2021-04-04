package com.epam.esm.certificate;

import com.epam.esm.tag.Tag;
import com.epam.esm.tag.Tag_;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Repository
public class CertificateDao {
    @PersistenceContext
    private final EntityManager em;
    private static final String SQL_FIND_NON_INACTIVE_CERTIFICATE_BY_NAME = "select c from Certificate c" +
            " where c.status in(:active,:published)" +
            " and c.name=:name";

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

    public Optional<Certificate> find(long id) {
        return Optional.ofNullable(em.find(Certificate.class, id));
    }

    public List<Certificate> findCertificates(CertificateParamWrapper wrapper) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Certificate> query = cb.createQuery(Certificate.class);
        Root<Certificate> root = query.from(Certificate.class);
        query.select(root);

        String[] tagNamesArray = wrapper.getTagNames();
        List<String> tagNames = null;
        if(tagNamesArray != null) {
            tagNames = Arrays.asList(tagNamesArray);
        }

        CertificateStatus[] statusArray = wrapper.getStatuses();
        List<CertificateStatus> statuses = null;
        if(statusArray != null) {
            statuses = Arrays.asList(statusArray);
        }

        String textPart = wrapper.getTextPart();
        String orderBy = wrapper.getOrderBy();
        int page = wrapper.getPage();
        int perPage = wrapper.getPerPage();

        prepareSearchQuery(query, root, tagNames, textPart, statuses);

        if (orderBy != null) {
            query.orderBy(cb.desc(root.get(orderBy)));
        }

        return em.createQuery(query)
                .setFirstResult((page - 1) * perPage)
                .setMaxResults(perPage)
                .getResultList();
    }

    public int getTotalElementsCountFromCertificateSearch(CertificateParamWrapper wrapper) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> query = cb.createQuery(Long.class);
        Root<Certificate> root = query.from(Certificate.class);

        List<String> tagNames = Collections.emptyList();
        String[] tagNamesArray = wrapper.getTagNames();
        if(tagNamesArray != null) {
            tagNames = Arrays.asList(tagNamesArray);
        }

        CertificateStatus[] statusArray = wrapper.getStatuses();
        List<CertificateStatus> statuses = null;
        if(statusArray != null) {
            statuses = Arrays.asList(statusArray);
        }

        String textPart = wrapper.getTextPart();

        prepareSearchQuery(query, root, tagNames, textPart, statuses);

        query.select(cb.countDistinct(root));

        if(tagNames.size() == 0) {
            return em.createQuery(query)
                    .getSingleResult()
                    .intValue();
        } else {
            return em.createQuery(query)
                    .getResultList().size();
        }
    }

    public <X> void prepareSearchQuery(AbstractQuery<X> query, Root<Certificate> root,
                                       List<String> tagNames, String textPart, List<CertificateStatus> statuses) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        List<Predicate> predicates = new ArrayList<>();

        if (textPart != null && !textPart.isEmpty()) {
            Predicate predicateForName = cb.like(cb.lower(root.get(Certificate_.NAME)), "%" + textPart + "%");
            Predicate predicateForDescription = cb.like(root.get(Certificate_.description), "%" + textPart + "%");
            Predicate textPartPredicate = cb.or(predicateForName, predicateForDescription);
            predicates.add(textPartPredicate);
        }

        if(statuses != null && !statuses.isEmpty()) {
            Predicate statusPredicate = root.get(Certificate_.status).in(statuses);
            predicates.add(statusPredicate);
        }

        if (tagNames != null && tagNames.size() != 0) {
            ListJoin<Certificate, Tag> tagJoin = root.join(Certificate_.tags);
            Expression<Long> countOfCertificateTagsInGroup = cb.count(root);
            Predicate predicateCertificateTagsInInputList = tagJoin.get(Tag_.NAME).in(tagNames);
            predicates.add(predicateCertificateTagsInInputList);
            query.where(cb.and(predicates.toArray(new Predicate[0])))
                    .having(cb.equal(countOfCertificateTagsInGroup, tagNames.size()))
                    .groupBy(root);
        } else {
            query.where(cb.and(predicates.toArray(new Predicate[0])));
        }
    }

    public Optional<Certificate> findNonInactiveCertificateByName(String name) {
        TypedQuery<Certificate> typedQuery = em.createQuery(
                SQL_FIND_NON_INACTIVE_CERTIFICATE_BY_NAME,
                Certificate.class);
        typedQuery.setParameter("active", CertificateStatus.ACTIVE);
        typedQuery.setParameter("published", CertificateStatus.PUBLISHED);
        typedQuery.setParameter("name", name);
        try {
            Certificate certificate = typedQuery.getSingleResult();
            return Optional.ofNullable(certificate);
        } catch (NoResultException ex) {
            return Optional.empty();
        }
    }
}

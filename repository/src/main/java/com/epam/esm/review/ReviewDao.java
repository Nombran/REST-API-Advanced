package com.epam.esm.review;

import com.epam.esm.user.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Repository
public class ReviewDao {
    @PersistenceContext
    private final EntityManager em;

    private final String SQL_FIND_BY_USER_ID = "select r from Review r where r.developer.id=:developerId";

    @Autowired
    public ReviewDao(EntityManager em) {
        this.em = em;
    }

    public void create(Review review) {
        LocalDateTime creationDate = LocalDateTime.now();
        review.setReviewDate(creationDate);
        em.persist(review);
    }

    public List<Review> findByDeveloperId(int userId) {
        TypedQuery<Review> query = em.createQuery(SQL_FIND_BY_USER_ID,Review.class);
        query.setParameter("developerId", (long)userId);
        return query.getResultList();
    }

}

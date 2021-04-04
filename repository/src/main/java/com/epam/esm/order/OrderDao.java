package com.epam.esm.order;

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

@Repository
@Slf4j
public class OrderDao {
    @PersistenceContext
    private final EntityManager em;
    private static final String SQL_FIND_ORDER_BY_USER_ID = "select o from Order o where o.user.id =: id order by o.purchaseDate desc ";
    private static final String SQL_COUNT_OF_ORDERS = "select count(o) from Order o where o.user.id =: id";
    private static final String SQL_FIND_BY_USER_ID_AND_ORDER_ID = "select o from Order o where o.id =: orderId " +
            "and o.user.id =: userId";

    @Autowired
    public OrderDao(EntityManager em) {
        this.em = em;
    }

    public void create(Order order) {
        order.setPurchaseDate(LocalDateTime.now());
        em.persist(order);
    }

    public void delete(Order order) {
        em.remove(order);
    }

    public List<Order> getOrdersByUserId(long userId, int page, int perPage) {
        TypedQuery<Order> query = em.createQuery(SQL_FIND_ORDER_BY_USER_ID,
                Order.class);
        query.setParameter("id", userId);
        return query
                .setFirstResult((page-1) * perPage)
                .setMaxResults(perPage)
                .getResultList();
    }

    public Long getCountOfUsersOrders(long userId) {
        TypedQuery<Long> query = em.createQuery(
                SQL_COUNT_OF_ORDERS,
                Long.class);
        query.setParameter("id", userId);
        return query.getSingleResult();
    }

    public Optional<Order> findOrderByUserIdAndOrderId(long userId, long orderId) {
        TypedQuery<Order> query = em.createQuery(
                SQL_FIND_BY_USER_ID_AND_ORDER_ID,
                Order.class);
        query.setParameter("userId", userId);
        query.setParameter("orderId", orderId);
        try {
            return Optional.of(query.getSingleResult());
        } catch (NoResultException ex) {
            return Optional.empty();
        }
    }
}

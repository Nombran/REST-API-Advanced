package com.epam.esm.certificate.specification;

import lombok.Getter;

@Getter
public enum OrderBy {
    ID("id"),
    CREATION_DATE("creation_date"),
    MODIFICATION_DATE("modification_date"),
    NAME("name"),
    DESCRIPTION("description"),
    PRICE("price"),
    DURATION("duration");

    private final String orderByFieldName;

    OrderBy(String orderByFieldName) {
        this.orderByFieldName = orderByFieldName;
    }
}

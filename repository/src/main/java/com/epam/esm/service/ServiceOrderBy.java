package com.epam.esm.service;

import lombok.Getter;

@Getter
public enum ServiceOrderBy {
    ID("id"),
    CREATION_DATE("creationDate"),
    MODIFICATION_DATE("modificationDate"),
    NAME("name"),
    DESCRIPTION("description"),
    PRICE("price"),
    DURATION("duration");

    private final String orderByFieldName;

    ServiceOrderBy(String orderByFieldName) {
        this.orderByFieldName = orderByFieldName;
    }
}

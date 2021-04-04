package com.epam.esm.certificate;

import lombok.Getter;

@Getter
public enum CertificateOrderBy {
    ID("id"),
    CREATION_DATE("creationDate"),
    MODIFICATION_DATE("modificationDate"),
    NAME("name"),
    DESCRIPTION("description"),
    PRICE("price"),
    DURATION("duration");

    private final String orderByFieldName;

    CertificateOrderBy(String orderByFieldName) {
        this.orderByFieldName = orderByFieldName;
    }
}

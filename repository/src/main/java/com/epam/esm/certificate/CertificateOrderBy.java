package com.epam.esm.certificate;

import lombok.Getter;

@Getter
public enum CertificateOrderBy {
    ID("id"),
    CREATION_DATE("creation_date"),
    MODIFICATION_DATE("modification_date"),
    NAME("name"),
    DESCRIPTION("description"),
    PRICE("price"),
    DURATION("duration");

    private final String orderByFieldName;

    CertificateOrderBy(String orderByFieldName) {
        this.orderByFieldName = orderByFieldName;
    }
}

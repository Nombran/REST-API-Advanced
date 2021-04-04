package com.epam.esm.certificate;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class CertificateParamWrapper {
    private final String[] tagNames;
    private final String textPart;
    private final String orderBy;
    private final int page;
    private final int perPage;
    private CertificateStatus[] statuses;
}

package com.epam.esm.certificate;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class CertificateParamWrapper {
    private final String[] tagNames;
    private final String textPart;
    private final String orderBy;
    private final int page;
    private final int perPage;
}

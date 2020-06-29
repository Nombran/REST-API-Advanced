package com.epam.esm.certificate.mapper;

import com.epam.esm.certificate.model.Certificate;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;

public class CertificateMapper implements RowMapper<Certificate> {

    @Override
    public Certificate mapRow(ResultSet resultSet, int i) throws SQLException {
        Certificate certificate = new Certificate();
        certificate.setId(resultSet.getLong("id"));
        certificate.setName(resultSet.getString("name"));
        certificate.setDescription(resultSet.getString("description"));
        certificate.setPrice(resultSet.getBigDecimal("price"));
        certificate.setCreationDate(resultSet.getObject("creation_date", LocalDateTime.class));
        certificate.setModificationDate(resultSet.getObject("modification_date", LocalDateTime.class));
        certificate.setDuration(resultSet.getInt("duration"));
        return certificate;
    }
}

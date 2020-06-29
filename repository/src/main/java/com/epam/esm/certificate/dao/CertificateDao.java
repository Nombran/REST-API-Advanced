package com.epam.esm.certificate.dao;

import com.epam.esm.certificate.mapper.CertificateMapper;
import com.epam.esm.certificate.model.Certificate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Repository
public class CertificateDao {
    private final JdbcTemplate jdbcTemplate;
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private final static String SQL_FIND = "select id, name, description, price," +
            " creation_date, modification_date, duration from certificate where id = ?";
    private final static String SQL_INSERT = "insert into certificate (name, description, price, creation_date," +
            " duration) values (:name, :description, :price, :creation_date, :duration)";
    private final static String SQL_FIND_ALL = "select id, name, description, price," +
            " creation_date, modification_date, duration from certificate";
    private final static String SQL_UPDATE = "update certificate set name = ?, description = ?, price  = ?," +
            "modification_date = ?, duration = ? where id = ?";
    private final static String SQL_DELETE = "delete from certificate where id = ?";

    public CertificateDao(final DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
        namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);
    }

    public Certificate create(Certificate certificate) {
        KeyHolder holder = new GeneratedKeyHolder();
        LocalDateTime creationDate = LocalDateTime.now();
        SqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("name", certificate.getName())
                .addValue("description", certificate.getDescription())
                .addValue("price", certificate.getPrice())
                .addValue("creation_date", creationDate)
                .addValue("duration", certificate.getDuration());
        namedParameterJdbcTemplate.update(SQL_INSERT, parameters, holder, new String[] { "id" });
        certificate.setId(holder.getKey().longValue());
        certificate.setCreationDate(creationDate);
        return certificate;
    }

    public boolean update(Certificate certificate) {
        return jdbcTemplate.update(SQL_UPDATE, certificate.getName(), certificate.getDescription(),
                certificate.getPrice(), LocalDateTime.now(),
                certificate.getDuration(), certificate.getId()) > 0;
    }

    public boolean delete(long id) {
        return jdbcTemplate.update(SQL_DELETE, id) > 0;
    }

    public Optional<Certificate> find(long id) {
        try {
            Certificate certificate = jdbcTemplate.queryForObject(SQL_FIND,
                    new Object[]{id},
                    new CertificateMapper());
            return Optional.ofNullable(certificate);
        }
        catch(EmptyResultDataAccessException e) {
            log.error("Certificate with id " + id + "doesn't exists");
            return Optional.empty();
        }
    }

    public List<Certificate> findAll() {
        return jdbcTemplate.query(SQL_FIND_ALL, new CertificateMapper());
    }

    public List<Certificate> findCertificates(String query, MapSqlParameterSource parameters) {
        return namedParameterJdbcTemplate.query(query, parameters, new CertificateMapper());
    }
}

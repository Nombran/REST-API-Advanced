package com.epam.esm.certificate.specification;
import java.util.stream.Stream;

public class CertificateSearchSqlBuilder {
    private final String tagName;
    private final String textPart;
    private final String orderBy;
    private static final String  SQL_FIND_ALL = "select distinct certificate.id, certificate.name, description, price," +
            " creation_date, modification_date, duration from certificate";
    private static final String SQL_SELECT_JOINS = " left join certificate_tag on certificate.id" +
            " = certificate_tag.certificate_id left join tag t on certificate_tag.tag_id" +
            " = t.id where ";
    private static final String SQL_TAG_NAME_PART = "t.name = (:tag_name) ";
    private static final String SQL_DESCRIPTION_PART = "  search((:textPart), certificate) ";
    private static final String SQL_ORDER_BY_PART = " order by ";
    private static final String SQL_PAGINATION_PART = " limit (:perPage) offset (:page)";

    public CertificateSearchSqlBuilder(String tagName, String textPart,
                                       String orderBy) {
        this.tagName = tagName;
        this.textPart = textPart;
        this.orderBy = orderBy;
    }

    public boolean checkOrderBy() {
        return Stream.of(OrderBy.values()).anyMatch(value ->
                value.getOrderByFieldName().equals(orderBy));
    }

    public String getSqlQuery() {
        String SQL;
        if(tagName == null && textPart == null &&
                !checkOrderBy()) {
            return SQL_FIND_ALL + SQL_PAGINATION_PART;
        } else {
            SQL = SQL_FIND_ALL;
        }
        if(tagName != null) {
            SQL = SQL + SQL_SELECT_JOINS + SQL_TAG_NAME_PART;
            if(textPart != null) {
                SQL = SQL + " and " + SQL_DESCRIPTION_PART;
            }
        } else {
            if(textPart != null) {
                SQL = SQL + SQL_SELECT_JOINS + SQL_DESCRIPTION_PART;
            }
        }
        if(checkOrderBy()) {
            SQL = SQL + SQL_ORDER_BY_PART + orderBy;
        }
        SQL = SQL + SQL_PAGINATION_PART;
        return SQL;
    }
}

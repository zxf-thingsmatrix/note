package com.thingsmatrix.accountCenter.authServer.dao;

import com.google.common.base.CaseFormat;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.persistence.Table;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Component
public class JdbcDao {

    @Resource
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;


    /**
     * @param rows
     * @param updateColumns 存在时需要更新的字段
     * @param <T>
     * @return 返回值中 1表示不存在并插入 2表示存在并更新 0表示存在不更新
     */
    public <T extends Serializable> int[] insertDuplicateKeyUpdate(List<T> rows, List<String> updateColumns) {
        if (CollectionUtils.isEmpty(updateColumns)) return new int[]{};

        String updateClause = updateColumns.stream()
                .filter(StringUtils::isNotBlank).map(e -> String.format("%s=VALUES(%s)", e, e))
                .collect(Collectors.joining(","));

        return insertDuplicateKeyUpdate(rows, updateClause);
    }

    /**
     * @param rows
     * @param updateClause 存在时需要执行的更新子句
     * @param <T>
     * @return 返回值中 1表示不存在并插入 2表示存在并更新 0表示存在不更新
     */
    public <T extends Serializable> int[] insertDuplicateKeyUpdate(List<T> rows, String updateClause) {
        if (CollectionUtils.isEmpty(rows)) return new int[]{};
        Class<? extends Serializable> clazz = rows.get(0).getClass();
        Table table = clazz.getAnnotation(Table.class);
        if (Objects.isNull(table)) return new int[]{};
        String tableName = table.name();

        List<String> fieldName = getFieldName(clazz);
        if (CollectionUtils.isEmpty(fieldName)) return new int[]{};

        String columns = fieldName.stream().map(name -> CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, name)).collect(Collectors.joining(","));
        String values = fieldName.stream().map(name -> ":" + name).collect(Collectors.joining(","));

        String sqlTemplate = "INSERT INTO %s(%s) VALUES (%s) ON DUPLICATE KEY UPDATE %s";
        String sql = String.format(sqlTemplate, tableName, columns, values, updateClause);
        log.info("insertDuplicateKeyUpdate sql:{}", sql);
        SqlParameterSource[] batch = SqlParameterSourceUtils.createBatch(rows.toArray());
        int[] affects = namedParameterJdbcTemplate.batchUpdate(sql, batch);
        return affects;
    }

    /**
     * @param rows
     * @param <T>
     * @return 返回值中 1表示不存在并插入 0表示存在不更新
     */
    public <T extends Serializable> int[] insertIgnore(List<T> rows) {
        if (CollectionUtils.isEmpty(rows)) return new int[]{};
        Class<? extends Serializable> clazz = rows.get(0).getClass();
        Table table = clazz.getAnnotation(Table.class);
        if (Objects.isNull(table)) return new int[]{};
        String tableName = table.name();

        List<String> fieldName = getFieldName(clazz);
        if (CollectionUtils.isEmpty(fieldName)) return new int[]{};

        String columns = fieldName.stream().map(name -> CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, name)).collect(Collectors.joining(","));
        String values = fieldName.stream().map(name -> ":" + name).collect(Collectors.joining(","));

        String sqlTemplate = "INSERT IGNORE INTO %s(%s) VALUES (%s)";
        String sql = String.format(sqlTemplate, tableName, columns, values);
        log.info("insertIgnore sql:{}", sql);
        SqlParameterSource[] batch = SqlParameterSourceUtils.createBatch(rows.toArray());
        int[] affects = namedParameterJdbcTemplate.batchUpdate(sql, batch);
        return affects;
    }

    /**
     * @param rows
     * @param <T>
     * @return 返回值中 1表示不存在并插入 2表示存在并删除旧纪录插入新记录
     */
    public <T extends Serializable> int[] replaceInto(List<T> rows) {
        if (CollectionUtils.isEmpty(rows)) return new int[]{};
        Class<? extends Serializable> clazz = rows.get(0).getClass();
        Table table = clazz.getAnnotation(Table.class);
        if (Objects.isNull(table)) return new int[]{};
        String tableName = table.name();

        List<String> fieldName = getFieldName(clazz);
        if (CollectionUtils.isEmpty(fieldName)) return new int[]{};

        String columns = fieldName.stream().map(name -> CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, name)).collect(Collectors.joining(","));
        String values = fieldName.stream().map(name -> ":" + name).collect(Collectors.joining(","));

        String sqlTemplate = "REPLACE INTO %s(%s) VALUES (%s)";
        String sql = String.format(sqlTemplate, tableName, columns, values);
        log.info("replaceInto sql:{}", sql);
        SqlParameterSource[] batch = SqlParameterSourceUtils.createBatch(rows.toArray());
        int[] affects = namedParameterJdbcTemplate.batchUpdate(sql, batch);
        return affects;
    }

    private List<String> getFieldName(Class<?> clazz) {
        Field[] declaredFields = clazz.getDeclaredFields();
        if (ArrayUtils.isEmpty(declaredFields)) return new ArrayList<>(0);
        return Arrays.stream(declaredFields).map(Field::getName).collect(Collectors.toList());
    }
}

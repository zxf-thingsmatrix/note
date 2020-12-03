package com.thingsmatrix.accountCenter.authServer;

import com.thingsmatrix.accountCenter.authServer.dao.JdbcDao;
import com.thingsmatrix.accountCenter.authServer.domain.UserGroupRel;
import org.apache.commons.lang3.time.DateUtils;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@SpringBootTest
public class JdbcDaoTest {

    @Resource
    private JdbcDao jdbcDao;

    @Test
    public void test_insertDuplicateKeyUpdate() {
        Date now = new Date();
        Date mingtian = DateUtils.addDays(now, 1);
        Date houtian = DateUtils.addDays(now, 2);

        UserGroupRel rel1 = UserGroupRel.builder().id("1").createTime(now).creator("1")
                .companyId("1").groupId("1").userId("1").build();

        UserGroupRel rel2 = UserGroupRel.builder().id("2").createTime(mingtian).creator("1")
                .companyId("1").groupId("1").userId("1").build();

        UserGroupRel rel3 = UserGroupRel.builder().id("3").createTime(houtian).creator("1")
                .companyId("1").groupId("1").userId("1").build();

        List<UserGroupRel> rows = Lists.newArrayList(rel1, rel2, rel3);
        List<String> updateColumns = Lists.newArrayList("create_time");
        int[] ints = jdbcDao.insertDuplicateKeyUpdate(rows, updateColumns);
        System.out.println("===> affects:" + Arrays.toString(ints));
    }

    @Test
    public void test_insertIgnore() {
        Date now = new Date();
        Date mingtian = DateUtils.addDays(now, 1);
        Date houtian = DateUtils.addDays(now, 2);

        UserGroupRel rel1 = UserGroupRel.builder().id("1").createTime(now).creator("1")
                .companyId("1").groupId("1").userId("1").build();

        UserGroupRel rel2 = UserGroupRel.builder().id("2").createTime(mingtian).creator("1")
                .companyId("1").groupId("1").userId("1").build();

        UserGroupRel rel3 = UserGroupRel.builder().id("3").createTime(houtian).creator("1")
                .companyId("1").groupId("1").userId("1").build();

        List<UserGroupRel> rows = Lists.newArrayList(rel1, rel2, rel3);
        int[] ints = jdbcDao.insertIgnore(rows);
        System.out.println("===> affects:" + Arrays.toString(ints));
    }


    @Test
    public void test_replaceInto() {
        Date now = new Date();
        Date mingtian = DateUtils.addDays(now, 1);
        Date houtian = DateUtils.addDays(now, 2);

        UserGroupRel rel1 = UserGroupRel.builder().id("1").createTime(now).creator("1")
                .companyId("1").groupId("1").userId("1").build();

        UserGroupRel rel2 = UserGroupRel.builder().id("2").createTime(mingtian).creator("1")
                .companyId("1").groupId("1").userId("1").build();

        UserGroupRel rel3 = UserGroupRel.builder().id("3").createTime(houtian).creator("1")
                .companyId("2").groupId("2").userId("2").build();

        List<UserGroupRel> rows = Lists.newArrayList(rel1, rel2, rel3);
        int[] ints = jdbcDao.replaceInto(rows);
        System.out.println("===> affects:" + Arrays.toString(ints));
    }
}

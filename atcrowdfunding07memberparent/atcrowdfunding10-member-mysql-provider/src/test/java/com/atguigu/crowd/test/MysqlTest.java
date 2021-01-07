package com.atguigu.crowd.test;

import com.atguigu.crowd.entity.po.MemberPO;
import com.atguigu.crowd.mapper.MemberPOMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.sql.DataSource;
import java.sql.SQLException;

@RunWith(SpringRunner.class)
@SpringBootTest
public class MysqlTest {

    private Logger logger = LoggerFactory.getLogger(MysqlTest.class);

    @Autowired
    private DataSource dataSource;
    @Autowired
    private MemberPOMapper memberPOMapper;

    @Test
    public void test01() throws SQLException {
        /*logger.debug("------------"+dataSource.getConnection().toString()+"------------");
        System.out.println("-------------------");*/
    }

    @Test
    public void insert() {
        /*MemberPO memberPO = new MemberPO(null,"bjw",
                "123456","wangbinjie",
                "123@qq.com",1,1,
                "王彬杰","6666",1);
        memberPOMapper.insert(memberPO);*/
    }

    @Test
    public void  test02(){

    }

}

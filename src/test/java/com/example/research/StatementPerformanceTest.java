package com.example.research;

import org.h2.jdbcx.JdbcDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.temporal.ChronoField;

@ContextConfiguration("classpath:/test-context.xml")
public class StatementPerformanceTest extends AbstractTestNGSpringContextTests {

    @Autowired
    private JdbcDataSource dataSource;
    private static final int INSERT_NUMBER = 100_000;

    @BeforeMethod
    public void setUp() {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(
                     "DROP TABLE IF EXISTS TEST; " +
                             "CREATE TABLE TEST (" +
                             "a1 numeric," +
                             "a2 numeric," +
                             "a3 numeric," +
                             "a4 numeric," +
                             "a5 numeric," +
                             "a6 numeric," +
                             "a7 numeric," +
                             "a8 numeric," +
                             "a9 numeric," +
                             "a10 numeric" +
                             ");")
        ) {
            preparedStatement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void statement_insert_performance() throws SQLException {

        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement()) {
            LocalDateTime before = LocalDateTime.now();
            for (int i = 0; i < INSERT_NUMBER; i++) {
                String x = String.valueOf(i);
                statement.execute(String.format("INSERT INTO TEST VALUES (%s, %s, %s, %s, %s, %s, %s, %s, %s, %s)", x, x, x, x, x, x, x, x, x, x));
            }
            LocalDateTime after = LocalDateTime.now();
            System.out.println("statement_insert_performance: " + INSERT_NUMBER + " in " + (after.getLong(ChronoField.MILLI_OF_DAY) - before.getLong(ChronoField.MILLI_OF_DAY)) + " millis");

            ResultSet resultSet = statement.executeQuery("select count(1) from TEST");
            Assert.assertTrue(resultSet.next());
            Assert.assertEquals(INSERT_NUMBER, resultSet.getInt(1));
        }
    }

    @Test
    public void prepare_statement_insert_performance() throws SQLException {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO TEST VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
             Statement statement = connection.createStatement()) {
            LocalDateTime before = LocalDateTime.now();
            for (int i = 0; i < INSERT_NUMBER; i++) {
                String x = String.valueOf(i);
                preparedStatement.setString(1, x);
                preparedStatement.setString(2, x);
                preparedStatement.setString(3, x);
                preparedStatement.setString(4, x);
                preparedStatement.setString(5, x);
                preparedStatement.setString(6, x);
                preparedStatement.setString(7, x);
                preparedStatement.setString(8, x);
                preparedStatement.setString(9, x);
                preparedStatement.setString(10, x);
                preparedStatement.executeUpdate();
            }
            LocalDateTime after = LocalDateTime.now();
            System.out.println("prepare_statement_insert_performance: " + INSERT_NUMBER + " in " + (after.getLong(ChronoField.MILLI_OF_DAY) - before.getLong(ChronoField.MILLI_OF_DAY)) + " millis");

            ResultSet resultSet = statement.executeQuery("select count(1) from TEST");
            Assert.assertTrue(resultSet.next());
            Assert.assertEquals(INSERT_NUMBER, resultSet.getInt(1));
        }
    }
}
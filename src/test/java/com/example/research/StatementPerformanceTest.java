package com.example.research;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javax.sql.DataSource;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.temporal.ChronoField;

@ContextConfiguration("classpath:/test-context.xml")
public class StatementPerformanceTest extends AbstractTestNGSpringContextTests {

    @Autowired
    private DataSource dataSource;
    private static final int INSERT_NUMBER = 1_000;

    @BeforeMethod
    public void setUp() {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(
                     "CREATE TABLE if not exists TEST (" +
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
                             ");");
             PreparedStatement clearTable = connection.prepareStatement("DELETE FROM TEST;")
        ) {
            preparedStatement.execute();
            clearTable.execute();
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
                preparedStatement.setInt(1, i);
                preparedStatement.setInt(2, i);
                preparedStatement.setInt(3, i);
                preparedStatement.setInt(4, i);
                preparedStatement.setInt(5, i);
                preparedStatement.setInt(6, i);
                preparedStatement.setInt(7, i);
                preparedStatement.setInt(8, i);
                preparedStatement.setInt(9, i);
                preparedStatement.setInt(10, i);
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
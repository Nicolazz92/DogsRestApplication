package com.example.dogsrestapplication.dao;

import com.example.dogsrestapplication.exception.DogNotFoundException;
import com.example.dogsrestapplication.model.Dog;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

public class JdbcDogDao {

    private final DataSource dataSource;

    public JdbcDogDao(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public Dog create(Dog newDog) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {
            connection = dataSource.getConnection();
            connection.setAutoCommit(false);
            preparedStatement = connection.prepareStatement("insert into dog values ( ?, ?, ?, ?, ?, ? )");
            preparedStatement.setString(1, newDog.getId());
            preparedStatement.setString(2, newDog.getName());
            preparedStatement.setDate(3, java.sql.Date.valueOf(newDog.getDateOfBirth()));
            preparedStatement.setInt(4, newDog.getHeight());
            preparedStatement.setInt(5, newDog.getWeight());
            preparedStatement.setString(6, newDog.getCode());
            preparedStatement.executeUpdate();
            connection.commit();
        } catch (SQLException e) {
            if (connection != null) {
                try {
                    connection.rollback();
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
            }
            throw new RuntimeException(e);
        } finally {
            if (preparedStatement != null) {
                try {
                    preparedStatement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return newDog;
    }

    public Collection<Dog> getAll() {
        ArrayList<Dog> result = new ArrayList<>();
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement("select * from dog")
        ) {
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                Dog dog = extractResultSetToDogUtil(resultSet);
                result.add(dog);
                System.out.printf("Got: %s%n", dog);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return result;
    }

    public Optional<Dog> get(String id) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement("select * from dog where id=?");
        ) {
            preparedStatement.setString(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            boolean isNotEmpty = resultSet.next();
            if (isNotEmpty) {
                Dog dog = extractResultSetToDogUtil(resultSet);
                System.out.printf("Got: %s%n", dog);
                return Optional.of(dog);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return Optional.empty();
    }

    public Optional<Dog> replace(String id, Dog newDog) throws DogNotFoundException {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement("update dog set name=?, dateOfBirth=?, height=?, weight=? where id=?")
        ) {
            preparedStatement.setString(1, newDog.getName());
            preparedStatement.setDate(2, java.sql.Date.valueOf(newDog.getDateOfBirth()));
            preparedStatement.setInt(3, newDog.getHeight());
            preparedStatement.setInt(4, newDog.getWeight());
            preparedStatement.setString(5, id);
            int resultInt = preparedStatement.executeUpdate();
            System.out.printf("Updated: %s%n", resultInt);
            if (resultInt == 1) {
                return Optional.of(newDog);
            } else {
                throw new DogNotFoundException(id);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean delete(String id) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement("delete from dog where id=?")
        ) {
            preparedStatement.setString(1, id);
            int resultInt = preparedStatement.executeUpdate();
            System.out.println("Deleted: " + resultInt);
            return resultInt > 0;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private static Dog extractResultSetToDogUtil(ResultSet resultSet) throws SQLException {
        Dog dog = new Dog();
        dog.setId(resultSet.getString("id"));
        dog.setName(resultSet.getString("name"));
        dog.setDateOfBirth(resultSet.getDate("dateOfBirth").toLocalDate());
        dog.setHeight(resultSet.getInt("height"));
        dog.setWeight(resultSet.getInt("weight"));
        return dog;
    }
}

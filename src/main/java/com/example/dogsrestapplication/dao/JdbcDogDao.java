package com.example.dogsrestapplication.dao;

import com.example.dogsrestapplication.exception.DogNotFoundException;
import com.example.dogsrestapplication.model.Dog;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

public class JdbcDogDao {
    //TODO отрубить автокоммит

    private final DataSource dataSource;

    public JdbcDogDao(DataSource dataSource) {
        this.dataSource = dataSource;
        setDDL();
    }

    public void setDDL() {
        String dogDDL =
                "drop table if exists dog;" +
                        "create table dog" +
                        "(" +
                        "    id          varchar(255)," +
                        "    name        varchar(255)," +
                        "    dateOfBirth date," +
                        "    height      numeric," +
                        "    weight      numeric" +
                        ");";
        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement();
        ) {
            statement.executeUpdate(dogDDL);
            System.out.println("Схема обновлена");
        } catch (SQLException e) {
            System.out.println("Схема не обновлена");
            e.printStackTrace();
        }
    }

    public Dog create(Dog newDog) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement("insert into dog values ( ?, ?, ?, ?, ? )")
        ) {
            preparedStatement.setString(1, newDog.getId());
            preparedStatement.setString(2, newDog.getName());
            preparedStatement.setDate(3, java.sql.Date.valueOf(newDog.getDateOfBirth()));
            preparedStatement.setInt(4, newDog.getHeight());
            preparedStatement.setInt(5, newDog.getWeight());
            preparedStatement.executeUpdate();
            return newDog;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
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

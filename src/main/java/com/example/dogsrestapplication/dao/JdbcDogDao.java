package com.example.dogsrestapplication.dao;

import com.example.dogsrestapplication.exception.DogNotFoundException;
import com.example.dogsrestapplication.model.Dog;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

public class JdbcDogDao {
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

    public Optional<Dog> create(Dog newDog) {
        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement();
        ) {
            int resultInt = statement.executeUpdate(String.format("insert into dog values ( '%s', '%s', '%s', %s, %s )",
                    newDog.getId(), newDog.getName(), newDog.getDateOfBirth(), newDog.getHeight(), newDog.getWeight()));
            System.out.println("Inserted: " + resultInt);
            if (resultInt == 1) {
                return Optional.of(newDog);
            } else {
                return Optional.empty();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.ofNullable(newDog);
    }

    public Collection<Dog> getAll() {
        ArrayList<Dog> result = new ArrayList<>();
        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement();
        ) {
            ResultSet resultSet = statement.executeQuery("select * from dog");
            while (resultSet.next()) {
                Dog dog = new Dog();
                extractResultSetToDogUtil(dog, resultSet);
                result.add(dog);
                System.out.printf("Got: %s%n", dog);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    public Optional<Dog> get(String id) throws DogNotFoundException {
        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement();
        ) {
            ResultSet resultSet = statement.executeQuery(String.format("select * from dog where id='%s'", id));
            resultSet.next();
            Dog dog = new Dog();
            extractResultSetToDogUtil(dog, resultSet);
            System.out.printf("Got: %s%n", dog);
            return Optional.of(dog);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    public Optional<Dog> replace(String id, Dog newDog) throws DogNotFoundException {
        Dog result = new Dog();
        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement();
        ) {
            int resultSet = statement.executeUpdate(String.format("update dog set name='%s', dateOfBirth='%s', height=%s, weight=%s where id='%s'",
                    newDog.getName(), newDog.getDateOfBirth(), newDog.getHeight(), newDog.getWeight(), id));
            System.out.printf("Updated: %s%n", resultSet);
            if (resultSet == 1) {
                return Optional.of(result);
            } else  {
                return Optional.empty();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    public void delete(String id) throws DogNotFoundException {
        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement();
        ) {
            int resultInt = statement.executeUpdate(String.format("delete from dog where id='%s'", id));
            System.out.println("Deleted: " + resultInt);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void extractResultSetToDogUtil(Dog dog, ResultSet resultSet) throws SQLException {
        dog.setId(resultSet.getString("id"));
        dog.setName(resultSet.getString("name"));
        dog.setDateOfBirth(resultSet.getDate("dateOfBirth").toLocalDate());
        dog.setHeight(resultSet.getInt("height"));
        dog.setWeight(resultSet.getInt("weight"));
    }
}

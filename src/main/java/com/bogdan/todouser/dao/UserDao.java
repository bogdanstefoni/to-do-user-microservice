package com.bogdan.todouser.dao;

import com.bogdan.todouser.entity.UserEntity;
import com.bogdan.todouser.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserDao {


    private UserRepository repository;

    public List<UserEntity> findAllUsers() {

        return repository.findAll();
    }

    public Optional<UserEntity> findById(Long id) {

        return repository.findById(id);
    }

    public UserEntity createEntity(UserEntity entity) {

        return repository.save(entity);
    }

    public UserEntity updateEntity(UserEntity entity) {

        return repository.save(entity);
    }

    public void deleteEntityById(Long id) {

        repository.deleteById(id);
    }

}

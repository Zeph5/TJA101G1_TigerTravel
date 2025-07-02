package com.manager.repository;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import com.manager.model.Manager;

public interface ManagerRepository extends CrudRepository<Manager, Integer> {

	Optional<Manager> findByAccount(String account);

}

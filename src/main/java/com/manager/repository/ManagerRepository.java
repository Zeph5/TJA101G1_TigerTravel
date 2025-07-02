package com.manager.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;


import com.manager.model.Manager;

public interface ManagerRepository extends JpaRepository<Manager, Integer> {

	Optional<Manager> findByAccount(String account);

}

package com.nam.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.nam.entity.Role;
import com.nam.entity.RoleName;

@Repository
public interface IRoleRepository extends JpaRepository<Role, Long>{
	List<Role> findByName(RoleName name);
}

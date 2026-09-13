package com.yashpal.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.yashpal.model.Address;

public interface AddressRepository extends JpaRepository<Address, Long> {

}

package com.ecommerce.repositories;

import com.ecommerce.models.Address;
import com.ecommerce.payloads.AddressDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AddressRepository extends JpaRepository<Address, Long>{
}

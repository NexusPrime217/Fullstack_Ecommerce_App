package com.ecommerce.repositories;

import com.ecommerce.models.Address;
import com.ecommerce.payloads.AddressDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AddressRepository extends JpaRepository<Address, Long>{
    @Query("SELECT a FROM Address a WHERE a.addressId=?1 AND a.user.userId=?2")
    Optional<Address> findByAddressIdAndUserId(Long addressId, Long userId);
}

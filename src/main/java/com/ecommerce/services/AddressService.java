package com.ecommerce.services;

import com.ecommerce.models.User;
import com.ecommerce.payloads.AddressDTO;
import jakarta.validation.Valid;

import java.util.List;

public interface AddressService {
    AddressDTO createAddress(AddressDTO addressDTO, User user);

    List<AddressDTO> getAllAddresses();

    AddressDTO getAddressById(Long addressId);

    List<AddressDTO> getAddressByUser(User user);

    AddressDTO updateAddress(@Valid AddressDTO addressDTO, Long addressId, Long userId);
}

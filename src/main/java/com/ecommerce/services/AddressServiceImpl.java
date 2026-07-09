package com.ecommerce.services;

import com.ecommerce.exceptions.ResourceNotFoundException;
import com.ecommerce.models.Address;
import com.ecommerce.models.User;
import com.ecommerce.payloads.AddressDTO;
import com.ecommerce.repositories.AddressRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AddressServiceImpl implements AddressService{

    private final AddressRepository addressRepository;
    private final ModelMapper modelMapper;

    @Autowired
    public AddressServiceImpl(AddressRepository addressRepository, ModelMapper modelMapper) {
        this.addressRepository = addressRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public AddressDTO createAddress(AddressDTO addressDTO, User user) {
        Address address = modelMapper.map(addressDTO,Address.class);

        List<Address> addressList = user.getAddresses();
        addressList.add(address);
        user.setAddresses(addressList);

        address.setUser(user);
        Address savedAddress = addressRepository.save(address);
        return modelMapper.map(savedAddress, AddressDTO.class);
    }

    @Override
    public List<AddressDTO> getAllAddresses() {
        List<Address> addresses = addressRepository.findAll();
        List<AddressDTO> addressDTOS = addresses.stream()
                .map(address -> modelMapper.map(address, AddressDTO.class))
                .toList();
        return addressDTOS;
    }

    @Override
    public AddressDTO getAddressById(Long addressId) {
        Address address = addressRepository.findById(addressId)
                .orElseThrow(()->new ResourceNotFoundException("AddressId","Address",addressId));
        return modelMapper.map(address, AddressDTO.class);
    }

    @Override
    public List<AddressDTO> getAddressByUser(User user) {
        List<Address> addresses = user.getAddresses();
        List<AddressDTO> addressDTOList = addresses.stream()
                .map(address -> modelMapper.map(address, AddressDTO.class))
                .toList();

        return addressDTOList;
    }

    @Override
    public AddressDTO updateAddress(AddressDTO addressDTO, Long addressId, Long userId) {
        Address address = addressRepository.findByAddressIdAndUserId(addressId,userId)
                .orElseThrow(()->new ResourceNotFoundException("AddressId","Address",addressId));

        address.setStreet(addressDTO.getStreet());
        address.setBuilding(addressDTO.getBuilding());
        address.setCity(addressDTO.getCity());
        address.setState(addressDTO.getState());
        address.setCountry(addressDTO.getCountry());
        address.setPinCode(addressDTO.getPinCode());

        Address savedAddress = addressRepository.save(address);
        return modelMapper.map(savedAddress, AddressDTO.class);
    }

    @Override
    public String deleteAddress(Long addressId, Long userId) {
        Address address = addressRepository.findByAddressIdAndUserId(addressId,userId)
                .orElseThrow(()->new ResourceNotFoundException("AddressId","Address",addressId));
        addressRepository.delete(address);
        return "Address deleted succesfully!";
    }
}

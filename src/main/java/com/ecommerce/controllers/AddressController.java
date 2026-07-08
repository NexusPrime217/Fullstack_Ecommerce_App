package com.ecommerce.controllers;

import com.ecommerce.models.User;
import com.ecommerce.payloads.AddressDTO;
import com.ecommerce.services.AddressService;
import com.ecommerce.util.AuthUtil;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class AddressController {

    private final AddressService addressService;
    private final AuthUtil authUtil;

    @Autowired
    public AddressController(AddressService addressService, AuthUtil authUtil) {
        this.addressService = addressService;
        this.authUtil = authUtil;
    }

    @PostMapping("/addresses")
    public ResponseEntity<AddressDTO> createAddress(
           @Valid @RequestBody AddressDTO addressDTO
    ){
        User user = authUtil.loggedInUser();
        AddressDTO savedAddressDTO = addressService.createAddress(addressDTO,user);
        return new ResponseEntity<>(savedAddressDTO, HttpStatus.OK);
    }

    @GetMapping("/addresses")
    public ResponseEntity<List<AddressDTO>> getAllAddresses(){
        List<AddressDTO> addressDTOs = addressService.getAllAddresses();
        return new ResponseEntity<>(addressDTOs, HttpStatus.OK);
    }

    @GetMapping("/addresses/{addressId}")
    public ResponseEntity<AddressDTO> getAddressById(
            @PathVariable Long addressId
    ){
        AddressDTO addressDTO = addressService.getAddressById(addressId);
        return new ResponseEntity<>(addressDTO,HttpStatus.OK);
    }

    @GetMapping("users/addresses")
    public ResponseEntity<List<AddressDTO>> getAddressByUser(){
        User user = authUtil.loggedInUser();
        List<AddressDTO> addressDTOs = addressService.getAddressByUser(user);
        return new ResponseEntity<>(addressDTOs, HttpStatus.OK);
    }

    @PutMapping("/addresses/{addressId}")
    public ResponseEntity<AddressDTO> updateAddress(
            @Valid @RequestBody AddressDTO addressDTO,
            @PathVariable Long addressId
    ){
        User user = authUtil.loggedInUser();
        AddressDTO updatedAddressDTO = addressService.updateAddress(addressDTO,addressId);
        return new ResponseEntity<>(updatedAddressDTO, HttpStatus.OK);
    }


}

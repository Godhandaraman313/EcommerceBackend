package com.project.ecommerce.service;

import com.project.ecommerce.dto.AddressRequest;
import com.project.ecommerce.model.Address;
import com.project.ecommerce.repository.AddressRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AddressService {

    private final AddressRepository addressRepository;

    public AddressService(AddressRepository addressRepository) {
        this.addressRepository = addressRepository;
    }

    public Map<String, Object> listForUser(String email) {
        List<Address> addresses = addressRepository.findByEmail(email);

        Address primary = addresses.stream()
                .filter(Address::isDefaultForShipping)
                .findFirst()
                .orElse(addresses.isEmpty() ? null : addresses.get(0));

        Map<String, Object> response = new HashMap<>();
        response.put("addresses", addresses);
        response.put("primary", primary);
        return response;
    }

    public Address getById(Long id, String email) {
        Address address = addressRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Address not found"));
        verifyOwner(address, email);
        return address;
    }

    @Transactional
    public Address create(String email, AddressRequest request) {
        validate(request);

        if (request.isDefaultForShipping()) {
            clearDefaultForUser(email);
        }

        Address address = mapToEntity(new Address(), request);
        address.setEmail(email);
        return addressRepository.save(address);
    }

    @Transactional
    public Address update(Long id, String email, AddressRequest request) {
        validate(request);

        Address existing = getById(id, email);

        if (request.isDefaultForShipping()) {
            clearDefaultForUser(email);
        }

        mapToEntity(existing, request);
        return addressRepository.save(existing);
    }

    @Transactional
    public void delete(Long id, String email) {
        Address address = getById(id, email);
        addressRepository.delete(address);
    }

    @Transactional
    public Address setDefault(Long id, String email) {
        Address address = getById(id, email);
        clearDefaultForUser(email);
        address.setDefaultForShipping(true);
        return addressRepository.save(address);
    }

    private void clearDefaultForUser(String email) {
        addressRepository.findByEmail(email).forEach(a -> {
            if (a.isDefaultForShipping()) {
                a.setDefaultForShipping(false);
                addressRepository.save(a);
            }
        });
    }

    private void verifyOwner(Address address, String email) {
        if (address.getEmail() == null || !address.getEmail().equalsIgnoreCase(email)) {
            throw new RuntimeException("Address not found");
        }
    }

    private void validate(AddressRequest request) {
        if (request.getFirstName() == null || request.getFirstName().isBlank()) {
            throw new RuntimeException("First name is required");
        }
        if (request.getLastName() == null || request.getLastName().isBlank()) {
            throw new RuntimeException("Last name is required");
        }
        if (request.getAddressLine1() == null || request.getAddressLine1().isBlank()) {
            throw new RuntimeException("Address is required");
        }
        if (request.getCity() == null || request.getCity().isBlank()) {
            throw new RuntimeException("City is required");
        }
        if (request.getPostalCode() == null || request.getPostalCode().isBlank()) {
            throw new RuntimeException("Postal code is required");
        }
    }

    private Address mapToEntity(Address address, AddressRequest request) {
        address.setFirstName(request.getFirstName().trim());
        address.setLastName(request.getLastName().trim());
        address.setPhoneNumber(request.getPhoneNumber());
        address.setAddressLine1(request.getAddressLine1().trim());
        address.setAddressLine2(request.getAddressLine2());
        address.setState(request.getState());
        address.setCity(request.getCity().trim());
        address.setCountry(request.getCountry());
        address.setPostalCode(request.getPostalCode().trim());
        address.setDefaultForShipping(request.isDefaultForShipping());
        return address;
    }
}

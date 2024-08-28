package dev.sam.SpringRestApi.service;


import dev.sam.SpringRestApi.exception.ConflictException;
import dev.sam.SpringRestApi.model.Product;
import dev.sam.SpringRestApi.model.Vendor;
import dev.sam.SpringRestApi.repository.VendorRepository;
import dev.sam.SpringRestApi.response.Response;
import dev.sam.SpringRestApi.response.ResponseHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;
import java.util.UUID;
@Slf4j
@Service
public class VendorService {

    private final VendorRepository vendorRepository;
    @Autowired
    public VendorService(VendorRepository vendorRepository) {
        this.vendorRepository = vendorRepository;
    }

    public Vendor CreateVendor(Vendor vendor){
        log.info("Attempting to create vendor with name: {}", vendor.getName());

        Optional<Vendor> existingVendor = vendorRepository.findVendorByName(vendor.getName());
        if (existingVendor.isPresent()) {
            log.warn("Vendor creation failed - Vendor with name: {} already exists", vendor.getName());
            throw new ConflictException(vendor.getName() + " already exists");
        }

        Vendor newVendor = new Vendor();
        newVendor.setName(vendor.getName());
        newVendor.setUuid(UUID.randomUUID().toString());
        newVendor.setEmail(vendor.getEmail());
        newVendor.setPhone_number(vendor.getPhone_number());
        newVendor = vendorRepository.save(newVendor);
        log.info("Vendor created successfully with UUID: {}", newVendor.getUuid());
        return newVendor;

    }
    public String deleteVendor(String uuid){
        Optional<Vendor> existingVendor = vendorRepository.findVendorByUuid(uuid);
        if (existingVendor.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Vendor does not exist");
        }

        Vendor vendor = existingVendor.get();
        vendorRepository.deleteVendorByName(uuid);
        return vendor.getName();
    }
}

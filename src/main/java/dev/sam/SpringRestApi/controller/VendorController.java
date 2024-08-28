package dev.sam.SpringRestApi.controller;

import dev.sam.SpringRestApi.model.Product;
import dev.sam.SpringRestApi.model.Vendor;
import dev.sam.SpringRestApi.response.AuthenticationResponse;
import dev.sam.SpringRestApi.response.Response;
import dev.sam.SpringRestApi.service.VendorService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/vendor")
public class VendorController {
    private final VendorService vendorService;
    @Autowired
    public VendorController(VendorService vendorService) {
        this.vendorService = vendorService;
    }

    @PostMapping("/create")
    public ResponseEntity<Response> createNewVendor(@RequestBody @Valid Vendor vendor) {
        Vendor newVendor = vendorService.CreateVendor(vendor);
        Response response = new Response();
        response.setResponse(newVendor.getName() + " created successfully. With uuid " + newVendor.getUuid());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @DeleteMapping("/{uuid}")
    public ResponseEntity<Response> deleteProduct(@PathVariable String uuid) {
        String vendorName = vendorService.deleteVendor(uuid);
        Response response = new Response();
        response.setResponse( vendorName  + " deleted successfully");
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}

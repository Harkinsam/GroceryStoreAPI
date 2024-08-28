package dev.sam.SpringRestApi.repository;

import dev.sam.SpringRestApi.model.Vendor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface VendorRepository extends JpaRepository<Vendor,Long> {
    Optional<Vendor> findVendorByName(String name);
    Optional<Vendor> findVendorByUuid (String uuid);

    @Modifying
    @Query("DELETE FROM Vendor v WHERE v.name = :name")
    void deleteVendorByName(@Param("name") String name);

}

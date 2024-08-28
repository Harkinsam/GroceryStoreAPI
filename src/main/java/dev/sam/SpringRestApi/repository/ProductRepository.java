package dev.sam.SpringRestApi.repository;

import dev.sam.SpringRestApi.model.Product;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product,Long> {
    Optional<Product> findProductByUuid(String uuid);
    Optional<Product> findProductByName(String name);
    List<Product> findByTag(String tag, Pageable pageable);


}

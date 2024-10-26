package com.examplecode.core.repositories;

import com.examplecode.core.models.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByUserId(String userId); // Trova prodotti per l'utente
}
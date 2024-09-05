package com.javiersanchez.irregularitiesApp.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.javiersanchez.irregularitiesApp.persistence.entity.Zonas;

import java.util.List;

import javax.transaction.Transactional;

public interface ZonaRepository extends JpaRepository<Zonas, Long> {
    List<Zonas> findByUnidad_Id(String unidadId);

    @Transactional
    boolean existsByZonaId(String zonaId);

    @Transactional
    void deleteByZonaId(String zonaId);
}

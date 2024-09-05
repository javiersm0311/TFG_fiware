package com.javiersanchez.irregularitiesApp.persistence.repository;

import org.springframework.data.repository.CrudRepository;
import com.javiersanchez.irregularitiesApp.persistence.entity.Unidad;

public interface UnidadRepository extends CrudRepository<Unidad, String> {
}
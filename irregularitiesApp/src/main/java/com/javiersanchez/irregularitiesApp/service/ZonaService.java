package com.javiersanchez.irregularitiesApp.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.javiersanchez.irregularitiesApp.persistence.entity.Zonas;
import com.javiersanchez.irregularitiesApp.persistence.repository.ZonaRepository;

import java.util.List;

@Service
public class ZonaService {

    @Autowired
    private ZonaRepository zonaRepository;

    public List<Zonas> getZonasByUnidad(String unidadId) {
        return zonaRepository.findByUnidad_Id(unidadId);
    }

    public boolean deleteZona(String zonaId) {
        if (zonaRepository.existsByZonaId(zonaId)) {
            zonaRepository.deleteByZonaId(zonaId);
            return true;
        }
        return false;
    }
}

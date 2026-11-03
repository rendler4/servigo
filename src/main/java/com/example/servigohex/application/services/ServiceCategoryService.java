package com.example.servigohex.application.services;

import com.example.servigohex.application.ports.in.ServiceCategoryUseCase;
import com.example.servigohex.application.ports.out.ServiceCategoryRepositoryPort;
import com.example.servigohex.domain.exceptions.DuplicateCategoryNameException;
import com.example.servigohex.domain.exceptions.ResourceNotFoundException;
import com.example.servigohex.domain.models.ServiceCategory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ServiceCategoryService implements ServiceCategoryUseCase {

    private static final String CACHE_NAME = "categorias";
    private static final String CACHE_KEY_ACTIVAS = "'activas'";

    private final ServiceCategoryRepositoryPort serviceCategoryRepositoryPort;

    public ServiceCategoryService(ServiceCategoryRepositoryPort serviceCategoryRepositoryPort) {
        this.serviceCategoryRepositoryPort = serviceCategoryRepositoryPort;
    }

    @Override
    @Cacheable(value = CACHE_NAME, key = CACHE_KEY_ACTIVAS)
    public List<ServiceCategory> listarActivas() {
        return serviceCategoryRepositoryPort.findAllByActiveTrue();
    }

    @Override
    public ServiceCategory buscarPorId(Long id) {
        return serviceCategoryRepositoryPort.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró la categoría con id " + id));
    }

    @Override
    @CacheEvict(value = CACHE_NAME, key = CACHE_KEY_ACTIVAS)
    public ServiceCategory crear(ServiceCategory categoria) {
        if (serviceCategoryRepositoryPort.existsByName(categoria.getName())) {
            throw new DuplicateCategoryNameException("Ya existe una categoría con el nombre " + categoria.getName());
        }
        categoria.setActive(true);
        return serviceCategoryRepositoryPort.save(categoria);
    }

    @Override
    @CacheEvict(value = CACHE_NAME, key = CACHE_KEY_ACTIVAS)
    public ServiceCategory actualizar(Long id, ServiceCategory categoria) {
        ServiceCategory existente = buscarPorId(id);
        existente.setName(categoria.getName());
        existente.setDescription(categoria.getDescription());
        return serviceCategoryRepositoryPort.save(existente);
    }

    @Override
    @CacheEvict(value = CACHE_NAME, key = CACHE_KEY_ACTIVAS)
    public void desactivar(Long id) {
        ServiceCategory existente = buscarPorId(id);
        existente.setActive(false);
        serviceCategoryRepositoryPort.save(existente);
    }
}

package com.example.servigohex.infrastructure.adapters.in.batch;

import com.example.servigohex.domain.models.Profile;
import com.example.servigohex.infrastructure.adapters.out.persistence.entities.ProfileEntity;
import com.example.servigohex.infrastructure.adapters.out.persistence.mappers.ProfileMapper;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

/**
 * Adaptador de entrada ("in") del step chunk-oriented: convierte la entidad
 * JPA leída por el reader al modelo de dominio, manteniendo el dominio
 * desacoplado de JPA.
 */
@Component
public class ProveedorPendienteItemProcessor implements ItemProcessor<ProfileEntity, Profile> {

    @Override
    public Profile process(ProfileEntity item) {
        return ProfileMapper.INSTANCE.toProfile(item);
    }
}

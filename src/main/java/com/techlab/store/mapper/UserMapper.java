package com.techlab.store.mapper;


import com.techlab.store.dto.ListingDTO;
import com.techlab.store.dto.UserDTO;
import com.techlab.store.dto.UpdateUserDTO;
import com.techlab.store.entity.Listing;
import com.techlab.store.entity.User;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.techlab.store.entity.Client;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface UserMapper {

    // TDOD chequiar que  status se mapea correctamente

    UserDTO toDto(User entity);

    // Nota: Establecer relacion user.client por afuera
    @Mapping(target = "id", ignore = true)
    User toEntity(UserDTO dto);

    @Mapping(target = "id", ignore = true)
    void updateFromDto(UserDTO dto, @MappingTarget User entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "client", ignore = true)
    @Mapping(target = "pendingReviews", ignore = true)
    @Mapping(target = "favorites", ignore = true)
    public User updateFromEntity(User dataToEdit, @MappingTarget User user);



    
}

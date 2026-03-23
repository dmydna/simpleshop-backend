package com.techlab.store.mapper;


import com.techlab.store.dto.ListingDTO;
import com.techlab.store.dto.UserDTO;
import com.techlab.store.entity.Listing;
import com.techlab.store.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface UserMapper {

    @Mapping(source = "client.firstName", target = "clientName")
    UserDTO toDto(User entity);

    // Nota: Establecer relacion user.client por afuera
    @Mapping(target = "client", ignore = true)
    User toEntity(User dto);

    @Mapping(target = "id", ignore = true)
    void updateFromDto(UserDTO dto, @MappingTarget User entity);

}

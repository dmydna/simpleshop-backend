package com.techlab.store.mapper;


import com.techlab.store.dto.UserDTO;
import com.techlab.store.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(source = "client.firstName", target = "clientName")
    UserDTO toDto(User entity);

    // Nota: Establecer relacion user.client por afuera
    @Mapping(target = "client", ignore = true)
    User toEntity(User dto);

}

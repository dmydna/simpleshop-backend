package com.techlab.store.mapper;

import com.techlab.store.dto.ProfileDTO;
import com.techlab.store.dto.UserDTO;
import com.techlab.store.entity.Client;
import com.techlab.store.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ProfileMapper {

    @Mapping(source = "user.username", target = "username")
    @Mapping(source = "user.email", target = "email")
    @Mapping(source = "client.firstName", target = "firstName")
    @Mapping(source = "client.lastName", target = "lastName")
    @Mapping(source = "client.address", target = "address")
    @Mapping(source = "client.phone", target = "phone")
    ProfileDTO toDto(User user, Client client);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "deletedDate", ignore = true)
    @Mapping(target = "client", ignore = true)
    User toUserEntity(ProfileDTO dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "orders", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "deletedDate", ignore = true)
    Client toClientEntity(ProfileDTO dto);


    @Mapping(target = "id", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "client", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "deletedDate", ignore = true)
    void updateUserFromDto(@MappingTarget User user, ProfileDTO dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "deletedDate", ignore = true)
    void updateClientFromDto(@MappingTarget Client client, ProfileDTO dto);
}

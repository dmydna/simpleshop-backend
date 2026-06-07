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

    @Mapping(source = "user.id", target = "id")
    @Mapping(source = "user.username", target = "username")
    @Mapping(source = "user.email", target = "email")
    @Mapping(source = "client.firstName", target = "firstName")
    @Mapping(source = "client.lastName", target = "lastName")
    @Mapping(source = "client.address", target = "address")
    @Mapping(source = "client.phone", target = "phone")
    @Mapping(source = "user.createdAt",    target = "meta.createdAt")
    @Mapping(source = "user.updatedAt",    target = "meta.updatedAt")
    @Mapping(source = "user.deletedAt",    target = "meta.deletedAt")
    @Mapping(source = "user.bannedAt",     target = "meta.bannedAt")
    @Mapping(source = "user.banExpiresAt", target = "meta.banExpiresAt")
    @Mapping(source = "user.banReason",    target = "meta.banReason")
    @Mapping(source = "user.status",       target = "meta.status")
    ProfileDTO toDto(User user, Client client);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "client", ignore = true)
    User toUserEntity(ProfileDTO dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "orders", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    Client toClientEntity(ProfileDTO dto);


    @Mapping(target = "id", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "client", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    void updateUserFromDto(@MappingTarget User user, ProfileDTO dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    void updateClientFromDto(@MappingTarget Client client, ProfileDTO dto);
}

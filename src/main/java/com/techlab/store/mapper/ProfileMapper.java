package com.techlab.store.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import com.techlab.store.dto.ProfileDTO;
import com.techlab.store.entity.Client;
import com.techlab.store.entity.User;
import com.techlab.store.service.HashidService;




@Mapper(componentModel = "spring", uses = {HashidService.class})
public interface ProfileMapper {

    @Mapping(source = "user.id", target = "id", qualifiedByName = "encodeId")
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
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "bannedAt", ignore = true)
    @Mapping(target = "banExpiresAt", ignore = true)
    @Mapping(target = "banReason", ignore = true)
    @Mapping(target = "favorites", ignore = true)
    @Mapping(target = "reviews", ignore = true)
    User toUserEntity(ProfileDTO dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "orders", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Client toClientEntity(ProfileDTO dto);


    // -- UPDATE MAPPINGS -- //
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "client", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "bannedAt", ignore = true)
    @Mapping(target = "banExpiresAt", ignore = true)
    @Mapping(target = "banReason", ignore = true)
    @Mapping(target = "favorites", ignore = true)
    @Mapping(target = "reviews", ignore = true)
    void updateUserFromDto(@MappingTarget User user, ProfileDTO dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "orders", ignore = true)
    void updateClientFromDto(@MappingTarget Client client, ProfileDTO dto);
}

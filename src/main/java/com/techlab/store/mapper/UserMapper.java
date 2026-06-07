package com.techlab.store.mapper;


import com.techlab.store.dto.ListingDTO;
import com.techlab.store.dto.UserDTO;
import com.techlab.store.dto.UpdateUserDTO;
import com.techlab.store.entity.Listing;
import com.techlab.store.entity.User;
import com.techlab.store.utils.EnumUtils;
import com.techlab.store.enums.Status;
import com.techlab.store.enums.UserStatus;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.NullValuePropertyMappingStrategy;


import com.techlab.store.entity.Client;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface UserMapper {


    @Mapping(target = "meta.createdAt", source = "createdAt")
    @Mapping(target = "meta.updatedAt", source = "updatedAt")
    @Mapping(target = "meta.deletedAt", source = "deletedAt")
    @Mapping(target = "meta.bannedAt", source = "bannedAt")
    @Mapping(target = "meta.banExpiresAt", source = "banExpiresAt")
    @Mapping(target = "meta.banReason", source = "banReason")
    @Mapping(target = "meta.status", source = "status")
    UserDTO toDto(User entity);

    // Nota: Establecer relacion user.client por afuera
    @Mapping(target = "createdAt", source = "meta.createdAt" )
    @Mapping(target = "updatedAt",source = "meta.updatedAt")
    @Mapping(target = "deletedAt", source = "meta.deletedAt")
    @Mapping(target = "bannedAt", source = "meta.bannedAt")
    @Mapping(target = "banExpiresAt", source = "meta.banExpiresAt")
    @Mapping(target = "banReason", source = "meta.banReason")
    @Mapping(target = "status", source = "meta.status")
    @Mapping(target = "id", ignore = true)
    User toEntity(UserDTO dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "client", ignore = true)
    @Mapping(target = "favorites", ignore = true)
    public User updateFromEntity(User dataToEdit, @MappingTarget User user);




    
}

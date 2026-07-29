package com.techlab.store.mapper;

import com.techlab.store.dto.ClientDTO;
import com.techlab.store.dto.ClientFullDTO;
import com.techlab.store.dto.RegisterRequest;
import com.techlab.store.entity.Client;
import com.techlab.store.entity.Order;

import java.util.List;

import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.techlab.store.dto.OrderSummary;


@Mapper(componentModel = "spring", uses = {OrderMapper.class}, nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ClientMapper {

    // --- SALIDA (GET) :
    @Named("clientToSimpleDto")
    ClientDTO toSimpleDto(Client entity);

    @Named("clientToFullDto")
    @Mapping(source = "user.username", target = "username")
    @Mapping(source = "user.email", target = "email")
    ClientFullDTO toFullDto(Client entity);

    @Mapping(target = "clientId", ignore = true)
    OrderSummary mapOrder(Order order);

    List<ClientDTO> toDtoList(List<Client> clients);

    @IterableMapping(qualifiedByName = "clientToFullDto")
    List<ClientFullDTO> toFullDtoList(List<Client> clients);

 
    // Para crear un cliente nuevo
    @Mapping(target = "orders", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    Client toEntity(ClientDTO dto);

    @Mapping(target = "orders", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "id", ignore = true)
    Client toEntity(RegisterRequest dto);


    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "orders", ignore = true)
    @Mapping(target = "user", ignore = true)
    public Client updateFromEntity(
        Client update, 
        @MappingTarget Client client);

}

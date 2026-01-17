package com.techlab.store.utils;

import com.techlab.store.dto.ClientDTO;
import com.techlab.store.dto.ClientFullDTO;
import com.techlab.store.dto.OrderDetailDTO;
import com.techlab.store.entity.Client;
import com.techlab.store.entity.OrderDetail;
import org.mapstruct.*;

import java.util.List;


@Mapper(componentModel = "spring", uses = {OrderMapper.class})
public interface ClientMapper {

    // --- SALIDA (GET) :
    @Named("clientToSimpleDto")
    ClientDTO toSimpleDto(Client entity);

    @Named("clientToFullDto")
    ClientFullDTO toFullDto(Client entity);

    List<ClientDTO> toDtoList(List<Client> clients);
    List<ClientFullDTO> toFullDtoList(List<Client> clients);

    // --- ENTRADA (POST/PUT)

    // Se ignora orders para no romper relacion de entidad client->orders

    // Para crear un cliente nuevo
    @Mapping(target = "orders", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    Client toEntity(ClientDTO dto);



    // Para editar un cliente existente
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "orders", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    void updateClientFromDto(ClientDTO dto, @MappingTarget Client entity);

}

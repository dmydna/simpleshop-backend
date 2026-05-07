package com.techlab.store.service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.techlab.store.dto.ClientDTO;
import com.techlab.store.dto.ClientFullDTO;
import com.techlab.store.entity.Client;
import com.techlab.store.entity.User;
import com.techlab.store.mapper.ClientMapper;
import com.techlab.store.repository.ClientRepository;
import com.techlab.store.dto.RegisterRequest;
import com.techlab.store.utils.StringUtils;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import com.techlab.store.specification.ClientSpecifications;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ClientService {

    // TODO mover logica DTO a Controller y dejar solo entities.
    
    @Autowired
    private final ClientRepository clientRepository;
    private final StringUtils stringUtils;
    private final ClientMapper clientMapper;

    public Client create(Client client){
       return clientRepository.save(client);
    }

    public Client create(RegisterRequest request, User savedUser) {
        // 3. Crear la entidad Client (Negocio) vinculada al User
        Client client = clientMapper.toEntity(request);
        client.setUser(savedUser); // Establecemos la relación 1:1
        return clientRepository.save(client);
    }



    public Client getById(Long id) {
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("No encontrado"));
        return client;
    }



    public Page<Client> filter(
            String firstname,
            String lastname,
            Pageable pageable
    ){
        Specification<Client> spec = Specification
                .where(ClientSpecifications.isNotDeleted())
                .and(ClientSpecifications.hasFirstName(firstname))
                .and(ClientSpecifications.hasLastName(lastname));

        return clientRepository.findAll(spec, pageable);
    }


    public boolean isDeleted(Long id){
        Client client = getById(id);
        return client.getDeletedAt() != null;
    }


    // FIXME: eliminar cliente deja a user sin cliente asociado
    public void deleteById(Long id) {
        Client clientEntity = getById(id);
        clientEntity.setDeletedAt(LocalDateTime.now());
        this.clientRepository.save(clientEntity);
    }


    // TODO: Crear UpdateClientDTO
    public Client  updateById(Long id, Client dataToEdit) {
        Client client = getById(id);

        Client updatedClient = clientMapper.updateFromEntity(dataToEdit, client);
        updatedClient.setUpdatedAt(LocalDateTime.now());

        return clientRepository.save(updatedClient);
    }


}

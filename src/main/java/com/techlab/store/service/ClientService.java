package com.techlab.store.service;

import com.techlab.store.dto.ClientDTO;
import com.techlab.store.dto.ClientFullDTO;
import com.techlab.store.utils.ClientMapper;
import com.techlab.store.utils.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import com.techlab.store.repository.ClientRepository;
import com.techlab.store.entity.Client;
import java.util.List;

@Service
public class ClientService {

    @Autowired
    private final ClientRepository clientRepository;
    private final StringUtils stringUtils;

    @Autowired
    private ClientMapper clientMapper;

    public ClientService(ClientRepository clientRepository, StringUtils stringUtils) {
        this.clientRepository = clientRepository;
        this.stringUtils = stringUtils;
    }

    public ClientFullDTO createCliente(ClientFullDTO dto){
       Client newClient = clientMapper.toEntity(dto);
       Client savedClient  = this.clientRepository.save(newClient);
       return clientMapper.toFullDto(savedClient);
    }


    public ClientFullDTO save(ClientDTO dto) {
        if (!dto.getEmail().contains("@")) {
            throw new RuntimeException("Formato de email no valido: ");
        }

        Client newClient = clientMapper.toEntity(dto);
        Client savedClient = clientRepository.save(newClient);
        return clientMapper.toFullDto(savedClient);
    }


    public List<Client> findById() {
        return this.clientRepository.findAll();
    }

    public ClientFullDTO getClientById(Long id) {
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("No encontrado"));
        return this.clientMapper.toFullDto(client);
    }

    public ClientFullDTO editClientById(Long id, ClientFullDTO dto){
        Client clientEntity = clientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));
        this.clientMapper.updateClientFromDto(dto, clientEntity);
        Client savedEntity = clientRepository.save(clientEntity);
        return clientMapper.toFullDto(savedEntity);
    }



    public List<ClientFullDTO> findAllClient(String name){
        List<Client> clientsEntity = null;
        if (!name.isEmpty()){
            clientsEntity = this.clientRepository.findByNameContainingIgnoreCase(name);
        }else{
            clientsEntity = this.clientRepository.findAll();
        }
        return this.clientMapper.toFullDtoList(clientsEntity);

    }

    public ClientFullDTO deleteClientById(Long id) {
        Client clientEntity = clientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));

        clientEntity.setDeleted(true);
        this.clientRepository.save(clientEntity);

        ClientFullDTO dto = this.clientMapper.toFullDto(clientEntity);
        this.clientMapper.updateClientFromDto(dto, clientEntity);
        return dto;
    }

}

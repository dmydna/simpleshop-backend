package com.techlab.store.service;

import com.techlab.store.dto.ClientDTO;
import com.techlab.store.dto.ClientFullDTO;
import com.techlab.store.dto.UserDTO;
import com.techlab.store.entity.User;
import com.techlab.store.mapper.ClientMapper;
import com.techlab.store.utils.RegisterRequest;
import com.techlab.store.utils.StringUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import com.techlab.store.repository.ClientRepository;
import com.techlab.store.entity.Client;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ClientService {

    @Autowired
    private final ClientRepository clientRepository;
    private final StringUtils stringUtils;

    @Autowired
    private ClientMapper clientMapper;

    public ClientFullDTO create(ClientDTO dto){
       Client newClient = clientMapper.toEntity(dto);
       Client savedClient  = this.clientRepository.save(newClient);
       return clientMapper.toFullDto(savedClient);
    }

    public ClientDTO create(RegisterRequest request, User savedUser) {
        // 3. Crear la entidad Client (Negocio) vinculada al User
        Client client = new Client();
        client.setFirstName(request.firstName());
        client.setLastName(request.lastName());
        client.setPhone(request.phone());
        client.setAddress(request.address());
        client.setUser(savedUser); // Establecemos la relación 1:1
        Client savedClient  = this.clientRepository.save(client);
        return clientMapper.toSimpleDto(savedClient);
    }


    public ClientFullDTO save(ClientDTO dto) {

        Client newClient = clientMapper.toEntity(dto);
        Client savedClient = clientRepository.save(newClient);
        return clientMapper.toFullDto(savedClient);
    }


    public ClientFullDTO getById(Long id) {
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("No encontrado"));
        return this.clientMapper.toFullDto(client);
    }



    public List<ClientFullDTO> findAllClient(String name){
        List<Client> clientsEntity = null;
        if (!name.isEmpty()){
            clientsEntity = this.clientRepository.findByFirstNameContainingIgnoreCase(name);
        }else{
            clientsEntity = this.clientRepository.findAll();
        }
        return this.clientMapper.toFullDtoList(clientsEntity);

    }

    public ClientDTO deleteById(Long id) {
        Client clientEntity = clientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));

        clientEntity.setDeleted(true);
        this.clientRepository.save(clientEntity);

        ClientDTO dto = this.clientMapper.toSimpleDto(clientEntity);
        this.clientMapper.updateClientFromDto(dto, clientEntity);
        return dto;
    }


    public ClientDTO  updateById(Long id, ClientDTO dataToEdit) {
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("No encontrado"));

        if (null != dataToEdit.deleted()){
            client.setDeleted(dataToEdit.deleted());
        }

        if (!stringUtils.isEmpty(dataToEdit.lastName())){
            client.setLastName(dataToEdit.lastName());
        }

        Client savedClient = clientRepository.save(client);
        return clientMapper.toSimpleDto(savedClient);
    }


    public Client findEntityById(Long id) {
        Client client = clientRepository.findById(id)
                .orElse(null);
        return client;
    }
}

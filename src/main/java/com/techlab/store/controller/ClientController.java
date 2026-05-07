package com.techlab.store.controller;


import com.techlab.store.dto.ClientDTO;
import com.techlab.store.dto.ClientFullDTO;
import com.techlab.store.service.ClientService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import org.mapstruct.MappingTarget;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;

import com.techlab.store.entity.Client;
import com.techlab.store.mapper.ClientMapper;

@RestController
@RequestMapping("/api/clients")
public class ClientController {

    // DONE: metodos deben retornar ResponseEntity

    @Autowired
    private ClientService clientService;
    private ClientMapper clientMapper;

    // CREATE
    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping
    public ResponseEntity<ClientFullDTO> create(@RequestBody ClientDTO dto) {
        Client entity = clientMapper.toEntity(dto);
        Client savedClient = clientService.create(entity);
        return ResponseEntity.ok(clientMapper.toFullDto(savedClient));
    }

    // GET ALL
    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping
    public  ResponseEntity<Page<ClientFullDTO>>  getAll(
        @RequestParam(required = false) String firstname,
        @RequestParam(required = false) String lastname,
        @PageableDefault(size = 10, sort = "id", direction = Sort.Direction.DESC) Pageable pageable
    ){
        Page<Client> filtered = clientService.filter(firstname, lastname, pageable);
        return ResponseEntity.ok(filtered.map(client -> this.clientMapper.toFullDto(client)));
    }

    // GET
    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<ClientFullDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(clientMapper.toFullDto(clientService.getById(id)));
    }

    // UPDATE
    @PreAuthorize("hasAuthority('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<ClientDTO> updateById(
        @PathVariable Long id, 
        @RequestBody ClientDTO dataToEdit){
        Client client = clientMapper.toEntity(dataToEdit);
        Client updatedClient = clientService.updateById(id, client);
        return ResponseEntity.ok(clientMapper.toSimpleDto(updatedClient));
    }

    // DELETE
    @PreAuthorize("hasAuthority('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void>  deleteById(@PathVariable Long id){
        clientService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

}

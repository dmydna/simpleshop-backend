package com.techlab.store.controller;


import com.techlab.store.dto.ClientDTO;
import com.techlab.store.dto.ClientFullDTO;
import com.techlab.store.service.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/clients")
public class ClientController {
    @Autowired
    private ClientService clientService;

    @PostMapping
    public ClientFullDTO createCliente(@RequestBody ClientFullDTO cliente) {
        return clientService.createCliente(cliente);
    }

    @GetMapping
    public List<ClientFullDTO> getAllClients(
            @RequestParam(required = false, defaultValue = "") String name){
        return this.clientService.findAllClient(name);
    }

    @GetMapping("/{id}")
    public ClientFullDTO getClienteById(@PathVariable Long id) {
        return clientService.getClientById(id);
    }

    @PutMapping("/{id}")
    public ClientFullDTO editClientById(@PathVariable Long id, @RequestBody ClientFullDTO dataToEdit){
        return this.clientService.editClientById(id, dataToEdit);
    }

    @DeleteMapping("/{id}")
    public ClientFullDTO deleteProductById(@PathVariable Long id){
        return this.clientService.deleteClientById(id);
    }

}

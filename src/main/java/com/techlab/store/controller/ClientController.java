package com.techlab.store.controller;


import com.techlab.store.dto.ClientDTO;
import com.techlab.store.dto.ClientFullDTO;
import com.techlab.store.service.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/clients")
public class ClientController {
    @Autowired
    private ClientService clientService;

    @PostMapping
    public ClientFullDTO createCliente(@RequestBody ClientDTO cliente) {
        return clientService.create(cliente);
    }

    @GetMapping
    public List<ClientFullDTO> getAll(
            @RequestParam(required = false, defaultValue = "") String name){
        return this.clientService.findAllClient(name);
    }

    @GetMapping("/{id}")
    public ClientFullDTO getById(@PathVariable Long id) {
        return clientService.getById(id);
    }

    @PutMapping("/{id}")
    public ClientDTO updateById(@PathVariable Long id, @RequestBody ClientDTO dataToEdit){
        return clientService.updateById(id, dataToEdit);
    }

    @DeleteMapping("/{id}")
    public ClientDTO deleteById(@PathVariable Long id){
        return clientService.deleteById(id);
    }

}

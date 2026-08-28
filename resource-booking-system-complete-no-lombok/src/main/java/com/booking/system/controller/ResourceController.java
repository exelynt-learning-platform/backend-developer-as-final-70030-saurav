package com.booking.system.controller;

import com.booking.system.dto.request.ResourceRequest;
import com.booking.system.dto.response.ResourceResponse;
import com.booking.system.service.ResourceService;
import jakarta.validation.Valid;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/resources")
public class ResourceController {

    private final ResourceService resourceService;

    public ResourceController(ResourceService resourceService) {
        this.resourceService = resourceService;
    }

    @PostMapping
    public ResponseEntity<ResourceResponse> create(@Valid @RequestBody ResourceRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(resourceService.create(request));
    }

    @GetMapping
    public List<ResourceResponse> getAll() {
        return resourceService.getAll();
    }

    @GetMapping("/{id}")
    public ResourceResponse getById(@PathVariable Long id) {
        return resourceService.getById(id);
    }

    @PutMapping("/{id}")
    public ResourceResponse update(@PathVariable Long id,
                                   @Valid @RequestBody ResourceRequest request) {
        return resourceService.update(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        resourceService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

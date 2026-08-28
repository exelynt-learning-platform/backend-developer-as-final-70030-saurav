package com.booking.system.service;

import com.booking.system.dto.request.ResourceRequest;
import com.booking.system.dto.response.ResourceResponse;
import com.booking.system.entity.Resource;
import com.booking.system.repository.ResourceRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ResourceService {

    private final ResourceRepository resourceRepository;

    public ResourceService(ResourceRepository resourceRepository) {
        this.resourceRepository = resourceRepository;
    }

    public ResourceResponse create(ResourceRequest request) {
        Resource resource = new Resource();
        resource.setName(request.getName());
        resource.setDescription(request.getDescription());
        resource.setAvailable(request.getAvailable() == null ? true : request.getAvailable());
        resource.setPrice(request.getPrice());
        return map(resourceRepository.save(resource));
    }

    public List<ResourceResponse> getAll() {
        return resourceRepository.findAll().stream().map(this::map).toList();
    }

    public ResourceResponse getById(Long id) {
        return map(find(id));
    }

    public ResourceResponse update(Long id, ResourceRequest request) {
        Resource resource = find(id);
        resource.setName(request.getName());
        resource.setDescription(request.getDescription());
        resource.setAvailable(request.getAvailable());
        resource.setPrice(request.getPrice());
        return map(resourceRepository.save(resource));
    }

    public void delete(Long id) {
        resourceRepository.delete(find(id));
    }

    private Resource find(Long id) {
        return resourceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Resource not found with id: " + id));
    }

    private ResourceResponse map(Resource r) {
        return new ResourceResponse(r.getId(), r.getName(), r.getDescription(),
                r.getAvailable(), r.getPrice());
    }
}

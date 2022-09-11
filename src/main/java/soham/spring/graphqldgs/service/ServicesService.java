package soham.spring.graphqldgs.service;

import org.springframework.beans.factory.annotation.Autowired;
import soham.spring.graphqldgs.entity.Provider;
import soham.spring.graphqldgs.entity.Service;
import soham.spring.graphqldgs.error.NoDataFoundError;
import soham.spring.graphqldgs.repository.ServiceRepository;

import java.util.List;
import java.util.Optional;

@org.springframework.stereotype.Service
public class ServicesService {

    @Autowired
    private ServiceRepository serviceRepository;

    public List<Service> findAllServices() {
        return serviceRepository.findAll();
    }

    public Service findServiceById(String serviceId) {
        Optional<Service> service = serviceRepository.findById(Integer.parseInt(serviceId));

        if(service.isEmpty()) {
            throw new NoDataFoundError("No Service found", "SER-001");
        }
        return service.get();
    }

    public List<Service>  findProviderOfService(Provider provider) {
        return serviceRepository.findByProviderId(provider.getId());
    }

    public Service addService(String name, String description, String providerId) {
        Integer maxId = serviceRepository.maxId();

        return serviceRepository.save(Service.builder().id(maxId+1)
                .name(name).description(description).providerId(Integer.parseInt(providerId))
                .build());
    }
}

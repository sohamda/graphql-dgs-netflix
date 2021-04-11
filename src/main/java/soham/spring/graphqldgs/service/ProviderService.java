package soham.spring.graphqldgs.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import soham.spring.graphqldgs.entity.Provider;
import soham.spring.graphqldgs.error.NoDataFoundError;
import soham.spring.graphqldgs.repository.ProviderRepository;

import java.util.List;
import java.util.Optional;

@Service
public class ProviderService {

    @Autowired
    private ProviderRepository providerRepository;

    public List<Provider> findAllProviders() {
        return providerRepository.findAll();
    }

    public Provider findProviderById(String providerId) {
        Optional<Provider> provider = providerRepository.findById(Integer.parseInt(providerId));

        if(provider.isEmpty()) {
            throw new NoDataFoundError("No Provider found", "PRV-001");
        }
        return provider.get();
    }
}

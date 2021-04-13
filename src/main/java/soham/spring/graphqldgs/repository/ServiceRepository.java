package soham.spring.graphqldgs.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import soham.spring.graphqldgs.entity.Service;

import java.util.List;

public interface ServiceRepository extends JpaRepository<Service, Integer> {

    public List<Service> findByProviderId(Integer providerId);
}

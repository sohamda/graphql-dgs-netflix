package soham.spring.graphqldgs.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import soham.spring.graphqldgs.entity.Provider;

import java.util.List;

public interface ProviderRepository extends JpaRepository<Provider, Integer> {
    public List<Provider> findByIdIn(List<Integer> ids);
}

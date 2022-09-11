package soham.spring.graphqldgs.dataloader;

import com.netflix.graphql.dgs.DgsQueryExecutor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import soham.spring.graphqldgs.entity.Provider;
import soham.spring.graphqldgs.entity.Service;
import soham.spring.graphqldgs.repository.ProviderRepository;
import soham.spring.graphqldgs.repository.ServiceRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
public class ServiceToProviderDataloaderTest {

    @Autowired
    private DgsQueryExecutor dgsQueryExecutor;

    @MockBean
    ServiceRepository serviceRepository;
    @MockBean
    ProviderRepository providerRepository;

    @BeforeEach
    void init() {
        List<Service> services = List.of(new Service(1, "demo1", "demo1 desc", 1),
                new Service(2, "demo2", "demo2 desc", 2),
                new Service(3, "demo3", "demo3 desc", 3),
                new Service(4, "demo4", "demo4 desc", 4));
        lenient().when(serviceRepository.findAll()).thenReturn(services);
        lenient().when(serviceRepository.findById(2))
                .thenReturn(Optional.of(new Service(2, "demo2", "demo2 desc", 2)));
    }

    @Test
    void dataLoaderOnServiceById() {
        when(providerRepository.findByIdIn(anyList())).thenReturn(List.of(new Provider(2, "demo2", "demo2 desc")));
        String providerName = dgsQueryExecutor.executeAndExtractJsonPath(
                " { serviceById(id: 2) { name provider { name } }}",
                "data.serviceById.provider.name");

        assertEquals("demo2", providerName);

        verify(providerRepository, times(0)).findById(anyInt());

        ArgumentCaptor<List<Integer>> providerIds = ArgumentCaptor.forClass(ArrayList.class);
        verify(providerRepository, times(1)).findByIdIn(providerIds.capture());
        assertEquals(1, providerIds.getValue().size());
        assertEquals(2, providerIds.getValue().get(0));
    }

    @Test
    void dataLoaderOnAllServices() {
        List<Provider> providers = List.of(new Provider(1, "demo1", "demo1 desc"),
                new Provider(2, "demo2", "demo2 desc"),
                new Provider(3, "demo3", "demo3 desc"),
                new Provider(4, "demo4", "demo4 desc"));
        when(providerRepository.findByIdIn(anyList())).thenReturn(providers);

        List<String> providerNames = dgsQueryExecutor.executeAndExtractJsonPath(
                " { services { name provider { name } }}",
                "data.services[*].provider.name");

        assertEquals(4, providerNames.size());
        verify(providerRepository, times(0)).findById(anyInt());

        ArgumentCaptor<List<Integer>> providerIds = ArgumentCaptor.forClass(ArrayList.class);
        verify(providerRepository, times(1)).findByIdIn(providerIds.capture());
        assertEquals(4, providerIds.getValue().size());
        assertEquals(List.of(1, 2, 3, 4), providerIds.getValue());
    }
}

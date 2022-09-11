package soham.spring.graphqldgs.datafetcher;

import com.netflix.graphql.dgs.DgsQueryExecutor;
import graphql.ExecutionResult;
import org.intellij.lang.annotations.Language;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import soham.spring.graphqldgs.entity.Provider;
import soham.spring.graphqldgs.entity.Service;
import soham.spring.graphqldgs.repository.ProviderRepository;
import soham.spring.graphqldgs.repository.ServiceRepository;
import soham.spring.graphqldgs.service.ServicesService;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SpringBootTest
public class ServiceDataFetcherTests {

    @Autowired
    private DgsQueryExecutor dgsQueryExecutor;

    @MockBean
    ServiceRepository serviceRepository;
    @MockBean
    ProviderRepository providerRepository;
    @SpyBean
    ServicesService service;

    @BeforeEach
    void init() {
        List<Provider> providers = List.of(new Provider(1, "demo1", "demo1 desc"),
                new Provider(2, "demo2", "demo2 desc"),
                new Provider(3, "demo3", "demo3 desc"),
                new Provider(4, "demo4", "demo4 desc"));
        List<Service> services = List.of(new Service(1, "demo1", "demo1 desc", 1),
                new Service(2, "demo2", "demo2 desc", 2),
                new Service(3, "demo3", "demo3 desc", 3),
                new Service(4, "demo4", "demo4 desc", 4));
        lenient().when(serviceRepository.findAll()).thenReturn(services);
        lenient().when(serviceRepository.findById(2))
                .thenReturn(Optional.of(new Service(2, "demo2", "demo2 desc", 1)));
        lenient().when(serviceRepository.save(any(Service.class)))
                .thenReturn(new Service(5, "demo5", "demo5 desc", 3));
    }

    @Test
    void testServiceById() {
        String serviceName = dgsQueryExecutor.executeAndExtractJsonPath(
                " { serviceById(id: 2) { name id }}",
                "data.serviceById.name");
        assertEquals("demo2", serviceName);
        verify(service, times(1)).findServiceById(eq("2"));
    }

    @Test
    void testNoServiceFound() {
        ExecutionResult providerName = dgsQueryExecutor.execute(" { serviceById(id: 1) { name id }}");
        assertEquals("SER-001 : No Service found", providerName.getErrors().get(0).getMessage());
        verify(service, times(1)).findServiceById(eq("1"));
    }

    @Test
    void testAddService() {
        String serviceName = dgsQueryExecutor.executeAndExtractJsonPath(
                "mutation { addService(name: \"demo5\", description: \"demo5_desc\", providerId: 3) { name }}",
                "data.addService.name");

        assertEquals("demo5", serviceName);
        verify(service, times(1)).addService(eq("demo5"), eq("demo5_desc"), eq("3"));
    }

    @Test
    void testRetrieveServiceNames() {
        List<String> serviceNames = dgsQueryExecutor.executeAndExtractJsonPath(
                " { services { name }}", "data.services[*].name");
        assertTrue(serviceNames.contains("demo3"));
        assertEquals(4, serviceNames.size());
        verify(service, times(1)).findAllServices();
    }

    @ParameterizedTest
    @MethodSource("provideParameters")
    void testGetAllServicesCombination(@Language("GraphQL") String query, String jsonPathExtension, int resultSetCount) {
        List<String> providerNames = dgsQueryExecutor.executeAndExtractJsonPath(
                query, "data."+jsonPathExtension);

        assertEquals(providerNames.size(), resultSetCount);
        verify(service, times(1)).findAllServices();
    }

    private static Stream<Arguments> provideParameters() {
        return Stream.of(
                Arguments.of("{ services { name }}", "services[*].description", 0),
                Arguments.of("{ services { name }}", "services[*].name", 4),
                Arguments.of("{ services { name }}", "services[*].id", 0),
                Arguments.of("{ services { name }}", "services[*].provider", 0)
               /* ,
                Arguments.of("{ services { name provider { id name description } }}", "services[*].provider.name", 1),
                Arguments.of("{ services { name provider { id name description } }}", "services[*].provider.id", 1),
                Arguments.of("{ services { name provider { id name description } }}", "services[0].provider.description", 1),
                Arguments.of("{ services { name provider { id } }}", "services[0].provider.name", 0),
                Arguments.of("{ services { name provider { id } }}", "services[0].provider.description", 0)*/
        );
    }
}

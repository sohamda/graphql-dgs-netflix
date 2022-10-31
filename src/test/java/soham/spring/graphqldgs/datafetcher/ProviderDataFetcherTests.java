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
import soham.spring.graphqldgs.service.ProviderService;
import soham.spring.graphqldgs.service.ServicesService;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SpringBootTest
public class ProviderDataFetcherTests {

    @Autowired
    private DgsQueryExecutor dgsQueryExecutor;

    @MockBean
    private ProviderRepository providerRepository;
    @SpyBean
    ProviderService providerService;
    @MockBean
    private ServicesService service;

    @BeforeEach
    void init() {
        List<Provider> providers = List.of(new Provider(1, "demo1", "demo1 desc"),
                new Provider(2, "demo2", "demo2 desc"),
                new Provider(3, "demo3", "demo3 desc"),
                new Provider(4, "demo4", "demo4 desc"));
        List<Service> services = List.of(new Service(1, "demo1", "demo1 desc", 1),
                new Service(1, "demo2", "demo2 desc", 1),
                new Service(1, "demo3", "demo3 desc", 1),
                new Service(1, "demo4", "demo4 desc", 1));
        lenient().when(providerRepository.findAll()).thenReturn(providers);
        lenient().when(providerRepository.findById(2))
                .thenReturn(Optional.of(new Provider(2, "demo2", "demo2 desc")));
        lenient().when(providerRepository.save(any(Provider.class)))
                .thenReturn(new Provider(5, "demo5", "demo5 desc"));
        lenient().when(service.findProviderOfService(any(Provider.class))).thenReturn(services);
    }

    @Test
    void testRetrieveProviders() {
        List<String> serviceNames = dgsQueryExecutor.executeAndExtractJsonPath(
                " { providers { name services { name } }}",
                "data.providers[0].services[*].name");

        assertEquals(4, serviceNames.size());
        verify(service, times(4)).findProviderOfService(any(Provider.class));
    }

    @Test
    void testGetProviderById() {
        String providerName = dgsQueryExecutor.executeAndExtractJsonPath(
                " { providerById(id: 2) { name id }}",
                "data.providerById.name");
        assertEquals("demo2", providerName);
        verify(providerService, times(1)).findProviderById(eq("2"));
    }

    @Test
    void testNoProviderFound() {
        ExecutionResult providerName = dgsQueryExecutor.execute(" { providerById(id: 1) { name id }}");
        assertEquals("PRV-001 : No Provider found", providerName.getErrors().get(0).getMessage());
        verify(providerService, times(1)).findProviderById(eq("1"));
    }

    @Test
    void testAddProvider() {
        String providerName = dgsQueryExecutor.executeAndExtractJsonPath(
                "mutation { addProvider(name:\"demo5\", description:\"demo5_desc\") { name }}",
                "data.addProvider.name");

        assertEquals("demo5", providerName);
        verify(providerService, times(1)).addProvider(eq("demo5"), eq("demo5_desc"));
    }

    @Test
    void testRetrieveProviderNames() {
        List<String> providerNames = dgsQueryExecutor.executeAndExtractJsonPath(
                " { providers { name }}", "data.providers[*].name");
        assertTrue(providerNames.contains("demo3"));
        assertEquals(4, providerNames.size());
        verify(providerService, times(1)).findAllProviders();
    }

    @ParameterizedTest
    @MethodSource("provideParameters")
    void testGetAllProvidersCombination(@Language("GraphQL") String query, String jsonPathExtension, int resultSetCount) {
        List<String> providerNames = dgsQueryExecutor.executeAndExtractJsonPath(
                query, "data."+jsonPathExtension);

        assertEquals(providerNames.size(), resultSetCount);
        verify(providerService, times(1)).findAllProviders();
    }

    private static Stream<Arguments> provideParameters() {
        return Stream.of(
                Arguments.of("{ providers { name }}", "providers[*].description", 0),
                Arguments.of("{ providers { name }}", "providers[*].name", 4),
                Arguments.of("{ providers { name }}", "providers[*].id", 0),
                Arguments.of("{ providers { name }}", "providers[*].services", 0),
                Arguments.of("{ providers { name services { id name description } }}", "providers[0].services[*].name", 4),
                Arguments.of("{ providers { name services { id name description } }}", "providers[0].services[*].id", 4),
                Arguments.of("{ providers { name services { id name description } }}", "providers[0].services[*].description", 4),
                Arguments.of("{ providers { name services { id } }}", "providers[0].services[*].name", 0),
                Arguments.of("{ providers { name services { id } }}", "providers[0].services[*].description", 0)
        );
    }

}

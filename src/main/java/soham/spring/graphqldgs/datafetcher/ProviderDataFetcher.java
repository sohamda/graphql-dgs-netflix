package soham.spring.graphqldgs.datafetcher;

import com.netflix.graphql.dgs.DgsComponent;
import com.netflix.graphql.dgs.DgsData;
import com.netflix.graphql.dgs.DgsDataFetchingEnvironment;
import com.netflix.graphql.dgs.InputArgument;
import graphql.schema.DataFetchingEnvironment;
import org.springframework.beans.factory.annotation.Autowired;
import soham.spring.graphqldgs.entity.Provider;
import soham.spring.graphqldgs.entity.Service;
import soham.spring.graphqldgs.service.ProviderService;
import soham.spring.graphqldgs.service.ServicesService;

import java.util.List;

@DgsComponent
public class ProviderDataFetcher {

    @Autowired
    private ProviderService providerService;
    @Autowired
    private ServicesService service;

    @DgsData(parentType = "Query", field = "providers")
    public List<Provider> getAllProviders() {
        return providerService.findAllProviders();
    }

    @DgsData(parentType = "Query", field = "providerById")
    public Provider providerById(@InputArgument("id") String id) {
        return providerService.findProviderById(id);
    }

    @DgsData(parentType = "Provider", field = "services")
    public List<Service> services(DgsDataFetchingEnvironment dfe) {
        Provider provider = dfe.getSource();
        return service.findProviderOfService(provider);
    }

    @DgsData(parentType = "Mutation", field = "addProvider")
    public Provider addProvider(DataFetchingEnvironment dataFetchingEnvironment) {
        String name = dataFetchingEnvironment.getArgument("name");
        String description = dataFetchingEnvironment.getArgument("description");

        return providerService.addProvider(name, description);
    }
}

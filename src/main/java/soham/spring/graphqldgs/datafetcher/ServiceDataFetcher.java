package soham.spring.graphqldgs.datafetcher;

import com.netflix.graphql.dgs.DgsComponent;
import com.netflix.graphql.dgs.DgsData;
import com.netflix.graphql.dgs.DgsDataFetchingEnvironment;
import com.netflix.graphql.dgs.InputArgument;
import graphql.schema.DataFetchingEnvironment;
import org.dataloader.DataLoader;
import org.springframework.beans.factory.annotation.Autowired;
import soham.spring.graphqldgs.dataloader.ProviderDataLoader;
import soham.spring.graphqldgs.entity.Provider;
import soham.spring.graphqldgs.entity.Service;
import soham.spring.graphqldgs.service.ServicesService;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@DgsComponent
public class ServiceDataFetcher {

    @Autowired
    private ServicesService service;

    @DgsData(parentType = "Query", field = "services")
    public List<Service> getAllServices() {
        return service.findAllServices();
    }

    @DgsData(parentType = "Query", field = "serviceById")
    public Service serviceById(@InputArgument("id") String id) {
        return service.findServiceById(id);
    }

    @DgsData(parentType = "Service", field = "provider")
    public CompletableFuture<Provider> provider(DgsDataFetchingEnvironment dfe) {
        //DataLoader<Integer, Provider> dataLoader = dfe.getDataLoader("providers");
        DataLoader<Integer, Provider> dataLoader = dfe.getDataLoader(ProviderDataLoader.class);
        Service service = dfe.getSource();
        return dataLoader.load(service.getProviderId());
    }

    @DgsData(parentType = "Mutation", field = "addService")
    public Service addService(DataFetchingEnvironment dataFetchingEnvironment) {
        String name = dataFetchingEnvironment.getArgument("name");
        String description = dataFetchingEnvironment.getArgument("description");
        String providerId = dataFetchingEnvironment.getArgument("providerId");

        return service.addService(name, description, providerId);
    }

}

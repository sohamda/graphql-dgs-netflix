package soham.spring.graphqldgs.datafetcher;

import com.netflix.graphql.dgs.DgsComponent;
import com.netflix.graphql.dgs.DgsData;
import com.netflix.graphql.dgs.DgsDataFetchingEnvironment;
import com.netflix.graphql.dgs.InputArgument;
import org.springframework.beans.factory.annotation.Autowired;
import soham.spring.graphqldgs.entity.Provider;
import soham.spring.graphqldgs.entity.Service;
import soham.spring.graphqldgs.service.ServicesService;

import java.util.List;

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
    public Provider provider(DgsDataFetchingEnvironment dfe) {
        Service service = dfe.getSource();
        return service.getProvider();
    }

}

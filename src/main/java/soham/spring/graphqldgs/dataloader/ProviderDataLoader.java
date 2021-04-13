package soham.spring.graphqldgs.dataloader;

import com.netflix.graphql.dgs.DgsDataLoader;
import org.dataloader.BatchLoader;
import org.springframework.beans.factory.annotation.Autowired;
import soham.spring.graphqldgs.entity.Provider;
import soham.spring.graphqldgs.service.ProviderService;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

@DgsDataLoader(name = "providers")
public class ProviderDataLoader implements BatchLoader<Integer, Provider> {

    @Autowired
    private ProviderService providerService;

    @Override
    public CompletionStage<List<Provider>> load(List<Integer> keys) {
        return CompletableFuture.supplyAsync(() -> providerService.findAllProvidersByIds(keys));
    }
}

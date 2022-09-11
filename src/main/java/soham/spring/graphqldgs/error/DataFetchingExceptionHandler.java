package soham.spring.graphqldgs.error;

import com.netflix.graphql.types.errors.TypedGraphQLError;
import graphql.execution.DataFetcherExceptionHandler;
import graphql.execution.DataFetcherExceptionHandlerParameters;
import graphql.execution.DataFetcherExceptionHandlerResult;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Component
public class DataFetchingExceptionHandler implements DataFetcherExceptionHandler {

    @Override
    public CompletableFuture<DataFetcherExceptionHandlerResult> handleException(DataFetcherExceptionHandlerParameters params) {
        if(params.getException() instanceof NoDataFoundError noDataFoundError) {
            Map<String, Object> debugInfo = new HashMap<>();
            debugInfo.put("errorCode", noDataFoundError.getErrorCode());

            TypedGraphQLError graphqlError = TypedGraphQLError
                    .newInternalErrorBuilder()
                    .message(noDataFoundError.getMessage())
                    .debugInfo(debugInfo)
                    .path(params.getPath()).build();
            DataFetcherExceptionHandlerResult result = DataFetcherExceptionHandlerResult.newResult()
                    .error(graphqlError).build();
            return CompletableFuture.completedFuture(result);
        } else {
            return DataFetcherExceptionHandler.super.handleException(params);
        }
    }
}

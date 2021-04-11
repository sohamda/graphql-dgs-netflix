package soham.spring.graphqldgs.error;

import com.netflix.graphql.dgs.exceptions.DefaultDataFetcherExceptionHandler;
import com.netflix.graphql.types.errors.TypedGraphQLError;
import graphql.execution.DataFetcherExceptionHandler;
import graphql.execution.DataFetcherExceptionHandlerParameters;
import graphql.execution.DataFetcherExceptionHandlerResult;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class DataFetchingExceptionHandler implements DataFetcherExceptionHandler {

    private final DefaultDataFetcherExceptionHandler defaultHandler = new DefaultDataFetcherExceptionHandler();

    @Override
    public DataFetcherExceptionHandlerResult onException(DataFetcherExceptionHandlerParameters dataFetcherExceptionHandlerParameters) {
        if(dataFetcherExceptionHandlerParameters.getException() instanceof NoDataFoundError) {

            NoDataFoundError noDataFoundError = (NoDataFoundError)dataFetcherExceptionHandlerParameters.getException();

            Map<String, Object> debugInfo = new HashMap<>();
            debugInfo.put("errorCode", noDataFoundError.getErrorCode());

            TypedGraphQLError graphqlError = TypedGraphQLError.newInternalErrorBuilder().message(noDataFoundError.getMessage())
                    .debugInfo(debugInfo)
                    .path(dataFetcherExceptionHandlerParameters.getPath()).build();
            return DataFetcherExceptionHandlerResult.newResult()
                    .error(graphqlError)
                    .build();
        } else {
            return defaultHandler.onException(dataFetcherExceptionHandlerParameters);
        }
    }
}

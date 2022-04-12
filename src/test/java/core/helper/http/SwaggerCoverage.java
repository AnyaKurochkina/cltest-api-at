package core.helper.http;

import com.swagger.coverage.CoverageOutputWriter;
import com.swagger.coverage.FileSystemOutputWriter;
import io.swagger.models.Operation;
import io.swagger.models.Swagger;
import io.swagger.models.parameters.PathParameter;
import io.swagger.models.parameters.QueryParameter;

import java.nio.file.Paths;

import static com.swagger.coverage.SwaggerCoverageConstants.OUTPUT_DIRECTORY;
import static io.swagger.models.Scheme.forValue;
import static java.lang.String.valueOf;

public class SwaggerCoverage {
    private CoverageOutputWriter writer;

    public SwaggerCoverage(CoverageOutputWriter writer) {
        this.writer = writer;
    }

    public SwaggerCoverage() {
        this.writer = new FileSystemOutputWriter(Paths.get(OUTPUT_DIRECTORY));
    }

    public Response filter(Response response, Http http) {
        Operation operation = new Operation();

//        http.getPathParams().forEach((n, v) -> operation.addParameter(new PathParameter().name(n).example(v)));

//        try {
//            http.getQueryParams().forEach((n, v) -> operation.addParameter(new QueryParameter().name(n).example(v)));
//        } catch (ClassCastException ex) {
//            http.getQueryParams().keySet().forEach(n -> operation.addParameter(new QueryParameter().name(n)));
//        }
//        try {
//            requestSpec.getFormParams().forEach((n, v) -> operation.addParameter(new FormParameter().name(n).example(v)));
//        } catch (ClassCastException ex) {
//            requestSpec.getFormParams().keySet().forEach((n -> operation.addParameter(new FormParameter().name(n))));
//        }
//        //end
//        requestSpec.getHeaders().forEach(header -> operation.addParameter(new HeaderParameter().name(header.getName())
//                .example(header.getValue())));
//
//        requestSpec.getMultiPartParams().forEach(multiPartSpecification -> operation.addParameter(new FormParameter()
//                .name(multiPartSpecification.getControlName())));
//
//        if (Objects.nonNull(requestSpec.getBody())) {
//            operation.addParameter(new BodyParameter().name(BODY_PARAM_NAME));
//        }

        operation.addResponse(valueOf(response.status()), new io.swagger.models.Response());
        Swagger swagger = new Swagger()
                .scheme(forValue("http"))
                .host(http.host)
                .consumes(http.contentType)
                .produces(http.contentType)
                .path(http.path, new io.swagger.models.Path().set(http.method, operation));

        writer.write(swagger);
        return response;
    }
}

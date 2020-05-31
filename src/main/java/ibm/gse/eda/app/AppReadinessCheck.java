package ibm.gse.eda.app;

import javax.enterprise.context.ApplicationScoped;

import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.Readiness;

@Readiness
@ApplicationScoped
public class AppReadinessCheck implements HealthCheck {
    
    private static final String readinessCheck = OrderManagementResource.class.getSimpleName()
                                               + " Readiness Check";
    @Override
  public HealthCheckResponse call() {
    if (!System.getProperty("wlp.server.name").equals("defaultServer")) {
      return HealthCheckResponse.down(readinessCheck);
    }
    return HealthCheckResponse.up(readinessCheck);
  }
}
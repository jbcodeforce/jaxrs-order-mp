package ibm.gse.eda.app.domain;

import java.util.Collection;
import java.util.Optional;
import java.util.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.eclipse.microprofile.metrics.MetricUnits;
import org.eclipse.microprofile.metrics.annotation.Counted;
import org.eclipse.microprofile.metrics.annotation.Gauge;
import org.eclipse.microprofile.metrics.annotation.Timed;

import ibm.gse.eda.app.infrastructure.OrderRepository;

@ApplicationScoped
public class OrderService {
	private static final Logger logger = Logger.getLogger(OrderService.class.getName());

	@Inject
	private OrderRepository repository;
	
	public OrderService() {
	}
	
	public OrderService(OrderRepository repository) {
		this.repository = repository;
	}
	
	@Counted(name = "newOrderAccessCount",
		absolute = true,
		description = "Number of times a new order method is requested")
	public OrderEntity createOrder(OrderEntity order) {
		return repository.save(order);
	}

	public Optional<OrderEntity>  getOrderById(String orderID) {
		return  Optional.ofNullable(repository.getById(orderID));
	}

	@Counted(name = "orderAccessCount",
			absolute = true,
			description = "Number of times the list of orders method is requested")
	@Gauge(unit = MetricUnits.NONE,
			name = "ordersSizeGauge",
			absolute = true,
			description = "Number of order in the orders datasource")
	@Timed(name = "orderProcessingTime",
			tags = {"method=list"},
			absolute = true,
			description = "Time needed to process the orders")
	public Collection<OrderEntity> getAllPersistedOrders() {
		return repository.getAll();
	}

}

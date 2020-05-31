package ibm.gse.eda.app.domain;

import java.util.Collection;
import java.util.Optional;
import java.util.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

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
	
	public OrderEntity createOrder(OrderEntity order) {
		return repository.save(order);
	}

	public Optional<OrderEntity>  getOrderById(String orderID) {
		return  Optional.ofNullable(repository.getById(orderID));
	}

	public Collection<OrderEntity> getAllPersistedOrders() {
		return repository.getAll();
	}

}

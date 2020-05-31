package ibm.gse.eda.app.infrastructure;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import javax.enterprise.context.ApplicationScoped;

import ibm.gse.eda.app.domain.Address;
import ibm.gse.eda.app.domain.OrderEntity;

@ApplicationScoped
public class OrderRepository {

    private final Map<String, OrderEntity> orders;

    public OrderRepository() {
        orders = new ConcurrentHashMap<>();
        Address destinationAddress = new Address("Main Street", "San Jose", "Santa Clara", "CA", "94000");
		OrderEntity order = new OrderEntity("1","P01","AFarmer",100, 
				destinationAddress,  OrderEntity.COMPLETED_STATUS);
        orders.put("1",order);
    }
    
	public OrderEntity save(OrderEntity order) {
        if (order != null && order.getOrderID() == null) {
            UUID uuid = UUID.randomUUID();
            order.setOrderID(uuid.toString());
        }
        orders.put(order.getOrderID(),order);
		return order;
	}

	public OrderEntity getById(String orderID) {
		return orders.get(orderID);
    }
    
    public Collection<OrderEntity> getAll() {
        return Collections.unmodifiableCollection(orders.values());
    }
    
}
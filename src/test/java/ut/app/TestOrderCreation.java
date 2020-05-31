package ut.app;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import ibm.gse.eda.app.domain.OrderEntity;
import ibm.gse.eda.app.domain.OrderService;
import ibm.gse.eda.app.infrastructure.OrderRepository;

public class TestOrderCreation {
	static OrderService orderService;
	@BeforeAll
	public static void setUpBeforeClass() throws Exception {
		OrderRepository repo = new OrderRepository();
		orderService = new OrderService(repo);
	}

	@Test
	public void shouldGetOrderId() {
		OrderEntity order = OrderTestDataFactory.orderFixtureWithoutIdentity();
		OrderEntity orderOut = orderService.createOrder(order);
		assertTrue(orderOut.getOrderID().length() > 0);
		assertEquals("City",orderOut.getDeliveryAddress().getCity());
	}

	public void shouldGetOrderByIdLookup() {
		OrderEntity order = OrderTestDataFactory.orderFixtureWithoutIdentity();
		OrderEntity orderOut = orderService.createOrder(order);
		assertTrue(orderOut.getOrderID().length() > 0);
		Optional<OrderEntity> oo =  orderService.getOrderById(orderOut.getOrderID());
		assertEquals(orderOut.getOrderID(),oo.get().getOrderID());
	}

}

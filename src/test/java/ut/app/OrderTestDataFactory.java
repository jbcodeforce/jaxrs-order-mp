package ut.app;

import ibm.gse.eda.app.domain.Address;
import ibm.gse.eda.app.domain.OrderEntity;

public class OrderTestDataFactory {

	public static OrderEntity orderFixtureWithoutIdentity() {
		
		Address destinationAddress = new Address("Street", "City", "DestinationCountry", "State", "Zipcode");
		OrderEntity order = new OrderEntity(null,"P01","AFarmer",100, 
				destinationAddress,  OrderEntity.PENDING_STATUS);
		return order;
	}
}

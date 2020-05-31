package ibm.gse.eda.app;

import java.util.Collection;
import java.util.Optional;
import java.util.logging.Logger;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;

import ibm.gse.eda.app.domain.OrderEntity;
import ibm.gse.eda.app.domain.OrderFactory;
import ibm.gse.eda.app.domain.OrderService;
import ibm.gse.eda.app.dto.OrderParameters;

@Path("/orders")
@RequestScoped
public class OrderManagementResource {
	private static final Logger logger = Logger.getLogger(OrderManagementResource.class.getName());

	@Inject
	@ConfigProperty(name="app.message")
	private String message;
	
	@Inject
	private OrderService orderService;
	
	public OrderManagementResource(){
	
	}
	
	public OrderService getOrCreateOrderService() {
		/*
		 * if (orderService == null) {
		 
			orderService = new OrderService();
		}
		*/
		return orderService;
	}
	
	@GET
	@Path("hello")
    @Produces({ MediaType.TEXT_PLAIN })
    public Response getMessage() {
       return  Response.ok(message).build();
	}
	
	@GET
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Returns order references with order_id, customer_id and product_id", description = "")
    @APIResponses(value = {
            @APIResponse(responseCode = "404", description = "No records found", content = @Content(mediaType = "text/plain")),
            @APIResponse(responseCode = "200", description = "OK", content = @Content(mediaType = "application/json")) })
    public Response getAllOrders() {
		Collection<OrderEntity> orderList = getOrCreateOrderService().getAllPersistedOrders();
        if (! orderList.isEmpty()) {
        	return Response.ok().entity(orderList).build();
        } else {
        	 return Response.status(Status.NOT_FOUND).build();

        }
	}
	
	@GET
	@Path("{Id}")
	@Produces(MediaType.APPLICATION_JSON)
	@Operation(summary = "Query an order by id", description = "")
    @APIResponses(value = {
            @APIResponse(responseCode = "404", description = "Order not found", content = @Content(mediaType = "text/plain")),
            @APIResponse(responseCode = "200", description = "Order found", content = @Content(mediaType = "application/json")) })
	public Response getOrderById(@PathParam("Id") String id) {
		Optional<OrderEntity> order = getOrCreateOrderService().getOrderById(id);
        if (order.isPresent()) {
            return Response.ok().entity(order.get()).build();
        } else {
            return Response.status(Status.NOT_FOUND).build();
        }
	 }

	@POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces({MediaType.APPLICATION_JSON})
    @Operation(summary = "Request to create an order", description = "")
    @APIResponses(value = {
            @APIResponse(responseCode = "400", description = "Bad create order request", content = @Content(mediaType = MediaType.TEXT_PLAIN)),
            @APIResponse(responseCode = "200", description = "Order created, return order unique identifier", content = @Content(mediaType = MediaType.APPLICATION_JSON)) })
	public Response createOrder(OrderParameters orderParameters) {
		OrderEntity orderOut = null;
		if (orderParameters == null ) {
			return Response.status(400, "No parameter sent").build();
		}
		
		logger.info("process order for " + orderParameters.getCustomerID());
		OrderEntity order = OrderFactory.createNewOrder(orderParameters);
		try {
			
			orderOut = getOrCreateOrderService().createOrder(order);
		} catch(Exception e) {
			return Response.serverError().build();
		}
		
	    return Response.ok().entity(orderOut).build();
	}
}

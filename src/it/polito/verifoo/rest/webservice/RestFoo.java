package it.polito.verifoo.rest.webservice;

import java.net.MalformedURLException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import com.microsoft.z3.Status;

import io.swagger.annotations.*;
import it.polito.verifoo.rest.common.BadGraphError;
import it.polito.verifoo.rest.common.Translator;
import it.polito.verifoo.rest.common.VerifooProxy;
import it.polito.verifoo.rest.common.VerifooSerializer;
import it.polito.verifoo.rest.jaxb.ApplicationError;
import it.polito.verifoo.rest.jaxb.EType;
import it.polito.verifoo.rest.jaxb.Graph;
import it.polito.verifoo.rest.jaxb.NFV;
import it.polito.verifoo.rest.jaxb.PName;
import it.polito.verifoo.rest.jaxb.Property;
import it.polito.verigraph.mcnet.components.IsolationResult;

/**
 * 
 * This class implements the web service that deals with the deployment requests
 *
 */
@Path("/deployment")
@Api("/deployment")
public class RestFoo {
	    @POST
	    @ApiOperation(value = "Tests and Deploys a network model", notes = "This is the main API of the Verifoo Service. It provides, for each graph, the verification of the validity of the network model and the optimised deployment on the hosts.",
	    response=NFV.class)	    
	    @ApiResponses(value = {
	    	    		@ApiResponse(code = 200, message = "OK", response=NFV.class),
	    	    		@ApiResponse(code = 400, message = "Something wrong with the client request",response=ApplicationError.class),
	    	    		@ApiResponse(code = 415, message = "Invalid Media Type"),
	    	    		@ApiResponse(code = 500, message = "Something wrong with server", response=ApplicationError.class),
	    				@ApiResponse(code = 503, message = "Service temporarily unavailable")})
	    @Consumes(MediaType.APPLICATION_XML)
		@Produces(MediaType.APPLICATION_XML)
	    public NFV put(@Context HttpServletRequest req,@ApiParam(value = "Complete or Tiny Response")@DefaultValue("true")@QueryParam("complete") Boolean complete,@ApiParam(value = "Network Schema", required = true) NFV root) throws MalformedURLException {
				String z3model = new String();
				VerifooSerializer test = new VerifooSerializer(root);
				if(complete!=true) {
					root.getHosts().getHost().removeIf((h)->!h.isActive());
					root.getConnections().getConnection().removeIf((c)->{
						return !(
							root.getHosts().getHost().stream().filter((h)->h.getName().equals(c.getSourceHost())).findFirst().isPresent()
							&&
							root.getHosts().getHost().stream().filter((h)->h.getName().equals(c.getDestHost())).findFirst().isPresent()
						);
					});						
				}
				return root;
	    }

}
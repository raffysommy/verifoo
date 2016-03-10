package it.polito.escape.verify.exception;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import it.polito.escape.verify.model.ErrorMessage;

@Provider
public class BadRequestExceptionManager implements ExceptionMapper<BadRequestException>{

	@Override
	public Response toResponse(BadRequestException exception) {
		ErrorMessage errorMessage = new ErrorMessage(exception.getMessage(), 400, "http://www.polito.it");
		return Response.status(Status.BAD_REQUEST)
				.entity(errorMessage)
				.build();
	}

}
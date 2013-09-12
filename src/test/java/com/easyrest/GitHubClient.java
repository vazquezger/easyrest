package com.easyrest;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/")
public interface GitHubClient {

	@GET
	@Path("/users")
	@Produces(MediaType.APPLICATION_JSON)
	public String getUsers();
	
	@GET
	@Path("/users/{username}")
	@Produces(MediaType.APPLICATION_JSON)
	public String getUserByUsername(@PathParam("username") String username);
		
}

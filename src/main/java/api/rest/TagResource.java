package api.rest;

import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;
import javax.transaction.Transactional;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import api.dao.TagDAO;
import api.model.Tag;

/**
 * A resource for processing requests related to Tags
 */
@RequestScoped
@Path("system/tags")
@Produces(MediaType.APPLICATION_JSON)
public class TagResource {

	@Inject
	private TagDAO tagDAO;

	@GET
	@Transactional
	public Response getTags() {

		List<Tag> allTags = tagDAO.readAllTags();

		JsonObjectBuilder wrapper = Json.createObjectBuilder();
		JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();

		allTags.forEach(tag -> {
			arrayBuilder.add(tag.getTag());
		});

		wrapper.add("tags", arrayBuilder.build());

		return Response.ok(wrapper.build()).build();
	}

}
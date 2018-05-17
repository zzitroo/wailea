package com.intuit.cg.backendtechassessment.controller;

import java.util.Collection;
import java.util.UUID;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;
import com.intuit.cg.backendtechassessment.controller.requestmappings.RequestMappings;
import com.intuit.cg.backendtechassessment.configuration.Marketplace;
import com.intuit.cg.backendtechassessment.exceptions.NoBidFoundException;
import com.intuit.cg.backendtechassessment.persistence.MarketplaceDAO;
import com.intuit.cg.backendtechassessment.dataobjects.Bid;
import com.intuit.cg.backendtechassessment.dataobjects.Buyer;
import com.intuit.cg.backendtechassessment.dataobjects.Project;
import com.intuit.cg.backendtechassessment.dataobjects.Projects;

/**
 * This class defines the ReST controller for this marketplace project. Most of the
 * heavy lifting is done by the MarketplaceDAO. This class simply delegates the
 * significant data manipulation and storage jobs to the data access object.
 *
 * There are some significant shortcomings that did not get addressed:
 * 1. The project max amount is not respected - that is, winning bids just have to be the
 *    lowest, but they will still win even if they are over the project's max amount
 * 2. While the times are stored and tracked, they are not factored into any of the
 *    current calculations - this controller would need a few more endpoints to help
 *    manage time and enforce time restrictions
 * 3. The data structures only offer a limited set of CRUD for the data objects - it 
 *    wasn't necessary to do all CRUD in order to complete the requirements
 */
@RestController
public class ProjectController {

	private Gson gson = new Gson();
	private MarketplaceDAO dao = Marketplace.getInitializedMarketplaceDAO();
	
	public ProjectController() {
	}
	
	/**
	 * Hello world listening at /
	 */
	@RequestMapping(value="/", method=RequestMethod.GET,
			produces=MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<String> hello() {
		return new ResponseEntity<String>("{result:'hello'}", HttpStatus.OK);
	}

	/**
	 * Handles the /projects/create end point. Accepts the JSON definition of a Project.
	 */
	@RequestMapping(value="/" + RequestMappings.PROJECTS + "/create",
			method=RequestMethod.POST, consumes=MediaType.APPLICATION_JSON_UTF8_VALUE,
			produces=MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<String> createProject(@RequestBody Project project) {
		dao.addProject(project);
		// missing error checking
		return new ResponseEntity<String>("{result:'success'}", HttpStatus.OK);
	}

	/**
	 * Handles the /projects/getById/{id} end point. Accepts the string UUID value and
	 * returns the JSON definition of a Project. The project returned includes the
	 * JSON definition of the buyer that submitted the lowest bid.
	 */
	@RequestMapping(value="/" + RequestMappings.PROJECTS + "/getById/{id}",
			method=RequestMethod.GET, produces=MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<Project> getProjectById(@PathVariable("id") String id) {
		Project project = dao.getProjectById(UUID.fromString(id));
		project.setSelectedBid(null);
		project.setSelectedBuyer(null);
		try {
			Bid bid = dao.findLowestBid(project);
			project.setSelectedBid(bid);
			project.setSelectedBuyer(dao.getBuyerById(bid.getBuyerId()));
		} catch (NoBidFoundException nbfe) {
			// do nothing
		}
		if (project != null) {
			return new ResponseEntity<Project>(project, HttpStatus.OK);
		}
		return new ResponseEntity(HttpStatus.NOT_FOUND);
	}

	/**
	 * Returns the JSON representation of all projects when invoking the
	 * /projects/getAll end point.
	 */
	@RequestMapping(value="/" + RequestMappings.PROJECTS + "/getAll",
			method=RequestMethod.GET, produces=MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<Projects> getAllProjects() {
		Collection<Project> projects = dao.getAllProjects();
		Projects projectList = new Projects();
		projectList.setProjects(projects);
		// missing error checking
		return new ResponseEntity<Projects>(projectList, HttpStatus.OK);
	}

	/**
	 * Handles the /bids/create end point. Accepts the JSON definition of a Bid.
	 */
	@RequestMapping(value="/" + RequestMappings.BIDS + "/create",
			method=RequestMethod.POST, consumes=MediaType.APPLICATION_JSON_UTF8_VALUE,
			produces=MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<String> createBid(@RequestBody Bid bid) {
		dao.addBid(bid);
		// missing error checking
		return new ResponseEntity<String>("{result:'success'}", HttpStatus.OK);
	}

	/**
	 * Handles the /buyers/create end point. Accepts the JSON definition of a Buyer.
	 */
	@RequestMapping(value="/" + RequestMappings.BUYERS + "/create",
			method=RequestMethod.POST, consumes=MediaType.APPLICATION_JSON_UTF8_VALUE,
			produces=MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<String> createBuyer(@RequestBody Buyer buyer) {
		dao.addBuyer(buyer);
		// missing error checking
		return new ResponseEntity<String>("{result:'success'}", HttpStatus.OK);
	}
}

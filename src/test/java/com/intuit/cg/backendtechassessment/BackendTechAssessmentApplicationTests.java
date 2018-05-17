package com.intuit.cg.backendtechassessment;

import java.util.Collection;
import java.util.UUID;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.http.MediaType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import com.intuit.cg.backendtechassessment.dataobjects.Bid;
import com.intuit.cg.backendtechassessment.controller.ProjectController;
import com.intuit.cg.backendtechassessment.dataobjects.Project;
import com.google.gson.Gson;
import com.intuit.cg.backendtechassessment.persistence.MarketplaceDAO;
import com.intuit.cg.backendtechassessment.configuration.Marketplace;
import com.intuit.cg.backendtechassessment.controller.requestmappings.RequestMappings;
import com.intuit.cg.backendtechassessment.exceptions.NoBidFoundException;
import com.intuit.cg.backendtechassessment.dataobjects.Buyer;

import org.junit.*;
import org.junit.runner.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.boot.test.autoconfigure.web.servlet.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * The main design idea behind the tests was to construct one for each of the project
 * requirements. This way having passing tests means that some part of the desired
 * functionality is working. The only test that was actually interesting was the 
 * autobid test. This one required multiple steps in order to prove that the autobid
 * feature is actually working.
 */
@RunWith(SpringRunner.class)
@WebMvcTest(ProjectController.class)
public class BackendTechAssessmentApplicationTests {

	private MarketplaceDAO dao = Marketplace.getInitializedMarketplaceDAO();
	private Gson gson = new Gson();

	@Autowired
	private MockMvc mvc;

	@Test
	public void helloTest() throws Exception {
		this.mvc.perform(get("/"))
				.andExpect(status().isOk()).andExpect(content().string("{result:'hello'}"));
	}

	/**
	 * Test the requirement to create a project
	 */
	@Test
	public void createProject() throws Exception {
		UUID projectId = UUID.randomUUID();
		Project project = new Project();
		project.setTitle("testProject");
		project.setProjectId(projectId);
		project.setDescription("a test description");
		project.setMaxBudget(25.0);
		project.setProjectEndDate(System.currentTimeMillis() + 3600000); // one hour from now
		project.setSellerId(dao.getRandomSellerId());

		this.mvc.perform(post("/projects/create").contentType(MediaType.APPLICATION_JSON_UTF8_VALUE).content(gson.toJson(project)))
				.andExpect(status().isOk()).andExpect(content().string("{result:'success'}"));
	}

	/**
	 * Test the requirement to return a project by id - including winning bid and buyer
	 */
	@Test
	public void requestProjectById() throws Exception {
		UUID projectId = dao.getRandomProjectId();
		Project project = dao.getProjectById(projectId);
		project.setSelectedBid(null);
		project.setSelectedBuyer(null);
		try {
			Bid bid = dao.findLowestBid(project);
			project.setSelectedBid(bid);
			project.setSelectedBuyer(dao.getBuyerById(bid.getBuyerId()));
		} catch (NoBidFoundException nbfe) {
			// do nothing
		}

		StringBuffer buffy = new StringBuffer("/projects/getById/");
		buffy.append(projectId.toString());
		this.mvc.perform(get(buffy.toString()))
				.andExpect(status().isOk()).andExpect(content().string(gson.toJson(project)));
	}

	/**
	 * Test the requirement to create a bid
	 */
	@Test
	public void createBid() throws Exception {
		UUID bidId = UUID.randomUUID();
		Bid bid = new Bid();
		bid.setBuyerId(dao.getRandomBuyerId());
		bid.setBidAmount(23.2);
		bid.setProjectId(dao.getRandomProjectId());
		bid.setBidId(bidId);
		
		this.mvc.perform(post("/bids/create").contentType(MediaType.APPLICATION_JSON_UTF8_VALUE).content(gson.toJson(bid)))
				.andExpect(status().isOk()).andExpect(content().string("{result:'success'}"));
	}

	/**
	 * Test the autobid feature
	 */
	@Test
	public void performAutobid() throws Exception {
		// create a brand new buyer, so there cannot be any bids owned by this buyer
		// set the autobid flag and autobid amount when defining the new buyer
		double autobidAmount = 123.45;
		UUID buyerId = UUID.randomUUID();
		Buyer buyer = new Buyer();
		buyer.setName("Autobuyer Guy");
		buyer.setBuyerId(buyerId);
		buyer.setAutobidAmount(autobidAmount);
		buyer.setAutobidSet(true);
		this.mvc.perform(post("/buyers/create").contentType(MediaType.APPLICATION_JSON_UTF8_VALUE).content(gson.toJson(buyer)))
				.andExpect(status().isOk()).andExpect(content().string("{result:'success'}"));
		
		// add a new project
		UUID projectId = UUID.randomUUID();
		Project project = new Project();
		project.setTitle("newTestProject");
		project.setProjectId(projectId);
		project.setDescription("autobid test description");
		project.setMaxBudget(250.0);
		project.setProjectEndDate(System.currentTimeMillis() + 3600000); // one hour from now
		project.setSellerId(dao.getRandomSellerId());

		this.mvc.perform(post("/projects/create").contentType(MediaType.APPLICATION_JSON_UTF8_VALUE).content(gson.toJson(project)))
				.andExpect(status().isOk()).andExpect(content().string("{result:'success'}"));
		
		// query the bids for the new buyer, and show that one exists for the new project,
		// linked to this new buyer, for the autobid amount that was specified
		Collection<Bid> bids = dao.getAllBidsForBuyerId(buyerId);
		Bid bid = (Bid)bids.toArray()[0];	// there should only be one
		Assert.assertTrue(bid.getProjectId().compareTo(projectId) == 0);
		Assert.assertTrue(bid.getBidAmount() == autobidAmount);
	}
}

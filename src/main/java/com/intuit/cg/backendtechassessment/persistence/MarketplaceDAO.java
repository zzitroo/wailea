package com.intuit.cg.backendtechassessment.persistence;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import com.intuit.cg.backendtechassessment.dataobjects.Bid;
import com.intuit.cg.backendtechassessment.dataobjects.Buyer;
import com.intuit.cg.backendtechassessment.dataobjects.Project;
import com.intuit.cg.backendtechassessment.dataobjects.Seller;
import com.intuit.cg.backendtechassessment.exceptions.InvalidBidAmountException;
import com.intuit.cg.backendtechassessment.exceptions.NoBidFoundException;
import com.intuit.cg.backendtechassessment.utils.CurrencyFormatter;

/**
 * The MarketplaceDAO represents the data structures that will contain all of the
 * persisted marketplace data. This object stores and returns all marketplace data items
 * that have been initialized.
 */
public class MarketplaceDAO {

	private HashMap<UUID, Bid> bidMap = new HashMap<UUID, Bid>(5);
	private HashMap<UUID, Buyer> buyerMap = new HashMap<UUID, Buyer>(5);
	private HashMap<UUID, Project> projectMap = new HashMap<UUID, Project>(5);
	private HashMap<UUID, Seller> sellerMap = new HashMap<UUID, Seller>(5);

	public MarketplaceDAO() {
	}
	
	/**
	 * Creates a bid from the given pieces of bid information
	 */
	public void addBid(double bidAmount, UUID buyerId, UUID projectId) throws InvalidBidAmountException {
		if (bidAmount < 0.0) {
			throw new InvalidBidAmountException("Buyer " + getBuyerById(buyerId).getName() +
					" submitted invalid bid amount of: " +
					CurrencyFormatter.makePrettyCurrencyString(bidAmount));
		}
		UUID bidId = UUID.randomUUID();
		Bid bid = new Bid();
		bid.setBuyerId(buyerId);
		bid.setBidAmount(bidAmount);
		bid.setBidId(bidId);
		bid.setProjectId(projectId);
		bidMap.put(bidId, bid);
	}
	
	/**
	 * Creates a bid from a complete Bid object.
	 */
	public void addBid(Bid bid) {
		bidMap.put(bid.getBidId(), bid);
	}
	
	/**
	 * Returns the Collection of all bids
	 */
	public Collection<Bid> getAllBids() {
		return bidMap.values();
	}
	
	/**
	 * Returns all of the bids that were made against the Project with the given projectId
	 */
	public List<Bid> getAllBidsForProjectId(UUID projectId) {
		Collection<Bid> allBids = bidMap.values();
		ArrayList<Bid> projectBids = new ArrayList<Bid>(0);
		for (Bid bid : allBids) {
			if (bid.getProjectId().compareTo(projectId) == 0) {
				projectBids.add(bid);
			}
		}
		return projectBids;
	}
	
	/**
	 * Returns all of the bids that were made by a particular buyer
	 */
	public List<Bid> getAllBidsForBuyerId(UUID buyerId) {
		Collection<Bid> allBids = bidMap.values();
		ArrayList<Bid> buyerBids = new ArrayList<Bid>(0);
		for (Bid bid : allBids) {
			if (bid.getBuyerId().compareTo(buyerId) == 0) {
				buyerBids.add(bid);
			}
		}
		return buyerBids;
	}
	
	/**
	 * Whenever a new Project is created, this method is called and walks through all
	 * buyers - for each buyer that has autobidSet to true, creates a bid for that Project
	 * on behalf of that buyer.
	 */
	private void createAutobids(Project project) {
		Collection<Buyer> buyers = buyerMap.values();
		for(Buyer buyer : buyers) {
			if (buyer.isAutobidSet()) {
				try {
					addBid(buyer.getAutobidAmount(), buyer.getBuyerId(), project.getProjectId());
				} catch (InvalidBidAmountException ibae) {
					// Autobid does not work if your bid amount is invalid (i.e. less than $0.00
				}
			}
		}
	}
	
	/**
	 * Adds a new buyer
	 */
	public void addBuyer(String name) {
		UUID buyerId = UUID.randomUUID();
		Buyer buyer = new Buyer();
		buyer.setName(name);
		buyer.setBuyerId(buyerId);
		buyerMap.put(buyerId, buyer);
	}
	
	/**
	 * Adds a new buyer given a Buyer object
	 */
	public void addBuyer(Buyer buyer) {
		buyerMap.put(buyer.getBuyerId(), buyer);
	}
	
	/**
	 * Returns the Collection of all buyers
	 */
	public Collection<Buyer> getAllBuyers() {
		return buyerMap.values();
	}
	
	/**
	 * Returns the buyer with the given buyerId
	 */
	public Buyer getBuyerById(UUID id) {
		return buyerMap.get(id);
	}

	/**
	 * Attempts to return the lowest Bid for the given project. Throws a
	 * NoBidFoundException if no buyer has bid on this project.
	 */
	public Bid findLowestBid(Project project) throws NoBidFoundException {
		List<Bid> bids = getAllBidsForProjectId(project.getProjectId());
		if (bids.size() == 0) {
			throw new NoBidFoundException("No bids were found for project with id: " + project.getProjectId());
		}
		Bid winningBid = bids.get(0);
		for (Bid bid : bids) {
			if (bid.getBidAmount() < winningBid.getBidAmount()) {
				winningBid = bid;
			}
		}
		return winningBid;
	}
	
	/**
	 * Adds a new seller
	 */
	public void addSeller(String name) {
		UUID sellerId = UUID.randomUUID();
		Seller seller = new Seller();
		seller.setName(name);
		seller.setSellerId(sellerId);
		sellerMap.put(sellerId, seller);		
	}
	
	/**
	 * Returns the Collection of all sellers
	 */
	public Collection<Seller> getAllSellers() {
		return sellerMap.values();
	}

	/**
	 * Add a new project to the marketplace with the given information. Whenever new
	 * projects are added, a check is made to find any buyers that have autobidSet, and
	 * automatically generate a bid for those buyers towards this new Project.
	 */
	public void addProject(String title, String description, double maxBudget, long endDate, UUID sellerId) {
		UUID projectId = UUID.randomUUID();
		Project project = new Project();
		project.setTitle(title);
		project.setProjectId(projectId);
		project.setDescription(description);
		project.setMaxBudget(maxBudget);
		project.setProjectEndDate(endDate);
		project.setSellerId(sellerId);
		
		// make sure to use the addProject(Project project) method so createAutobids()
		// can get called
		addProject(project);
	}
	
	/**
	 * Adds a new Project with the given Project object.  Whenever new
	 * projects are added, a check is made to find any buyers that have autobidSet, and
	 * automatically generate a bid for those buyers towards this new Project.
	 */
	public void addProject(Project project) {
		projectMap.put(project.getProjectId(), project);
		createAutobids(project);
	}
	
	/**
	 * Returns the Collection of all projects
	 */
	public Collection<Project> getAllProjects() {
		return projectMap.values();
	}
	
	/**
	 * Returns the Project with the given projectId.
	 */
	public Project getProjectById(UUID id) {
		return projectMap.get(id);
	}

	//////////////////////////////////////////////////////////////////////////////////////
	// Testing support ///////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Convenience method for testing purposes to return a random buyerId. This helps with
	 * seeding other data structure values.
	 */
	public UUID getRandomBuyerId() {
		int size = buyerMap.size();
		Random random = new Random();
		int rVal = random.nextInt(100) + 1;
		int index = rVal % size;
		Set<UUID> uuids = buyerMap.keySet();
		return (UUID)uuids.toArray()[index];
	}
	
	/**
	 * Convenience method for testing purposes to return a random sellerId. This helps with
	 * seeding other data structure values.
	 */
	public UUID getRandomSellerId() {
		int size = sellerMap.size();
		Random random = new Random();
		int rVal = random.nextInt(100) + 1;
		int index = rVal % size;
		Set<UUID> uuids = sellerMap.keySet();
		return (UUID)uuids.toArray()[index];
	}
	
	/**
	 * Convenience method for testing purposes to return a random projectId. This helps with
	 * seeding other data structure values.
	 */
	public UUID getRandomProjectId() {
		int size = projectMap.size();
		Random random = new Random();
		int rVal = random.nextInt(100) + 1;
		int index = rVal % size;
		Set<UUID> uuids = projectMap.keySet();
		return (UUID)uuids.toArray()[index];
	}
}
package com.intuit.cg.backendtechassessment.configuration;

import com.intuit.cg.backendtechassessment.persistence.MarketplaceDAO;
import com.intuit.cg.backendtechassessment.dataobjects.Bid;
import com.intuit.cg.backendtechassessment.dataobjects.Buyer;
import com.intuit.cg.backendtechassessment.dataobjects.Project;
import com.intuit.cg.backendtechassessment.dataobjects.Seller;
import com.intuit.cg.backendtechassessment.exceptions.InvalidBidAmountException;

/**
 * Initializes the data structures and prepares the marketplace. The Marketplace object
 * itself is a singleton whose only purpose is to return the MarketplaceDAO.
 */
public class Marketplace {
	private static MarketplaceDAO DAO = new MarketplaceDAO();
	private static final Marketplace MARKETPLACE = new Marketplace();
	
	private Marketplace() {
	}
	
	/**
	 * Returns an empty MarketplaceDAO object.
	 */
	public static MarketplaceDAO getMarketplaceDAO() {
		return MARKETPLACE.DAO;
	}
	
	/**
	 * Returns a static instance of the MarketplaceDAO that has been initialized with
	 * dummy values for testing purposes.
	 */
	public static MarketplaceDAO getInitializedMarketplaceDAO() {
		init();
		return MARKETPLACE.DAO;
	}
	
	private static void init() {
		// only initialize the DAO once
		if (DAO.getAllBuyers().size() == 0) {
			// must initialize buyers and sellers first (order between buyers and sellers is
			// unimportant)
			initializeBuyers();
			initializeSellers();
		
			// projects must come next
			initializeProjects();
		
			// and bids must be last
			initializeBids();
		}
	}
	
	private static void initializeBids() {
		for (int i = 0; i < 30; i++) {
			try {
				DAO.addBid((5.92 * i), DAO.getRandomBuyerId(), DAO.getRandomProjectId());
			} catch (InvalidBidAmountException ibae) {
				// do nothing
			}
		}
	}
	
	private static void initializeBuyers() {
		DAO.addBuyer("tom buyer");
		DAO.addBuyer("joe buyer");
		DAO.addBuyer("bob buyer");
		DAO.addBuyer("bill buyer");
		DAO.addBuyer("steve buyer");
		DAO.addBuyer("sam buyer");
		DAO.addBuyer("mary buyer");
		DAO.addBuyer("wendy buyer");
		DAO.addBuyer("pete buyer");
		DAO.addBuyer("tim buyer");
	}
	
	private static void initializeProjects() {
		long timeNow = System.currentTimeMillis();
		long tenMinutes = 600000;
		for (int i = 0; i < 15; i++) {
			DAO.addProject("project " + i, "this is a project description", (12.27 * i), (timeNow + (tenMinutes * i)), DAO.getRandomSellerId());
		}
	}
	
	private static void initializeSellers() {
		DAO.addSeller("tom seller");
		DAO.addSeller("joe seller");
		DAO.addSeller("bob seller");
		DAO.addSeller("bill seller");
		DAO.addSeller("steve seller");
		DAO.addSeller("sam seller");
		DAO.addSeller("mary seller");
		DAO.addSeller("wendy seller");
		DAO.addSeller("pete seller");
		DAO.addSeller("tim seller");
	}
}
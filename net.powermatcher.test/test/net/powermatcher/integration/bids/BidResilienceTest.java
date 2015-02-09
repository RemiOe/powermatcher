package net.powermatcher.integration.bids;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.zip.DataFormatException;

import net.powermatcher.api.data.MarketBasis;
import net.powermatcher.integration.base.ResilienceTest;
import net.powermatcher.integration.util.AuctioneerWrapper;
import net.powermatcher.integration.util.ConcentratorWrapper;
import net.powermatcher.integration.util.CsvBidReader;
import net.powermatcher.integration.util.CsvExpectedResultsReader;
import net.powermatcher.mock.MockAgent;
import net.powermatcher.mock.MockScheduler;
import net.powermatcher.mock.SimpleSession;
import net.powermatcher.test.helpers.PropertieBuilder;
import net.powermatcher.test.helpers.TestClusterHelper;

import org.junit.After;

/**
 *
 * @author FAN
 * @version 2.0
 */
public class BidResilienceTest
    extends ResilienceTest {

    private static final String AUCTIONEER_NAME = "auctioneer";
    private static final String CONCENTRATOR_NAME = "concentrator";

    // The direct upstream matcher for the agents
    protected ConcentratorWrapper concentrator;

    // The wrapped auctioneer
    protected AuctioneerWrapper auctioneer;

    protected MockScheduler auctioneerScheduler;

    protected void prepareTest(String testID, String suffix) throws IOException, DataFormatException {
        // Get the expected results
        resultsReader = new CsvExpectedResultsReader(getExpectedResultsFile(testID, suffix));

        MarketBasis marketBasis = resultsReader.getMarketBasis();

        auctioneer = new AuctioneerWrapper();
        auctioneer.activate(new PropertieBuilder().agentId(AUCTIONEER_NAME)
                                                  .clusterId("testCluster")
                                                  .marketBasis(marketBasis)
                                                  .bidUpdateRate(600)
                                                  .priceUpdateRate(600)
                                                  .build());

        concentrator = new ConcentratorWrapper();
        concentrator.activate(new PropertieBuilder().agentId(CONCENTRATOR_NAME)
                                                    .desiredParentId(AUCTIONEER_NAME)
                                                    .bidUpdateRate(600)
                                                    .build());

        cluster = new TestClusterHelper(marketBasis, concentrator);

        auctioneerScheduler = new MockScheduler();
        auctioneer.setExecutorService(auctioneerScheduler);
        auctioneer.setTimeService(cluster.getTimer());

        new SimpleSession(concentrator, auctioneer).connect();

        // Create the bid reader
        bidReader = new CsvBidReader(getBidInputFile(testID, suffix), marketBasis);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void sendBidsToMatcher() throws IOException, DataFormatException {
        super.sendBidsToMatcher();
        cluster.performTasks();
    }

    protected void checkEquilibriumPrice() {
        double expPrice = resultsReader.getEquilibriumPrice();

        // Verify the price received by the agents
        for (MockAgent agent : cluster) {
            assertEquals(expPrice, agent.getLastPriceUpdate().getPrice().getPriceValue(), 0);
        }
    }

    @After
    public void tearDown() throws IOException {
        if (bidReader != null) {
            bidReader.closeFile();
        }
        cluster.close();
    }
}
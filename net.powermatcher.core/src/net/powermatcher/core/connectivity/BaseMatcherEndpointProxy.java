package net.powermatcher.core.connectivity;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import net.powermatcher.api.Session;
import net.powermatcher.api.connectivity.MatcherEndpointProxy;
import net.powermatcher.api.data.Bid;
import net.powermatcher.api.data.MarketBasis;
import net.powermatcher.api.data.PriceUpdate;
import net.powermatcher.core.BaseAgent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Base implementation for remote agents. This is the "sending end" of a remote communication pair.
 */
public abstract class BaseMatcherEndpointProxy extends BaseAgent implements MatcherEndpointProxy {

    private static final Logger LOGGER = LoggerFactory.getLogger(BaseMatcherEndpointProxy.class);

    private Session localSession;

    /**
     * Scheduler that can schedule commands to run after a given delay, or to execute periodically.
     */
    private ScheduledExecutorService scheduler;

    /**
     * A delayed result-bearing action that can be cancelled.
     */
    private ScheduledFuture<?> scheduledFuture;

    protected void baseActivate(int reconnectTimeout) {
        // Start connector thread
        scheduledFuture = this.scheduler.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                connectRemote();
            }
        }, 0, reconnectTimeout, TimeUnit.SECONDS);
    }

    protected void baseDeactivate() {
        // Stop connector thread
        this.scheduledFuture.cancel(false);

        // Disconnect the agent
        this.disconnectRemote();
    }

    public void setExecutorService(ScheduledExecutorService scheduler) {
        this.scheduler = scheduler;
    }

    @Override
    public boolean isLocalConnected() {
        return this.localSession != null;
    }

    @Override
    public void updateRemoteMarketBasis(MarketBasis marketBasis) {
        // Sync marketbasis with local session, for new connections
        if (this.isLocalConnected() && this.localSession.getMarketBasis() == null) {
            this.localSession.setMarketBasis(marketBasis);
        }
    }

    @Override
    public void updateRemoteClusterId(String clusterId) {
        // Sync clusterid with local session, for new connections
        if (this.isLocalConnected() && this.localSession.getMarketBasis() == null) {
            this.localSession.setClusterId(clusterId);
        }
    }

    @Override
    public boolean connectToAgent(Session session) {
        this.localSession = session;
        LOGGER.info("Agent connected with session [{}]", session.getSessionId());

        // Initiate a remote connection
        connectRemote();

        return true;
    }

    @Override
    public void agentEndpointDisconnected(Session session) {
        // Disconnect local agent
        this.localSession = null;
        LOGGER.info("Agent disconnected with session [{}]", session.getSessionId());

        // Disconnect remote agent
        this.disconnectRemote();
    }

    @Override
    public void updateBid(Session session, Bid newBid) {
        if (this.localSession != session) {
            LOGGER.warn("Received bid update for unknown session.");
            return;
        }

        if (!isRemoteConnected()) {
            LOGGER.warn("Received bid update, but remote agent is not connected.");
            return;
        }

        if (this.localSession.getMarketBasis() == null) {
            LOGGER.info("Skip bid update to local agent, no marketbasis available.");
            return;
        }

        // Relay bid to remote agent
        this.updateBidRemote(newBid);
    }

    @Override
    public void updateLocalPrice(PriceUpdate priceUpdate) {
        if (!this.isLocalConnected()) {
            LOGGER.info("Skip price update to local agent, not connected.");
            return;
        }

        if (this.localSession.getMarketBasis() == null) {
            LOGGER.info("Skip price update to local agent, no marketbasis available.");
            return;
        }

        this.localSession.updatePrice(priceUpdate);
    }

    public boolean canEqual(Object other) {
        return other instanceof BaseMatcherEndpointProxy;
    }

    @Override
    public boolean equals(Object obj) {
        BaseMatcherEndpointProxy that = (BaseMatcherEndpointProxy) ((obj instanceof BaseMatcherEndpointProxy) ? obj
                : null);
        if (that == null) {
            return false;
        }

        if (this == that) {
            return true;
        }

        return canEqual(that) && this.localSession.equals(that.localSession);
    }

    @Override
    public int hashCode() {
        return 211 * (this.localSession.hashCode());
    }
}
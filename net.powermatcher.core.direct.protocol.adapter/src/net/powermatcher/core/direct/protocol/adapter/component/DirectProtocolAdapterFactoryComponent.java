package net.powermatcher.core.direct.protocol.adapter.component;


import java.util.Map;

import net.powermatcher.core.adapter.DirectConnectorFactoryTracker;
import net.powermatcher.core.adapter.DirectConnectorTrackerListener;
import net.powermatcher.core.adapter.component.DirectAdapterFactoryComponent;
import net.powermatcher.core.adapter.service.Connectable;
import net.powermatcher.core.adapter.service.DirectAdapterFactoryService;
import net.powermatcher.core.agent.framework.service.ChildConnectable;
import net.powermatcher.core.agent.framework.service.ParentConnectable;
import net.powermatcher.core.direct.protocol.adapter.DirectProtocolAdapter;
import net.powermatcher.core.direct.protocol.adapter.DirectProtocolAdapterFactory;

import org.osgi.framework.BundleContext;

import aQute.bnd.annotation.component.Activate;
import aQute.bnd.annotation.component.Component;
import aQute.bnd.annotation.component.Deactivate;
import aQute.bnd.annotation.component.Reference;


/**
 *  OSGi wrapper component for a DirectProtocolAdapter.
 * 
 * <p>
 * The DirectProtocolAdapterFactoryComponent is a wrapper class that creates an OSGi component of
 * a DirectProtocolAdapter. A DirectConnectorFactoryTracker will bind the adapter component to a direct protocol adapter
 * component that implements the AgentConnectorService interface if that component 
 * instance has the same connector id and cluster id.
 * </p>
 * Configuration properties and default values are defined in DirectProtocolAdapterFactoryComponentConfiguration.
 * 
 * @author IBM
 * @version 0.9.0
 * 
 * @see DirectConnectorFactoryTracker
 * @see DirectConnectorTrackerListener
 * @see ChildConnectable
 * @see DirectProtocolAdapterFactoryComponentConfiguration
 * @see DirectProtocolAdapter
 */
@Component(name = DirectProtocolAdapterFactoryComponent.COMPONENT_NAME, designateFactory = DirectProtocolAdapterFactoryComponentConfiguration.class)
public class DirectProtocolAdapterFactoryComponent extends DirectAdapterFactoryComponent<ChildConnectable, ParentConnectable> {
	/**
	 * Define the component name (String) constant.
	 */
	public final static String COMPONENT_NAME = "net.powermatcher.core.direct.protocol.adapter.component.DirectProtocolAdapterFactory";

	/**
	 * Constructs an instance of this class.
	 */
	public DirectProtocolAdapterFactoryComponent() {
		super(new DirectProtocolAdapterFactory());
	}

	/**
	 * Activate with the specified properties parameter.
	 * 
	 * @param properties
	 *            The properties (<code>Map<String,Object></code>) parameter.
	 */
	@Activate
	protected void activate(final BundleContext context, final Map<String, Object> properties) {
		super.activate(context, properties);
	}

	/**
	 * Add agent connector with the specified source connector parameter.
	 * 
	 * @param sourceConnector
	 *            The source connector (<code>AgentConnectorService</code>)
	 *            parameter.
	 * @see #removeAgentConnector(ChildConnectable)
	 */
	@Reference(type = '*')
	protected void addAgentConnector(final ChildConnectable sourceConnector) {
		super.addSourceConnector(sourceConnector);
	}

	/**
	 * Add matcher connector with the specified target connector parameter.
	 * 
	 * @param targetConnector
	 *            The target connector (<code>MatcherConnectorService</code>)
	 *            parameter.
	 * @see #removeMatcherConnector(ParentConnectable)
	 */
	@Reference(type = '*')
	protected void addMatcherConnector(final ParentConnectable targetConnector) {
		super.addTargetConnector(targetConnector);
	}

	/**
	 * Deactivate.
	 */
	@Deactivate
	protected void deactivate() {
		super.deactivate();
	}

	/**
	 * Remove agent connector with the specified source connector parameter.
	 * 
	 * @param sourceConnector
	 *            The source connector (<code>AgentConnectorService</code>)
	 *            parameter.
	 * @see #addAgentConnector(ChildConnectable)
	 */
	protected void removeAgentConnector(final ChildConnectable sourceConnector) {
		super.removeSourceConnector(sourceConnector);
	}

	/**
	 * Remove matcher connector with the specified target connector parameter.
	 * 
	 * @param targetConnector
	 *            The target connector (<code>AgentConnectorService</code>)
	 *            parameter.
	 * @see #addMatcherConnector(ParentConnectable)
	 */
	protected void removeMatcherConnector(final ParentConnectable targetConnector) {
		super.removeTargetConnector(targetConnector);
	}

	/**
	 * Get the Java type of the connector T.
	 * Due to type erasure it is necessary to gave a method return the type explicitly for use
	 * in the call to getTargetConnectorIds.
	 * @see DirectAdapterFactoryService#getTargetConnectorIds(Connectable) 
	 * @return The Java type of the connector T.
	 */
	@Override
	protected Class<ChildConnectable> getConnectorType() {
		return ChildConnectable.class;
	}

}

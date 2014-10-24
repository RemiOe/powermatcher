package net.powermatcher.core.messaging.protocol.adapter.component;


import java.util.Map;

import org.osgi.framework.BundleContext;

import net.powermatcher.core.adapter.component.SourceAdapterFactoryComponent;
import net.powermatcher.core.adapter.service.Connectable;
import net.powermatcher.core.adapter.service.DirectAdapterFactoryService;
import net.powermatcher.core.agent.framework.service.ChildConnectable;
import net.powermatcher.core.messaging.protocol.adapter.AgentProtocolAdapterFactory;
import aQute.bnd.annotation.component.Activate;
import aQute.bnd.annotation.component.Component;
import aQute.bnd.annotation.component.Deactivate;
import aQute.bnd.annotation.component.Reference;


/**
 * OSGi wrapper component for a AgentProtocolAdapterFactory.
 * 
 * <p>
 * The AgentProtocolAdapterFactoryComponent is a wrapper class that creates an OSGi component of
 * a AgentProtocolAdapter. A ConnectorTracker will bind the adapter component to a component 
 * that implements the AgentConnectorService interface if that component instance has the
 * same id and cluster id.
 * </p>
 * @author IBM
 * @version 0.9.0
 * 
 * @see AgentProtocolAdapterFactory
 * @see AgentProtocolAdapterFactoryComponentConfiguration
 */
@Component(name = AgentProtocolAdapterFactoryComponent.COMPONENT_NAME, designateFactory = AgentProtocolAdapterFactoryComponentConfiguration.class)
public class AgentProtocolAdapterFactoryComponent extends SourceAdapterFactoryComponent<ChildConnectable> {
	/**
	 * Define the component name (String) constant.
	 */
	public final static String COMPONENT_NAME = "net.powermatcher.core.messaging.protocol.adapter.AgentProtocolAdapterFactory";

	/**
	 * Constructs an instance of this class.
	 */
	public AgentProtocolAdapterFactoryComponent() {
		super(new AgentProtocolAdapterFactory());
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
	 *            The source connector (<code>AgentConnectorService</code>) parameter.
	 * @see #removeAgentConnector(ChildConnectable)
	 */
	@Reference(type = '*')
	protected void addAgentConnector(final ChildConnectable sourceConnector) {
		super.addSourceConnector(sourceConnector);
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
	 *            The source connector (<code>AgentConnectorService</code>) parameter.
	 * @see #addAgentConnector(ChildConnectable)
	 */
	protected void removeAgentConnector(final ChildConnectable sourceConnector) {
		super.removeSourceConnector(sourceConnector);
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

package it.polito.verifoo.rest.medicine;

import it.polito.verifoo.rest.jaxb.FunctionalTypes;
import it.polito.verifoo.rest.jaxb.Node;
import it.polito.verifoo.rest.jaxb.NodeConstraints.NodeMetrics;

public class VNFDescriptor {
	
	private String fileHeader = "descriptor_version: \"vnfd-schema-01\"\n"
								+"vendor: \"eu.sonata-nfv\"\n";
	private int freeInInterface, freeOutInterface;	
	/**
	 * Public constructor that creates for a specific node a VNF descriptor
	 * @param node 
	 * @param nMetrics Contraints on the node
	 * @param inLinks Number of incoming links
	 * @param outLinks Number of outgoing links
	 */
	public VNFDescriptor(Node node, NodeMetrics nMetrics, int inLinks, int outLinks) {
		int cores, reqStor, mem;
		boolean additional = false;
		if(nMetrics == null){
			cores = 1;
			reqStor = 1;
			mem = 1;
		}
		else{
			cores = nMetrics.getCores()==0?1:nMetrics.getCores();
			reqStor = nMetrics.getReqStorage()==0?1:nMetrics.getReqStorage();
			mem = nMetrics.getMemory()==0?1:nMetrics.getMemory();
					
		}
		if(node.getFunctionalType().equals(FunctionalTypes.MAILCLIENT) ||
				   node.getFunctionalType().equals(FunctionalTypes.WEBCLIENT) ||	
				   node.getFunctionalType().equals(FunctionalTypes.ENDHOST) ||
				   node.getFunctionalType().equals(FunctionalTypes.MAILSERVER) ||
				   node.getFunctionalType().equals(FunctionalTypes.WEBSERVER)){
			       additional = true;
				}
		fileHeader += "name: \""+node.getName().toLowerCase()+"-vnf\"\n"
						+ "version: \"0.1\"\n"
						+ "author: \"Verifoo\"\n"
						+ "description: \"VNF descriptor automatically generated for "+node.getName()+"\"\n"
						+ "virtual_deployment_units:\n"
						+ "  - id: \"1\"\n"
						+ "    vm_image: \"sonatanfv/sonata-empty-vnf\"\n" //TODO to change
						+ "    vm_image_format: \"docker\"\n"
						+ "    resource_requirements:\n"
						+ "      cpu:\n"
						+ "        vcpus: "+cores+"\n"
						+ "      memory:\n"
						+ "        size: "+mem+"\n"
						+ "        size_unit: \"GB\"\n"
						+ "      storage:\n"
						+ "        size: "+reqStor+"\n"
						+ "        size_unit: \"GB\"\n"
						+ "    connection_points:\n";
		for(int i = 0; i < inLinks+outLinks; i++){
			fileHeader += "      - id: \"vdu01:cp0"+i+"\"\n"
						+ "        interface: \"ipv4\"\n"
						+ "        type: \"internal\"\n";
		}
		if(additional){
			fileHeader += "      - id: \"vdu01:ext\"\n"
						+ "        interface: \"ipv4\"\n"
						+ "        type: \"internal\"\n";
		}
		fileHeader += "virtual_links:\n";
		for(int i = 0; i < inLinks; i++){
			fileHeader += "  - id: \"input"+i+"\"\n"
						+ "    connectivity_type: \"E-Line\"\n"
						+ "    connection_points_reference:\n"
						+ "      - \"vdu01:cp0"+i+"\"\n"
						+ "      - \"input"+i+"\"\n";
		}
		
		for(int i = 0; i < outLinks; i++){
			fileHeader += "  - id: \"output"+i+"\"\n"
						+ "    connectivity_type: \"E-Line\"\n"
						+ "    connection_points_reference:\n"
						+ "      - \"vdu01:cp0"+(i+inLinks)+"\"\n"
						+ "      - \"output"+i+"\"\n";
		}
		if(additional){
			fileHeader += "  - id: \"intf-ext\"\n"
						+ "    connectivity_type: \"E-Line\"\n"
						+ "    connection_points_reference:\n"
						+ "      - \"vdu01:ext\"\n"
						+ "      - \"intf-ext\"\n";
		}
		fileHeader += "connection_points:\n";
		for(int i = 0; i < inLinks; i++){
			fileHeader += "  - id: \"input"+i+"\"\n"
						+ "    interface: \"ipv4\"\n"
						+ "    type: \"external\"\n";
		}	
		freeInInterface = inLinks;
		for(int i = 0; i < outLinks; i++){
			fileHeader += "  - id: \"output"+i+"\"\n"
						+ "    interface: \"ipv4\"\n"
						+ "    type: \"external\"\n";
		}
		freeOutInterface = outLinks;
		if(additional){
			fileHeader += "  - id: \"intf-ext\"\n"
						+ "    interface: \"ipv4\"\n"
						+ "    type: \"external\"\n";
		}
	} 
    /**
     * Get the number of incoming interfaces still not connected
     * @return
     */
	public int getFreeInInterface() {
		return freeInInterface;
	}
	/**
     * Get the number of outgoing interfaces still not connected
     * @return
     */
	public int getFreeOutInterface() {
		return freeOutInterface;
	}
	/**
     * Use up an incoming interface because it has been connected
     * @return
     */
	public int bookInInterface() {
		freeInInterface--;
		return freeInInterface;
	}
	/**
     * Use up an outgoing interface because it has been connected
     * @return
     */
	public int bookOutInterface() {
		freeOutInterface--;
		return freeOutInterface;
	}
	/**
	 * Get the VNF descriptor
	 * @return
	 */
	public String getVNFDescriptor(){
		return fileHeader;
	}

}

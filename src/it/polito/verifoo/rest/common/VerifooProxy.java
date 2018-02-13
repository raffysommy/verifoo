package it.polito.verifoo.rest.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.microsoft.z3.ArithExpr;
import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.IntExpr;
import com.microsoft.z3.Context;
import com.microsoft.z3.Status;

import it.polito.verifoo.components.RoutingTable;
import it.polito.verifoo.rest.jaxb.*;
import it.polito.verigraph.mcnet.components.*;
/**
 * 
 * This is the main class that will interface with the Verifoo classes
 *
 */
public class VerifooProxy {
	    private Context ctx;
	    private NetContext nctx;
	    private Network net;
	    private NodeNetworkObject netobjs;
	    private HashMap<Node,List<String>> rawConditions;
	    private HashMap<Node, HashMap<String, BoolExpr>> conditionDB;
		public Checker check;
		private Logger logger = LogManager.getLogger("mylog");
		private List<List<String>> savedChain = new ArrayList<>();
		List<Node> nodes;
		List<Link> links = new ArrayList<>();
		List<Host> hosts;
		List<Connection> connections;
		Graph graph;
		private List<NodeCapacity> capacities;
		/**
		 * Public constructor for the verifoo proxy service
		 * @param graph The graph that will be deployed on the network
		 * @param hosts The list of hosts in the network
		 * @param conns The connections between hosts
		 * @param capacityDefinition The list of the capacity for each node that will be deployed
		 * @throws BadGraphError
		 */
	    public VerifooProxy(Graph graph,Hosts hosts,Connections conns, CapacityDefinition capacityDefinition) throws BadGraphError{
			HashMap<String, String> cfg = new HashMap<String, String>();
		    cfg.put("model", "true");
		    ctx = new Context(cfg);
		    nodes=graph.getNode();
		    this.hosts = hosts.getHost();
		    this.connections = conns.getConnection();
		    this.graph=graph;
		    if(capacityDefinition!=null){
		    	this.capacities=capacityDefinition.getCapacityForNode();
		    }else{
		    	this.capacities=new ArrayList<NodeCapacity>();
		    }
			nctx = NetContextGenerator.generate(ctx,nodes);
				
			//System.out.println(nctx.am);
			net = new Network (ctx,new Object[]{nctx});
			
			/* Generate the different network object and map it to XML Node */
			netobjs=new NodeNetworkObject(ctx, nctx, net,nodes);
			
			AddressMapping adm = new AddressMapping(netobjs, nctx, net);
			adm.setAddressMappings(nodes);
		    
		    
		    rawConditions=new HashMap<>();
		    conditionDB=new HashMap<>();
		    nodes.forEach(n -> {
				rawConditions.put(n, new ArrayList<>());
		    	conditionDB.put(n, new HashMap<>());
		    });
			checkPhysicalNetwork();
		    checkNffg();	
		    setConditions();
		    netobjs.generateAcl();
		    netobjs.generateCache();
		    netobjs.generateVPN();
		    netobjs.attachToNet();
		    check = new Checker(ctx,nctx,net);
	    }
	    /**
	     * Sets all the conditions for the nodes. First, it sets up the host conditions. 
	     * Then, it creates the conditions to make that a node is deployed only on one host.
	     * After that, it creates the condition that if an host has a node deployed on it, it has to be active.
	     * Lastly, it creates the conditions that the sum of the disk requirements of all the nodes
	     * deployed on an host, is less than disk capacity of that host 
	     * @throws BadGraphError
	     */
		private void setConditions() throws BadGraphError{
		  	
			HashMap<String, BoolExpr> hostCondition = new HashMap<>();
			hosts.forEach(h -> {
				BoolExpr e = ctx.mkBoolConst(h.getName());
				hostCondition.put(h.getName(),e);
				nctx.softConstraints.add(new Tuple<BoolExpr, String>(ctx.mkNot(e), "servers"));
			});
			logger.debug("Host constraint: " + hostCondition);
			conditionDB.entrySet().forEach(e -> {
				List<IntExpr> univocity = new ArrayList<>();
				e.getValue().entrySet().stream()
										.map(pair -> pair.getValue())
										.collect(Collectors.toList())
										.forEach(c -> {
											univocity.add(nctx.bool_to_int(c));
										});
				//System.out.println(e.getKey().getName() + " univocity: " + univocity);
				ArithExpr uniqueNodeConstraint = null;
				for(IntExpr u:univocity){
					if(uniqueNodeConstraint == null){
						uniqueNodeConstraint = u;
					}
					else{
						uniqueNodeConstraint = ctx.mkAdd(uniqueNodeConstraint, u);
					}
					//System.out.println(uniqueNodeConstraint);
				}
				if(uniqueNodeConstraint != null){
					logger.debug(e.getKey().getName() + " adding univocity: " + ctx.mkEq(uniqueNodeConstraint, ctx.mkInt(1)));
					nctx.constraints.add(ctx.mkEq(uniqueNodeConstraint, ctx.mkInt(1)));
				}
			});
			hosts.forEach(h -> {
				List<BoolExpr> implications = new ArrayList<>();
				conditionDB.entrySet().stream()
									.flatMap(e -> e.getValue().entrySet().stream())
									.filter(e -> e.getKey().equals(h.getName()))
									.map(e -> e.getValue())
									.collect(Collectors.toList())
									.forEach(i -> {
										implications.add(ctx.mkImplies(hostCondition.get(h.getName()), i));
									});
				//System.out.println(h.getName() + " implication: " + implications);
				BoolExpr hostImpliesNodeConstraint = null;
				for(BoolExpr i:implications){
					if(hostImpliesNodeConstraint == null){
						hostImpliesNodeConstraint = i;
					}
					else{
						hostImpliesNodeConstraint = ctx.mkOr(hostImpliesNodeConstraint, i);
					}
					//System.out.println(hostImpliesNodeConstraint);
				}
				if(hostImpliesNodeConstraint != null){
					logger.debug(h.getName() + " implication: " + hostImpliesNodeConstraint);
					nctx.constraints.add(hostImpliesNodeConstraint);
				}
			});
			
			hosts.forEach(h -> {
				List<ArithExpr> diskRequirements = new ArrayList<>();
				conditionDB.entrySet().stream()
									.flatMap(e -> e.getValue().entrySet().stream())
									.filter(e -> e.getKey().equals(h.getName()))
									.map(e -> e.getValue())
									.collect(Collectors.toList())
									.forEach(i -> {
										String node = i.toString().substring(0, i.toString().lastIndexOf('@'));
										NodeCapacity app = capacities.stream().filter(c->c.getNode().equals(node)).findFirst().orElse(null);
										int capacity;
										if(app == null)
											capacity = 0;
										else
											capacity = app.getCapacity();
										diskRequirements.add(ctx.mkMul(ctx.mkInt(capacity), nctx.bool_to_int(i)));
									});
				logger.debug(h.getName() + " disk requirement: " + diskRequirements);
				ArithExpr diskConstraint = null;
				for(ArithExpr d:diskRequirements){
					if(diskConstraint == null){
						diskConstraint = d;
					}
					else{
						diskConstraint = ctx.mkAdd(diskConstraint, d);
					}
					//System.out.println(hostImpliesNodeConstraint);
				}
				if(diskConstraint != null){
					logger.debug(h.getName() + " left side: " + diskConstraint);
					logger.debug(h.getName() + " requirements: " + ctx.mkLe(diskConstraint, ctx.mkMul(ctx.mkInt(h.getDiskStorage()), nctx.bool_to_int(hostCondition.get(h.getName())))));
					nctx.constraints.add(ctx.mkLe(diskConstraint, ctx.mkMul(ctx.mkInt(h.getDiskStorage()), nctx.bool_to_int(hostCondition.get(h.getName())))));
				}
			});
			
			
			hosts.forEach(h -> {
				nctx.softConstraints.add(new Tuple<BoolExpr, String>(ctx.mkNot(hostCondition.get(h.getName())), "num_servers"));
			});
		}

		/**
		 * Checks if in the xml there are only one host client and one host server, then
		 * it calls the function to calculate all the possible paths from the host client to the host server
		 * @throws BadGraphError
		 */
		private void checkPhysicalNetwork() throws BadGraphError{
            long nServer = hosts.stream()
	            	 .filter((h) -> {return h.getType() == TypeOfHost.SERVER;})
	            	 .count();
            long nClient = hosts.stream()
	            	 .filter((h) -> {return h.getType() == TypeOfHost.CLIENT;})
	            	 .count();
            long nMiddle = hosts.stream()
	            	 .filter((h) -> {return h.getType() == TypeOfHost.MIDDLEBOX;})
	            	 .count();
            /*System.out.println("nPhysicalServer: " + nServer +
            				   " nPhysicalClient: " + nClient);*/
            if(nServer != 1 || nClient != 1){
            	//System.err.println("Only one client and one server are allowed in the physical network");
            	throw new BadGraphError("Only one client and one server are allowed in the physical network",EType.INVALID_PHY_SERVER_CLIENT_CONF);
            }  
            if(nMiddle == 0) throw new BadGraphError("At least one middle host has to be defined",EType.NO_MIDDLE_HOST_DEFINED);
            String hostClient = hosts.stream().filter(h -> {return h.getType() == TypeOfHost.CLIENT;}).findFirst().get().getName();
			String hostServer = hosts.stream().filter(h -> {return h.getType() == TypeOfHost.SERVER;}).findFirst().get().getName();
            savedChain = ChainExtractor.createHostChain(hostClient, hostServer, connections, nodes.size());
            if(savedChain.size() == 0) throw new BadGraphError("Host client and host server are not connected",EType.INVALID_PHY_SERVER_CLIENT_CONF);
		}
		
		/**
		 * Checks if in the xml there are only one node that is a client and one node that is a server 
		 * and they are for the same service (web or mail), then it calls the function to create the link 
		 * from the node's neighbours. Lastly, it calls a function to create the routing tables
		 * @throws BadGraphError
		 */
		private void checkNffg() throws BadGraphError{
            long nMailServer = nodes.stream()
	            	 .filter((n) -> n.getFunctionalType().equals(FunctionalTypes.MAILSERVER))
	            	 .count();
            long nWebServer = nodes.stream()
	            	 .filter((n) -> n.getFunctionalType().equals(FunctionalTypes.WEBSERVER))
	            	 .count();
            long nMailClient = nodes.stream()
	            	 .filter((n) -> n.getFunctionalType().equals(FunctionalTypes.MAILCLIENT))
	            	 .count();
            long nWebClient = nodes.stream()
	            	 .filter((n) -> n.getFunctionalType().equals(FunctionalTypes.WEBCLIENT))
	            	 .count();
            long nEndHost	= nodes.stream()
            		 .filter((n) -> n.getFunctionalType().equals(FunctionalTypes.ENDHOST))
            		 .count();
            /*System.out.println("nMailServer: " + nMailServer +
            				   " nMailClient: " + nMailClient +
            				   " nWebServer: " + nWebServer +
            				   " nWebClient: " + nWebClient);*/
            if((nMailServer>0 && nMailServer != nMailClient+nEndHost) 
            	|| (nMailServer==0 && nMailClient!=nMailServer) 
            	|| (nWebServer>0 && nWebServer != nWebClient+nEndHost)
            	|| (nWebServer==0 && nWebClient!=nWebServer)
            	|| nMailServer+nWebServer>1){
            	//System.err.println("Only one client and one server of the same type is allowed");
            	throw new BadGraphError("Only one client and one server of the same type is allowed",EType.INVALID_SERVER_CLIENT_CONF);
            }
            Node client = nodes.stream().filter(n -> {return n.getFunctionalType().equals(FunctionalTypes.MAILCLIENT) || n.getFunctionalType().equals(FunctionalTypes.WEBCLIENT)|| n.getFunctionalType().equals(FunctionalTypes.ENDHOST);}).findFirst().get();
            Node server = nodes.stream().filter(n -> {return n.getFunctionalType().equals(FunctionalTypes.MAILSERVER) || n.getFunctionalType().equals(FunctionalTypes.WEBSERVER);}).findFirst().get();
            if(client.getNeighbour().size() != 1 || server.getNeighbour().size() != 1) throw new BadGraphError("Nodes must be in a chain",EType.INVALID_NODE_CHAIN);
            String nextName = client.getNeighbour().stream().filter(n -> !(n.getName().equals(client.getName()))).findFirst().get().getName();
			Node next = nodes.stream().filter(n -> n.getName().equals(nextName)).findFirst().get();
			try{
				createLink(client, next, server, new ArrayList<>(), new ArrayList<>());
				//logger.debug("Links created");
				createRoutingTables(client, server);   
			}catch(StackOverflowError e) {
            	throw new BadGraphError("The chain of nodes is invalid",EType.INVALID_NODE_CHAIN);
			}
		}
		/**
		 * Creates the link from the node's neighbours
		 * @param prec previous node in the chain
		 * @param current current node in the chain
		 * @param server the node that is the server of the chain
		 * @throws BadGraphError
		 */
		private boolean createLink(Node prec, Node current, Node server, List<String> converting, List<String> converted) throws BadGraphError{
			if(current.getName().equals(server.getName())){
				//logger.debug("Found neighbours of " + prec.getName() + " ("+ current.getName() + ") that reaches the server");
				logger.debug("New Link from " + prec.getName() + " to "+ current.getName() +" towards server "+server.getName());
				links.add(new Link(prec.getName(), current.getName()));
				return true;
			}
			if(converted.contains(current.getName())){
				//logger.debug("Found neighbours of " + prec.getName() + " ("+ current.getName() + ") that reaches the server");
				logger.debug("New Link from " + prec.getName() + " to "+ current.getName() +" towards server "+server.getName());
				links.add(new Link(prec.getName(), current.getName()));
				return true;
			}
			
			//if(current.getNeighbour().size() > 2) throw new BadGraphError("Nodes must be in a chain",EType.INVALID_NODE_CHAIN);
			boolean found = false;
			try {
				List<String> neighbours = current.getNeighbour().stream()
												.filter(n -> !(n.getName().equals(prec.getName())))
												.map(n -> n.getName())
												.collect(Collectors.toList());
				converting.add(current.getName());
				//logger.debug("From " + prec.getName() + " converting neighbours of " + current.getName() + " " + neighbours +" into links");
				
				for(String neighbour : neighbours){
					if(!converting.contains(neighbour)){
						Node next = nodes.stream().filter(n -> n.getName().equals(neighbour)).findFirst().get();
						//If neighbour reaches the server or a node that reaches the server then... 
						if(createLink(current, next, server, converting, converted) ){
							//logger.debug("Found neighbours of " + prec.getName() + " ("+ current.getName() + ") that reaches the server");
							logger.debug("New Link from " + prec.getName() + " to "+ current.getName() +" towards server "+server.getName());
							links.add(new Link(prec.getName(), current.getName()));
							converted.add(current.getName());
							found = true;
						}
						else{
							//logger.debug("Neighbour from " + current.getName() + " (" + neighbour +") don't reach the server");
						}
					}
				}
				converting.remove(current.getName());
			} catch (NoSuchElementException e) {
				throw new BadGraphError("Nodes must be in a chain",EType.INVALID_NODE_CHAIN);
			}
			return found;
		}
		/**
		 * Creates the routing table by adding the rules by exploring for each possible path between 
		 * an host client and host server all the possibles deploying scenarios for the nodes
		 * @param client the node client
		 * @param server the node server
		 * @throws BadGraphError
		 */
		private void createRoutingTables(Node client, Node server) throws BadGraphError{
			
			//System.out.println("Searching next hop for " + client.getName() + " towards " + server.getName());
			
			Link link = links.stream().filter(l -> l.getSourceNode().equals(client.getName())).findFirst().orElse(null);
			if(link == null){
				logger.error("Route: From CLIENT " + client.getName() 
									+ " to " + nctx.am.get(server.getName()) 
									+ " -> Dead End");
				throw new BadGraphError("Nodes must be in a chain",EType.INVALID_NODE_CHAIN);
			}
			String hostClient = hosts.stream().filter(h -> {return h.getType() == TypeOfHost.CLIENT;}).findFirst().get().getName();
			String hostServer = hosts.stream().filter(h -> {return h.getType() == TypeOfHost.SERVER;}).findFirst().get().getName();
			//System.out.println("The host client is: " + hostClient+" and the host server is "+hostServer);
			Node next = nodes.stream().filter(n -> n.getName().equals(link.getDestNode()) ).findFirst().get();
			//System.out.println("Route from CLIENT " + client.getName() 
			//								+ " to " + nctx.am.get(server.getIp()) 
			//								+ " -> next hop: " + netobjs.get(next));
			
			for(int i = 0; i < savedChain.size(); i++){
				String host1 = savedChain.get(i).get(1);
				//System.out.println("Chain -> " + savedChain.get(i));
				if(setNextHop(next, server, i, 1, hostServer)){
					/*System.out.println("Route from " + client.getName() 
					+ " to " + nctx.am.get(server.getIp()) 
					+ " -> next hop: " + netobjs.get(next));
					System.out.println("From " + hostClient + " to " + host1);
					*/
					//System.out.print("On RT("+next.getName()+") ");
					//System.out.println(next.getName()+"@"+host1);
					rawConditions.get(client).add(next.getName()+"@"+host1);
				}
			}	
			for(Node n : rawConditions.keySet()){
				ArrayList<RoutingTable> rt = new ArrayList<RoutingTable>();
				logger.debug("-----NODE "+n.getName()+"-----");
				List<String> cond = rawConditions.get(n).stream().distinct().collect(Collectors.toList());
				logger.debug("Condition for "+ n.getName() +" -> "+ cond);
				for(String s:cond){
					BoolExpr c;
					int latency = 0;
					if(s.lastIndexOf('/') != -1){
						String first = s.substring(0, s.lastIndexOf('/'));
						String second = s.substring(s.lastIndexOf('/')+1);
						String firstNode = first.substring(0,first.lastIndexOf('@'));
						String secondNode = second.substring(0, second.lastIndexOf('@'));
						String firstHost = first.substring(first.lastIndexOf('@')+1);
						String secondHost = second.substring(second.lastIndexOf('@')+1);
						if(firstHost.equals(secondHost)){
							latency = 0;
						}
						else{
							latency = connections.stream()
									.filter(con -> con.getSourceHost().equals(firstHost) && con.getDestHost().equals(secondHost))
									.findFirst().get().getAvgLatency();
						}
						logger.debug("Adding (" + first + " AND " + second+") to the routing table");
						c = ctx.mkAnd(ctx.mkBoolConst(first), ctx.mkBoolConst(second));
						if(n.getName().equals(firstNode)){
							conditionDB.get(n).put(firstHost, ctx.mkBoolConst(first));
						}
						if(n.getName().equals(secondNode)){
							conditionDB.get(n).put(secondHost, ctx.mkBoolConst(second));
						}
					}
					else{
						String host = s.substring(s.lastIndexOf('@')+1);
						if(n.getName().equals(client.getName())){
							latency = connections.stream()
									.filter(con -> con.getSourceHost().equals(hostClient) && con.getDestHost().equals(host))
									.findFirst().get().getAvgLatency();
						}
						else{
							latency = connections.stream()
									.filter(con -> con.getSourceHost().equals(host) && con.getDestHost().equals(hostServer))
									.findFirst().get().getAvgLatency();
						}
						logger.debug("Adding "+s+" to the routing table");
						c = ctx.mkBoolConst(s);
						if(n != client && n!= server){
							conditionDB.get(n).put(host, c);
						}
						
					}
					Link l = links.stream().filter(li -> li.getSourceNode().equals(n.getName())).findFirst().get();
					next = nodes.stream().filter(node -> node.getName().equals(l.getDestNode()) ).findFirst().get();
					rt.add(new RoutingTable(nctx.am.get(server.getName()), netobjs.get(next), nctx.addLatency(latency), c));
					
				}
				//System.out.println("Adding routing table to "+n.getName());
				net.routingOptimization(netobjs.get(n), rt);
			}
			logger.debug("----CONDITION DB----");
			conditionDB.entrySet().forEach(e -> {logger.debug(e.getKey().getName() + " -> " + e.getValue());});
			logger.debug("--------------------");
		}
		/**
		 * Explores recursively all the possible solution for setting a next hop condition
		 * @param source the node from which is exploring the solutions
		 * @param server the node server
		 * @param nChain number of the host chain on which it is trying to deploy all the nodes
		 * @param level on which host in the host chain it is trying to deploy the remaining nodes
		 * @param hostServer
		 * @return true if the last node has been deployed on the host server (good path), false otherwise
		 * @throws BadGraphError
		 */
		private boolean setNextHop(Node source, Node server, int nChain, int level, String hostServer) throws BadGraphError{
			String currentHost = savedChain.get(nChain).get(level);
			//logger.debug("Searching next hop for " + source.getName() + " towards " + server.getName());
			if(source.getName().equals(server.getName())){
				if(currentHost.equals(hostServer)){
					//logger.debug("Route from SERVER " + source.getName() + " to " + nctx.am.get(server.getName())  + " -> next hop: DESTINATION REACHED" + " CurrentHost: " + currentHost);
					//logger.debug("Found path from lv " + level + " of chain " +nChain );
					return true;
				}
				else{
					//logger.debug("Path not found path from lv " + level + " of chain " +nChain );
					return false;
				}
			}
			if(currentHost.equals(hostServer)){
				//logger.debug("Only server node can be deployed on server host -> tried to deploy " + source.getName() + " on " +currentHost );
				return false;
			}
			List<String> nextDest = links.stream().filter(l -> l.getSourceNode().equals(source.getName())).map(l -> l.getDestNode() ).collect(Collectors.toList());
			if(nextDest.size() == 0){
				logger.error("Route: From " + source.getName() 
									+ " to " + nctx.am.get(server.getName()) 
									+ " -> Dead End");
				throw new BadGraphError("Invalid leaf for the service graph",EType.INVALID_NODE_CHAIN);
			}
			/*logger.debug("Route: From " + source.getName() 
								+ " to " + nctx.am.get(server.getName()) 
								+ " -> Possible Next Hop "+nextDest);*/
			boolean found = false;
			for(String dest:nextDest){
				Node next = nodes.stream().filter(n -> n.getName().equals(dest)).findFirst().orElse(null);
				if(next == null){
					throw new BadGraphError("Incoherent service graph",EType.INVALID_NODE_CHAIN);
				}
				//logger.debug("Route from " + source.getName()+ " to " + nctx.am.get(server.getName())+ " -> next hop: " + netobjs.get(next));
				for(int i = level; i < savedChain.get(nChain).size() && i <= level+1; i++){
					String nextHost = savedChain.get(nChain).get(i);
					//logger.debug("RECURSION -> Deploying " + next.getName() +" on lv " + i + " of chain " +nChain +"("+nextHost+")");
					if(setNextHop(next, server, nChain, i, hostServer)){
						//logger.debug("From " + currentHost + " to " + nextHost);
						//logger.debug("On RT("+source.getName()+") ");
						if(nextHost.equals(hostServer)){
							//logger.debug("\t"+source.getName()+"@"+currentHost);
							rawConditions.get(source).add(source.getName()+"@"+currentHost);
						}
						else{
							//logger.debug("\t"+source.getName()+"@"+currentHost + " AND " + next.getName()+"@"+savedChain.get(nChain).get(i));
							rawConditions.get(source).add(source.getName()+"@"+currentHost + "/" + next.getName()+"@"+savedChain.get(nChain).get(i));
						}
						found = true;
					}
				}
			}
			return found;
		}
		/**
		 * Checks if the client node and the server node in a graph are reachable satisfying all the imposed conditions
		 * @return
		 */
		public IsolationResult checkNFFGProperty(){

            Node source = nodes.stream().filter(n -> {return n.getFunctionalType().equals(FunctionalTypes.MAILCLIENT)|| n.getFunctionalType().equals(FunctionalTypes.WEBCLIENT)|| n.getFunctionalType().equals(FunctionalTypes.ENDHOST);}).findFirst().get();
            Node dest = nodes.stream().filter(n -> {return n.getFunctionalType().equals(FunctionalTypes.MAILSERVER) || n.getFunctionalType().equals(FunctionalTypes.WEBSERVER);}).findFirst().get();
            logger.debug("Checking reachability from " + source.getName() + " to "+ dest.getName());
			IsolationResult ret = this.check.checkIsolationProperty(netobjs.get(source), netobjs.get(dest));
			if (ret.result == Status.UNSATISFIABLE){
				 	logger.debug("UNSAT"); // Nodes a and b are isolated
				 	logger.debug(ret.assertions.toString());
				 	
		    }else{
		    	 	logger.debug("SAT ");
		     		logger.debug( ""+ret.model); //p.printModel(ret.model);
		    }
			return ret;
		}
		/**
		 * Get Net Context
		 * @return the net context
		 */
		public NetContext getNctx() {
			return nctx;
		}
		
		
}

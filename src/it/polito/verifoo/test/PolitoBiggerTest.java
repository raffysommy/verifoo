/*
 * Copyright 2016 Politecnico di Torino
 * Authors:
 * Project Supervisor and Contact: Riccardo Sisto (riccardo.sisto@polito.it)
 * 
 * This file is part of Verigraph.
 * 
 * Verigraph is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of
 * the License, or (at your option) any later version.
 * 
 * Verigraph is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public
 * License along with Verigraph.  If not, see
 * <http://www.gnu.org/licenses/>.
 */
package it.polito.verifoo.test;


import java.util.ArrayList;

import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Context;
import com.microsoft.z3.DatatypeExpr;

import it.polito.verifoo.components.RoutingTable;
import it.polito.verigraph.mcnet.components.Checker;
import it.polito.verigraph.mcnet.components.NetContext;
import it.polito.verigraph.mcnet.components.Network;
import it.polito.verigraph.mcnet.components.NetworkObject;
import it.polito.verigraph.mcnet.components.Quattro;
import it.polito.verigraph.mcnet.components.Tuple;
import it.polito.verigraph.mcnet.netobjs.AclFirewall;
import it.polito.verigraph.mcnet.netobjs.Classifier;
import it.polito.verigraph.mcnet.netobjs.PolitoAntispam;
import it.polito.verigraph.mcnet.netobjs.PolitoCache;
import it.polito.verigraph.mcnet.netobjs.PolitoEndHost;
import it.polito.verigraph.mcnet.netobjs.PolitoMailClient;
import it.polito.verigraph.mcnet.netobjs.PolitoMailServer;




/**
 * @author Giacomo Costantini
 * <p/>
 * Antispam test													<p/>
 *| CLIENT | --------- | ANTISPAM | --------- | MAIL SERVER |		<p/>
 *..........................|										<p/>
 *...................| ERR FUNCTION |								<p/>
 */
public class PolitoBiggerTest {
	
	public Checker check;
	public AclFirewall x1;
	public AclFirewall x2;
	public AclFirewall x3;
	public PolitoEndHost a;
	public PolitoEndHost b;
	
	public int a_y1 = -10;
	public int a_y2 = -1;
	public int y1_y2= -2;
	public int y1_b= -10;
	public int y2_b= -100;
	
	
	public	PolitoBiggerTest(Context ctx){
	
		
			NetContext nctx = new NetContext (ctx,
					new String[]{"a", "x1","x2","x3", "b"},
					new String[]{"ip_a", "ip_x1", "ip_x2", "ip_x3", "ip_b"});
			Network net = new Network (ctx,new Object[]{nctx});
			
			
			x1 = new AclFirewall(ctx, new Object[]{nctx.nm.get("x1"), net, nctx});
			x2 = new AclFirewall(ctx, new Object[]{nctx.nm.get("x2"), net, nctx});
			x3 = new AclFirewall(ctx, new Object[]{nctx.nm.get("x3"), net, nctx});
			b = new PolitoEndHost(ctx, new Object[]{nctx.nm.get("b"), net, nctx});
			a = new PolitoEndHost(ctx, new Object[]{nctx.nm.get("a"), net, nctx});
		  
			ArrayList<Tuple<NetworkObject,ArrayList<DatatypeExpr>>> adm = new ArrayList<Tuple<NetworkObject,ArrayList<DatatypeExpr>>>();
			ArrayList<DatatypeExpr> al1 = new ArrayList<DatatypeExpr>();
  			ArrayList<DatatypeExpr> al2 = new ArrayList<DatatypeExpr>();
  			ArrayList<DatatypeExpr> al3 = new ArrayList<DatatypeExpr>();
  			ArrayList<DatatypeExpr> al5 = new ArrayList<DatatypeExpr>();
  			ArrayList<DatatypeExpr> al6 = new ArrayList<DatatypeExpr>();
  			
  			al1.add(nctx.am.get("ip_a"));
  			al2.add(nctx.am.get("ip_x1"));
  			al3.add(nctx.am.get("ip_b"));
  			al5.add(nctx.am.get("ip_x2"));
  			al6.add(nctx.am.get("ip_x3"));
			adm.add(new Tuple<>(a, al1));
		    adm.add(new Tuple<>(x1, al2));
		    adm.add(new Tuple<>(b, al3));
		    adm.add(new Tuple<>(x2, al5));
		    adm.add(new Tuple<>(x3, al6));

		    net.setAddressMappings(adm);

		    ArrayList<RoutingTable> rtClient = new ArrayList<RoutingTable>();
			rtClient.add(new RoutingTable(nctx.am.get("ip_b"), x1, nctx.addLatency(a_y1), nctx.x11));
			rtClient.add(new RoutingTable(nctx.am.get("ip_b"), x1, nctx.addLatency(a_y2), nctx.x12));
						
			ArrayList<RoutingTable> rtX1 = new ArrayList<RoutingTable>();
			rtX1.add(new RoutingTable(nctx.am.get("ip_b"), x2,nctx.addLatency(0),ctx.mkAnd(nctx.x11,nctx.x21)));
			rtX1.add(new RoutingTable(nctx.am.get("ip_b"), x2,nctx.addLatency(y1_y2),ctx.mkAnd(nctx.x11,nctx.x22)));
			rtX1.add(new RoutingTable(nctx.am.get("ip_b"), x2,nctx.addLatency(y1_y2),ctx.mkAnd(nctx.x12,nctx.x21)));
			rtX1.add(new RoutingTable(nctx.am.get("ip_b"), x2,nctx.addLatency(0),ctx.mkAnd(nctx.x12,nctx.x22)));
								
			ArrayList<RoutingTable> rtX2 = new ArrayList<RoutingTable>();
			rtX2.add(new RoutingTable(nctx.am.get("ip_b"), x3,nctx.addLatency(0),ctx.mkAnd(nctx.x21,nctx.x31)));
			rtX2.add(new RoutingTable(nctx.am.get("ip_b"), x3,nctx.addLatency(y1_y2),ctx.mkAnd(nctx.x21,nctx.x32)));
			rtX2.add(new RoutingTable(nctx.am.get("ip_b"), x3,nctx.addLatency(y1_y2),ctx.mkAnd(nctx.x22,nctx.x31)));
			rtX2.add(new RoutingTable(nctx.am.get("ip_b"), x3,nctx.addLatency(0),ctx.mkAnd(nctx.x22,nctx.x32)));
		
			ArrayList<RoutingTable> rtX3 = new ArrayList<RoutingTable>();
			rtX3.add(new RoutingTable(nctx.am.get("ip_b"), b,nctx.addLatency(y1_b),nctx.x31));
			rtX3.add(new RoutingTable(nctx.am.get("ip_b"), b,nctx.addLatency(y2_b),nctx.x32));
			
		
			ArrayList<RoutingTable> rtb = new ArrayList<RoutingTable>();
			
	    	net.routingOptimization(a, rtClient);
	    	net.routingOptimization(x1, rtX1);
	    	net.routingOptimization(x2, rtX2);
	    	net.routingOptimization(x3, rtX3);
	    	net.routingOptimization(b, rtb);
	    	
	    	ArrayList<Tuple<DatatypeExpr,DatatypeExpr>> acl = new ArrayList<Tuple<DatatypeExpr,DatatypeExpr>>();
	        x1.addAcls(acl);
	        x2.addAcls(acl);
	        x3.addAcls(acl);
	        acl.add(new Tuple<DatatypeExpr,DatatypeExpr>(nctx.am.get("ip_a"),nctx.am.get("ip_b")));
	        
		    net.attach(a, b, x1,x2,x3);
		    
		  
		    check = new Checker(ctx,nctx,net);
	}
}

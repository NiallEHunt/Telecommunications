package cs.tcd.ie;

import tcdIO.Terminal;

public class Assignment2Main
{
	// Assignment 2 Main is where all clients and routers are run. 
	// It's a hard-coded mess but it works the way it's intended to
	
	// To properly run this program first run the controller than run this class.
	// To change the destination port see the comments below
	
	static final int DEFAULT_SRC_PORT = 40000;
	
	static final int CLIENT0 = 40000;
	static final int CLIENT1 = 40001;
	static final int CLIENT2 = 40002;
	static final int CLIENT3 = 40003;

	static final int DEFAULT_GATEWAY_PORT = 50000;
	
	static final int ROUTER0 = 50000;
	static final int ROUTER1 = 50001;
	static final int ROUTER2 = 50002;	
	static final int ROUTER3 = 50003;
	static final int ROUTER4 = 50004;
	static final int ROUTER5 = 50005;

	static final int DEFAULT_CONTROLLER_PORT = 60001;
	static final String DEFAULT_DST_NODE = "localhost";

	public static void main(String[] args) throws Exception
	{
		Terminal routerTerminal0 = new Terminal("Router 0");
		Thread router0 = new Thread(
				new Router(routerTerminal0, ROUTER0, DEFAULT_CONTROLLER_PORT),
				"Router 1");

		Terminal routerTerminal1 = new Terminal("Router 1");
		Thread router1 = new Thread(
				new Router(routerTerminal1, ROUTER1, DEFAULT_CONTROLLER_PORT),
				"Router 2");

		Terminal routerTerminal2 = new Terminal("Router 2");
		Thread router2 = new Thread(
				new Router(routerTerminal2, ROUTER2, DEFAULT_CONTROLLER_PORT),
				"Router 3");
		
		Terminal routerTerminal3 = new Terminal("Router 3");
		Thread router3 = new Thread(
				new Router(routerTerminal3, ROUTER3, DEFAULT_CONTROLLER_PORT),
				"Router 3");
		
		Terminal routerTerminal4 = new Terminal("Router 4");
		Thread router4 = new Thread(
				new Router(routerTerminal4, ROUTER4, DEFAULT_CONTROLLER_PORT),
				"Router 3");
		
		Terminal routerTerminal5 = new Terminal("Router 5");
		Thread router5 = new Thread(
				new Router(routerTerminal5, ROUTER5, DEFAULT_CONTROLLER_PORT),
				"Router 3");

		Terminal terminal1 = new Terminal("Client 1");
		Terminal terminal2 = new Terminal("Client 2");
		Terminal terminal3 = new Terminal("Client 3");
		Terminal terminal4 = new Terminal("Client 4");

		// To change the destination port change the third parameter of the client constructor below
		Thread client1 = new Thread(
				new Client(terminal1, DEFAULT_DST_NODE, CLIENT3, CLIENT0, true), "Client 1");
		Thread client2 = new Thread(new Client(terminal2, DEFAULT_DST_NODE, CLIENT0, CLIENT1),
				"Client 2");
		Thread client3 = new Thread(
				new Client(terminal3, DEFAULT_DST_NODE, CLIENT3, CLIENT2), "Client 1");
		Thread client4 = new Thread(new Client(terminal4, DEFAULT_DST_NODE, CLIENT2, CLIENT3),
				"Client 2");

		client1.run();
		client2.run();
		client3.run();
		client4.run();

		router0.start();
		router1.start();
		router2.start();
		router3.start();
		router4.start();
		router5.start();
	}
}

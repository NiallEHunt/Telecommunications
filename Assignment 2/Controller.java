package cs.tcd.ie;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.nio.ByteBuffer;
import java.util.Arrays;

import tcdIO.Terminal;

public class Controller extends Node implements Runnable
{
	// Run this first then run the Assignment2Main class 
	
	static final int NUMBER_OF_NODES = 10;
	static final int DEFAULT_CONTROLLER_PORT = 60001;
	static final String DEFAULT_DST_NODE = "localhost";
	
	Terminal terminal;
	
	//    Vertices = {C0, C1, C2, C3, R0, R1, R2, R3, R4, R5} TODO: fix comment
	//				 {0, 1, 2, 3, 4, 5, 6, 7, 8, 9}
	// So the path 0 -> 4 -> 7 -> 5 corresponds to C0 -> R0 -> R3 -> C2
	private final int[] ports = {40000, 40001, 40002, 40003, 50000, 50001, 50002, 50003, 50004, 50005};
	
	private BreadthFirstPaths bfp;
	private Graph networkGraph;
	
	Controller(Terminal terminal, String dstHost, int controllerPort)
	{
		try
		{
			socket= new DatagramSocket(controllerPort);
			listener.go();
		}
		catch(java.lang.Exception e) {e.printStackTrace();}

		this.terminal = terminal;
		
		// The rest of the constructor is the hard-coded graphing of the network
		Graph networkGraph = new Graph(NUMBER_OF_NODES);
		int w = 0;
		int v = 0;
		for(v = 4;(v < 9);v++)
		{
			w = v + 1;
			if(v != 6)
				networkGraph.addEdge(v, w);
		}
		
		for(v = 4;(v <= 6);v++)
		{
			w = v + 3;
			networkGraph.addEdge(v, w);
		}
		
		networkGraph.addEdge(0, 4);
		networkGraph.addEdge(1, 6);
		networkGraph.addEdge(2, 7);
		networkGraph.addEdge(3, 9);
		
		System.out.println(networkGraph.toString());
		this.networkGraph = networkGraph;
	}

	public void onReceipt(DatagramPacket packet) 
	{
		terminal.println("Recieved packet");
		byte[] header = Header.getHeader(packet);
		Header.removeHeader(packet);
		
		byte[] dstPortArray = new byte[Header.BYTE_SIZE_OF_PORTS];
		System.arraycopy(header, 0, dstPortArray, 0, Header.BYTE_SIZE_OF_PORTS);
		int dstPort = ByteBuffer.wrap(dstPortArray).getInt();
		
		byte[] srcPortArray = new byte[Header.BYTE_SIZE_OF_PORTS];
		System.arraycopy(header, Header.BYTE_SIZE_OF_PORTS, srcPortArray, 0, Header.BYTE_SIZE_OF_PORTS);
		int srcPort = ByteBuffer.wrap(srcPortArray).getInt();
		
		terminal.println("From port " + srcPort + "\n");
		
		int srcIndex = Arrays.binarySearch(ports, srcPort);
		bfp = new BreadthFirstPaths(networkGraph, srcIndex);
		
		int dstIndex = Arrays.binarySearch(ports, dstPort);
		
		if(dstIndex < 0 || dstIndex >= ports.length)
		{
			System.out.print("Error in finding destination");
			System.exit(1);
		}
		
		Iterable<Integer> path = bfp.pathTo(dstIndex);
		
		int[] pathPorts = new int[bfp.distTo(dstIndex) + 1];
		int index = 0;
		for(int i : path)
		{
			System.out.println(i);
			pathPorts[index] = ports[i];
			System.out.println(pathPorts[index]);
			index++;
		}
		
		for(int i = 0;(i < pathPorts.length);i++)
		{
			packet = Header.addHeader(Header.makeHeader(pathPorts[i], srcPort, 1), packet);
		}
		
		terminal.println("Sending packet");
		
		packet = Header.removeHeader(packet);
		packet.setPort(pathPorts[pathPorts.length - 2]);
		try
		{
			socket.send(packet);
			terminal.println("Packet sent");
		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	public synchronized void start() throws Exception
	{
		terminal.println("Waiting for contact\n");
		this.wait();
	}

	public void run() 
	{		
		try {
			start();
		} catch (Exception e) { e.printStackTrace(); }
	}
	
	public static void main(String args[])
	{
		Terminal terminal = new Terminal("Controller");
		Thread controllerThread = new Thread(new Controller(terminal, DEFAULT_DST_NODE, DEFAULT_CONTROLLER_PORT));
		controllerThread.run();
	}
}

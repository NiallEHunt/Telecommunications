package cs.tcd.ie;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.nio.ByteBuffer;

import tcdIO.Terminal;

public class Router extends Node implements Runnable
{	
	private int controllerPort;
	
	Terminal terminal;
	
	Router(Terminal terminal, int routerPort, int controllerPort)
	{
		this.terminal = terminal;
		try 
		{
			socket = new DatagramSocket(routerPort);
			
			this.controllerPort = controllerPort;

			listener.go();
		} catch (SocketException e) {e.printStackTrace();}
	}
	
	public void onReceipt(DatagramPacket packet) 
	{
		terminal.println("Packet recieved");

		byte[] header = Header.getHeader(packet);
		
		byte[] dstPortArray = new byte[Header.BYTE_SIZE_OF_PORTS];
		System.arraycopy(header, 0, dstPortArray, 0, Header.BYTE_SIZE_OF_PORTS);
		int dstPort = ByteBuffer.wrap(dstPortArray).getInt();
		
		byte[] knownPathArray = new byte[Header.BYTE_SIZE_OF_KNOWN_PATH];
		System.arraycopy(header, (2 * Header.BYTE_SIZE_OF_PORTS), knownPathArray, 0, Header.BYTE_SIZE_OF_KNOWN_PATH);
		int knownPath = ByteBuffer.wrap(knownPathArray).getInt();
		
		if(knownPath == 0)
		{
			packet.setPort(controllerPort);
			terminal.println("Packet sent to controller\n");
			try 
			{
				socket.send(packet);
			} 
			catch (IOException e) {e.printStackTrace();}
		}
		else
		{
			packet = Header.removeHeader(packet);
			packet.setPort(dstPort);
			terminal.println("Packet sent to " + dstPort);
			try 
			{
				socket.send(packet);
			} 
			catch (IOException e) {e.printStackTrace();}
		}
	}

	public synchronized void start() throws Exception
	{
		terminal.println("Waiting for contact\n");
		this.wait();
	}
	
	public void run()
	{
		try { start(); } 
		catch (Exception e) { e.printStackTrace(); }
	}
}

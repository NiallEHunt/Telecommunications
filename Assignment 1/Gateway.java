package cs.tcd.ie;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.nio.ByteBuffer;

import tcdIO.Terminal;

public class Gateway extends Node
{
	static final int DEFAULT_SERVER_PORT = 60001;
	static final int DEFAULT_GATEWAY_PORT = 60000;
	static final String DEFAULT_DST_NODE = "localhost";
	
	Terminal terminal;
	InetSocketAddress dstAddress;
	
	Gateway(Terminal terminal)
	{
		this.terminal = terminal;
		try 
		{
			socket = new DatagramSocket(DEFAULT_GATEWAY_PORT);
			listener.go();
		} catch (SocketException e) {e.printStackTrace();}
	}
	
	public void onReceipt(DatagramPacket packet) 
	{
		StringContent content = new StringContent(packet);
		terminal.println("Packet being transmitted:");
		terminal.println(content.toString() + "\n");
		
		System.out.println("Port: " + packet.getPort());
		byte[] data = packet.getData();
		
		byte[] dstPortArray = new byte[PacketContent.BYTE_SIZE_OF_PORTS];
		System.arraycopy(data, 0, dstPortArray, 0, PacketContent.BYTE_SIZE_OF_PORTS);
		int dstPort = ByteBuffer.wrap(dstPortArray).getInt();
		System.out.println("Destination port: " + dstPort);
		
		packet.setPort(dstPort);
		try 
		{
			socket.send(packet);
		} 
		catch (IOException e) {e.printStackTrace();}
	}

	public synchronized void start() throws Exception
	{
		terminal.println("Waiting for contact");
		this.wait();
	}
	
	public static void main(String[] args) 
	{
		try
		{
			Terminal terminal = new Terminal("Gateway");
			(new Gateway(terminal)).start();
		}
		catch(java.lang.Exception e) {e.printStackTrace();}
	}
}

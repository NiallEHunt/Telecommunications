package cs.tcd.ie;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;

import tcdIO.Terminal;

public class Client extends Node implements Runnable
{
	static final int DEFAULT_SRC_PORT = 40000;
	static final int DEFAULT_ROUTER_PORT = 50000;
	static final int DEFAULT_CONTOLLER_PORT = 60001;
	static final String DEFAULT_DST_NODE = "localhost";

	Terminal terminal;
	InetSocketAddress dstAddress;

	private int dstPort, srcPort;
	private boolean sender;

	Client(Terminal terminal, String dstHost, int dstPort, int srcPort, boolean sender)
	{
		this(terminal, dstHost, dstPort, srcPort, sender, DEFAULT_ROUTER_PORT);
	}

	Client(Terminal terminal, String dstHost, int dstPort, int srcPort)
	{
		this(terminal, dstHost, dstPort, srcPort, false, DEFAULT_ROUTER_PORT);
	}

	Client(Terminal terminal, String dstHost, int dstPort, int srcPort, boolean sender, int routerPort)
	{
		Boolean validPort = false;
		while (!validPort)
		{
			try
			{
				this.terminal = terminal;
				dstAddress = new InetSocketAddress(dstHost, routerPort);
				socket = new DatagramSocket(srcPort);

				validPort = true;
				this.dstPort = dstPort;
				this.srcPort = srcPort;
				this.sender = sender;

				listener.go();
			} catch (java.net.BindException e)
			{
				srcPort++;
				if (srcPort >= dstPort)
				{
					System.out.println("Maximum number of clients reached");
					System.exit(1);
				}
			} catch (java.lang.Exception e)
			{
				e.printStackTrace();
			}
		}
	}

	public synchronized void onReceipt(DatagramPacket packet)
	{
		StringContent content = new StringContent(packet);
		this.notify();
		terminal.println(content.toString());
	}

	public synchronized void start() throws Exception
	{
		if (this.sender)
			sendPacket("Packet, packet, packet!");
	}

	public void run()
	{
		try
		{
			start();
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public void sendPacket(String packetcontent) throws IOException
	{
		DatagramPacket packet = null;

		byte[] payload = null;
		byte[] header = null;
		byte[] buffer = null;

		terminal.println("Ready ....\nPort number: " + srcPort + "\n");

		payload = packetcontent.getBytes();

		header = Header.makeHeader(dstPort, srcPort, 0);

		buffer = new byte[header.length + payload.length];
		System.arraycopy(header, 0, buffer, 0, header.length);
		System.arraycopy(payload, 0, buffer, header.length, payload.length);

		terminal.println("Sending packet...");
		packet = new DatagramPacket(buffer, buffer.length, dstAddress);
		
		socket.send(packet);
		terminal.println("Packet sent");
	}
}

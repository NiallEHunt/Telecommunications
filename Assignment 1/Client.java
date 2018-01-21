/**
 * 
 */
package cs.tcd.ie;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;

import tcdIO.*;

/**
 *
 * Client class
 * 
 * An instance accepts user input 
 *
 */
public class Client extends Node 
{
	static final int DEFAULT_SRC_PORT = 50000;
	static final int DEFAULT_GATEWAY_PORT = 60000;
	static final int DEFAULT_SERVER_PORT = 60001;
	static final String DEFAULT_DST_NODE = "localhost";	
	
	Terminal terminal;
	InetSocketAddress dstAddress;
	
	private int dstPort,srcPort, sequenceNumber;
	private boolean recieved;	
	/**
	 * Constructor
	 * 	 
	 * Attempts to create socket at given port and create an InetSocketAddress for the destinations
	 */
	Client(Terminal terminal, String dstHost, int dstPort, int srcPort) 
	{
		Boolean validPort = false;
		while(!validPort)
		{
			try {
				this.recieved = false;
				this.terminal= terminal;
				dstAddress= new InetSocketAddress(dstHost, DEFAULT_GATEWAY_PORT);
				socket= new DatagramSocket(srcPort);
				
				validPort = true;
				System.out.println("Source port: " + srcPort);
				this.dstPort = dstPort;
				this.srcPort = srcPort;
				this.sequenceNumber = 0;
				
				listener.go();
			}
			catch(java.net.BindException e)
			{
				srcPort++;
				if(srcPort >= dstPort)
				{
					System.out.println("Maximum number of clients reached");
					System.exit(1);
				}
			}
			catch(java.lang.Exception e) {e.printStackTrace();}
		}
	}

	
	/**
	 * Assume that incoming packets contain a String and print the string.
	 */
	public synchronized void onReceipt(DatagramPacket packet)
	{
		this.recieved = true;
		StringContent content= new StringContent(packet);
		this.notify();
		terminal.println(content.toString());
		
		byte[] data = packet.getData();
		byte[] sequenceArray = new byte[PacketContent.BYTE_SIZE_OF_SEQUENCE_NUMBER];
		System.arraycopy(data, (2 * PacketContent.BYTE_SIZE_OF_PORTS), sequenceArray, 0, PacketContent.BYTE_SIZE_OF_SEQUENCE_NUMBER);
		int sequenceNumber = ByteBuffer.wrap(sequenceArray).getInt();
		System.out.println("Sequence Number: "  + sequenceNumber);
	}

	
	/**
	 * Sender Method
	 * 
	 */
	public synchronized void start() throws Exception 
	{
		DatagramPacket packet= null;

		byte[] payload= null;
		byte[] header= null;
		byte[] buffer= null;
		
			payload= (terminal.readString("String to send: ")).getBytes();

			header= new byte[PacketContent.HEADERLENGTH];
			
			byte[] dstPortArray = ByteBuffer.allocate(PacketContent.BYTE_SIZE_OF_PORTS).putInt(dstPort).array();
			System.arraycopy(dstPortArray, 0, header, 0, PacketContent.BYTE_SIZE_OF_PORTS);
			
			byte[] srcPortArray = ByteBuffer.allocate(PacketContent.BYTE_SIZE_OF_PORTS).putInt(srcPort).array();
			System.arraycopy(srcPortArray, 0, header, PacketContent.BYTE_SIZE_OF_PORTS, PacketContent.BYTE_SIZE_OF_PORTS);
			
			byte[] sequenceNumberArray = ByteBuffer.allocate(PacketContent.BYTE_SIZE_OF_SEQUENCE_NUMBER).putInt(sequenceNumber).array();
			System.arraycopy(sequenceNumberArray, 0, header, (2 * PacketContent.BYTE_SIZE_OF_PORTS), PacketContent.BYTE_SIZE_OF_SEQUENCE_NUMBER);

			buffer= new byte[header.length + payload.length];
			System.arraycopy(header, 0, buffer, 0, header.length);
			System.arraycopy(payload, 0, buffer, header.length, payload.length);
			
			terminal.println("Sending packet...");
			packet= new DatagramPacket(buffer, buffer.length, dstAddress);
			while(!recieved)
			{
				socket.send(packet);
				terminal.println("Packet sent");
			
				this.wait(200);
			}
	}


	/**
	 * Test method
	 * 
	 * Sends a packet to a given address
	 */
	public static void main(String[] args) 
	{
		try {					
			Terminal terminal= new Terminal("Client");		
			(new Client(terminal, DEFAULT_DST_NODE, DEFAULT_SERVER_PORT, DEFAULT_SRC_PORT)).start();
			terminal.println("Program completed");
		} catch(java.lang.Exception e) {e.printStackTrace();}
	}
}

package cs.tcd.ie;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;

import tcdIO.Terminal;

public class Server extends Node 
{
	static final int DEFAULT_PORT = 60001;
	static final int DEFAULT_GATEWAY_PORT = 60000;

	static final String DEFAULT_DST_NODE = "localhost";	

	Terminal terminal;
	
	/*
	 * 
	 */
	Server(Terminal terminal, int port)
	{
		try {
			this.terminal= terminal;
			socket= new DatagramSocket(port);
			listener.go();
		}
		catch(java.lang.Exception e) {e.printStackTrace();}
	}

	/**
	 * Assume that incoming packets contain a String and print the string.
	 */
	public void onReceipt(DatagramPacket recievedPacket) 
	{
		try 
		{
			DatagramPacket response= null;

			byte[] payload= null;
			byte[] buffer= null;
			byte[] header = new byte[PacketContent.HEADERLENGTH];

			byte[] data = recievedPacket.getData();
			byte[] headerOfRecievedPacket = new byte[PacketContent.HEADERLENGTH];
			System.arraycopy(data, 0, headerOfRecievedPacket, 0, PacketContent.HEADERLENGTH);
			
			byte[] dstPortArray = new byte[PacketContent.BYTE_SIZE_OF_PORTS];
			System.arraycopy(headerOfRecievedPacket, PacketContent.BYTE_SIZE_OF_PORTS, dstPortArray, 0, PacketContent.BYTE_SIZE_OF_PORTS);
			
			System.arraycopy(dstPortArray, 0, header, 0, PacketContent.BYTE_SIZE_OF_PORTS);
			
			byte[] srcPortArray = ByteBuffer.allocate(PacketContent.BYTE_SIZE_OF_PORTS).putInt(DEFAULT_PORT).array();
			System.arraycopy(srcPortArray, 0, header, PacketContent.BYTE_SIZE_OF_PORTS, PacketContent.BYTE_SIZE_OF_PORTS);
			
			byte[] recievedSequenceNumberArray = new byte[PacketContent.BYTE_SIZE_OF_SEQUENCE_NUMBER];
			System.arraycopy(header, (2 * PacketContent.BYTE_SIZE_OF_PORTS), recievedSequenceNumberArray, 0, PacketContent.BYTE_SIZE_OF_SEQUENCE_NUMBER);
			int sequenceNumber = (ByteBuffer.wrap(recievedSequenceNumberArray).getInt()) + 1;
			
			byte[] sequenceNumberArray = ByteBuffer.allocate(PacketContent.BYTE_SIZE_OF_SEQUENCE_NUMBER).putInt(sequenceNumber).array();
			System.arraycopy(sequenceNumberArray, 0, header, (2 * PacketContent.BYTE_SIZE_OF_PORTS), PacketContent.BYTE_SIZE_OF_SEQUENCE_NUMBER);
			
			if(sequenceNumber != 0)
				payload = ("Wrong packet".getBytes());
			else
				payload= ("Ok!".getBytes());
			
			buffer= new byte[header.length + payload.length];
			System.arraycopy(header, 0, buffer, 0, header.length);
			System.arraycopy(payload, 0, buffer, header.length, payload.length);
			
			response = new DatagramPacket(buffer, buffer.length, new InetSocketAddress(DEFAULT_DST_NODE, DEFAULT_GATEWAY_PORT));
			
			StringContent content= new StringContent(recievedPacket);

			terminal.println(content.toString());

			socket.send(response);
		}
		catch(Exception e) {e.printStackTrace();}
	}

	
	public synchronized void start() throws Exception
	{
		terminal.println("Waiting for contact");
		this.wait();
	}
	
	/*
	 * 
	 */
	public static void main(String[] args) 
	{
		try {					
			Terminal terminal= new Terminal("Server");
			(new Server(terminal, DEFAULT_PORT)).start();
			terminal.println("Program completed");
		} catch(java.lang.Exception e) {e.printStackTrace();}
	}
}

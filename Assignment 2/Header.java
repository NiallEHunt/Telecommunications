package cs.tcd.ie;

import java.net.DatagramPacket;
import java.nio.ByteBuffer;

public class Header
{
	public static byte HEADERLENGTH = 12;
	public static final int BYTE_SIZE_OF_PORTS = 4;
	public static final int BYTE_SIZE_OF_KNOWN_PATH = 4;
	
	public static byte[] makeHeader(int dstPort, int srcPort, int knownPath)
	{
		byte[] header = new byte[HEADERLENGTH];

		byte[] dstPortArray = ByteBuffer.allocate(BYTE_SIZE_OF_PORTS).putInt(dstPort).array();
		System.arraycopy(dstPortArray, 0, header, 0, BYTE_SIZE_OF_PORTS);

		byte[] srcPortArray = ByteBuffer.allocate(BYTE_SIZE_OF_PORTS).putInt(srcPort).array();
		System.arraycopy(srcPortArray, 0, header, BYTE_SIZE_OF_PORTS, BYTE_SIZE_OF_PORTS);

		byte[] knownPathArray = ByteBuffer.allocate(BYTE_SIZE_OF_KNOWN_PATH).putInt(knownPath).array();
		System.arraycopy(knownPathArray, 0, header, (2 * BYTE_SIZE_OF_PORTS),
				BYTE_SIZE_OF_KNOWN_PATH);
		
		return header;
	}
	
	public static DatagramPacket addHeader(byte[] header, DatagramPacket packet)
	{
		byte[] buffer = new byte[packet.getLength() + HEADERLENGTH];
		byte[] data = packet.getData();
		
		System.arraycopy(header, 0, buffer, 0, HEADERLENGTH);
		System.arraycopy(data, 0, buffer, HEADERLENGTH, packet.getLength());
		System.out.println();
		
		return new DatagramPacket(buffer, buffer.length, packet.getSocketAddress());
	}
	
	public static byte[] getHeader(DatagramPacket packet)
	{
		byte[] data = packet.getData();
		byte[] header = new byte[HEADERLENGTH];
		System.arraycopy(data, 0, header, 0, HEADERLENGTH);
		
		return header;
	}
	
	public static DatagramPacket removeHeader(DatagramPacket packet)
	{
		byte[] data = packet.getData();
		byte[] buffer = new byte[packet.getLength() - HEADERLENGTH];
		System.arraycopy(data, HEADERLENGTH, buffer, 0, packet.getLength() - HEADERLENGTH);
		
		return new DatagramPacket(buffer, buffer.length, packet.getSocketAddress());
	}
}

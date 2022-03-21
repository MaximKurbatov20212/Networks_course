import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.Arrays;

public class Server {
    private final DatagramSocket socket;
    private int port;
    private final int maxSizeOfMessage = (65507 - 20) / 2;
    private byte[] buf = new byte[maxSizeOfMessage];

    private QueueOfPackets queueOfPackets = new QueueOfPackets();

    // Number of received packet
    private long numberOfPacket = 0;

    public Server() throws SocketException {
        port = 4000;
        socket = new DatagramSocket(port);
    }

    public void waitForTheMessage() {
        while (true) {
            try {
                Arrays.fill(buf, (byte)0);
                // Receive packet
                DatagramPacket packet = new DatagramPacket(buf, buf.length);
                socket.receive(packet);

                // Rebuild packet
                packet = new DatagramPacket(buf, buf.length, packet.getAddress(), packet.getPort());
                String received = new String(packet.getData(), 0, packet.getLength());
                System.out.println("Received[" + numberOfPacket +"]: " + received);

                if(packetWasLost()) {
                    simulationLostPacket();
                }
                else {
                    // queue is not necessary if Server responses only one host, but I implemented it:)
                    queueOfPackets.addPacket(packet);
                    // Zero packet - connection request
                    if(numberOfPacket == 0) {
                        System.out.println("Connection accepted, host: " + packet.getAddress());
                    }
                    numberOfPacket++;
                    sendBack(packet);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void sendBack(DatagramPacket packet) throws IOException {
        System.out.println("Send: confirmation\n");
        socket.send(packet);
        Arrays.fill(buf, (byte)0); // reset buffer
    }

    private void simulationLostPacket() {
        System.out.println("Packet is lost");
    }

    private boolean packetWasLost() {
        return Math.random() > 0.2;
    }

    public static void main(String[] args) throws Exception {
        Server server = new Server();
        server.waitForTheMessage();
    }
}

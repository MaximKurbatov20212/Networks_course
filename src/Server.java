import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.Arrays;

public class Server {
    private final int maxSizeOfMessage = 1024;
    private final DatagramSocket socket;
    private byte[] buf = new byte[maxSizeOfMessage];
    private int port;

    // Number of received packet
    private long numberOfPacket = 0;

    public Server() throws SocketException {
        port = 4000;
        socket = new DatagramSocket(port);
    }

    public void startServer() {
        while (true) {
            try {
                // Receive packet
                DatagramPacket packet = new DatagramPacket(buf, buf.length);
                socket.receive(packet);

                // Rebuild packet
                packet = new DatagramPacket(buf, buf.length, packet.getAddress(), packet.getPort());
                String received = new String(packet.getData(), 0, packet.getLength());
                System.out.println("Received[" + (numberOfPacket++) +"]: " + received);

                if(isStopPacket(received)) {
                    return;
                }

                if(packetWasLost()) {
                    simulationLostPacket();
                }
                else {
                    if(numberOfPacket == 0) {
                        System.out.println("Connection accepted, host: " + packet.getAddress());
                    }
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
        Arrays.fill(buf, (byte)0);
    }

    private boolean isStopPacket(String received) {
        return received.equals("end");
    }

    private void simulationLostPacket() {
        System.out.println("Packet is lost");
    }

    private boolean packetWasLost() {
        return Math.random() < 0.8;
    }

    public static void main(String[] args) throws Exception {
        Server server = new Server();
        server.startServer();
    }
}

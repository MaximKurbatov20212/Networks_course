import java.io.IOException;
import java.net.*;
import java.util.Objects;
import java.util.Scanner;

public class Client {
    private final DatagramSocket socket;
    private InetAddress address;
    private final int port = 4000;
    private byte[] buf;
    private int timeDelay;
    private final int maxNumberAttempts = 3; // we can set it if we want

    private final int MAX_LEN_OF_MESSAGE = (65507 - 20) / 2;
    private DatagramPacket currentSendingPacket;
    private DatagramPacket currentReceivingPacket;

    public Client() throws  SocketException, UnknownHostException {
        socket = new DatagramSocket();
//        address = InetAddress.getByName("192.168.43.55");
        address = InetAddress.getLocalHost();
        setMinDelay();
    }

    // Returns: message if delivery confirmed, otherwise null
    public String rdtSend(String msg) throws IOException {
        setMinDelay();
        currentSendingPacket = makePacket(msg);
        System.out.println("You sent packet with data: " + msg);
        for(int i = 0; i < maxNumberAttempts; i++) {
            socket.send(currentSendingPacket);
            boolean isConfirmed = getConfirmDelivery();

            // Пытаемся получить подтверждение
            if(isConfirmed) {
                System.out.println("Delivery confirmed\n");
                return new String(currentReceivingPacket.getData(), 0, currentReceivingPacket.getLength());
            }

            if(i != maxNumberAttempts - 1) {
                System.out.println("I am trying send again: " + (i + 1) + " attempt(s)");
            }
        }
        System.out.println("The server does not respond\n");
        return null;
    }

    // Makes packet and returns it
    private DatagramPacket makePacket(String msg) {
        buf = msg.getBytes();
        return new DatagramPacket(buf, buf.length, address, port);
    }

    // Returns true if server responses us, otherwise increases time delay and returns false
    private boolean getConfirmDelivery() throws IOException {
        if(deliveryConfirmed()) {
            return true;
        }
        increaseTimeDelay();
        return false;
    }

    // Sets time to receive packet and receives it
    // Returns true if packet is received, otherwise false
    private boolean deliveryConfirmed()  {
        currentReceivingPacket = new DatagramPacket(buf, buf.length);
        try {
            socket.setSoTimeout(timeDelay); // set time after which we consider packet was lost
            socket.receive(currentReceivingPacket);
            return true;
        } catch (SocketTimeoutException e) {
            System.out.println("Receive time out expired: " + timeDelay + " ms");
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Establish connection with host which has address defined in constructor
    public boolean establishConnection() throws IOException {
        Scanner scanner = new Scanner(System.in);
        while(true) {
            System.out.print("Do you want to establish connection with local host?(yes/no): ");
            String data = scanner.nextLine();
            System.out.println();
            switch (data) {
                case("yes"): {
                    String answer = rdtSend("I want to establish connection");
                    if(answer != null) {
                        System.out.println("Connection establish successfully, dest host: " + address + "\n");
                        return true;
                    }
                    break;
                }
                case("no"): {
                    System.out.println("Hmmmmmmm\n");
                    break;
                }
                default:
                    System.out.println("Uncorrected. Type yes/no\n");
            }
        }
    }

    // Gets message from standard input stream and gives it to send
    // "end" is stopstring
    private void talkToHost() throws IOException {
        Scanner scanner = new Scanner(System.in);
        String message;
        System.out.print("Type message: ");
        while(!Objects.equals(message = scanner.nextLine(), "end")) {
            for(int i = 0; i <= message.length() / MAX_LEN_OF_MESSAGE; i++) {
                String piece_of_message = message.substring(i * MAX_LEN_OF_MESSAGE, Math.min(i * MAX_LEN_OF_MESSAGE + MAX_LEN_OF_MESSAGE, message.length()));
                rdtSend(piece_of_message);
            }
            System.out.print("Type message: ");
        }
    }

    private void close() {
        socket.close();
    }

    public void setMinDelay() {
        timeDelay = 50;
    }

    private void increaseTimeDelay() {
        timeDelay *= 2;
    }

    public static void main(String[] args) throws Exception {
        Client client = new Client();

        boolean answer;
        do {
            answer = client.establishConnection();
        } while (!answer);

        client.talkToHost();
    }
}
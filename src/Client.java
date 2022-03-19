import java.io.IOException;
import java.net.*;
import java.util.Objects;
import java.util.Scanner;

public class Client {
    private final DatagramSocket socket;
    private InetAddress address;
    private final int port = 4000;
    private byte[] buf;
    private int timeDelay = 50;
    private final int numberOfTryingReceive = 5;

    private final int MAX_LEN_OF_MESSAGE = 50;
    private DatagramPacket currentSendingPacket;
    private DatagramPacket currentReceivingPacket;

    public Client() throws SocketException, UnknownHostException {
        socket = new DatagramSocket();
        address = InetAddress.getByName("192.168.43.55");
    }

    // Return: message if delivery confirmed, otherwise null
    public String rdtSend(String msg) throws IOException {
        timeDelay = 50; //
        makePacket(msg);

        System.out.println("You sent packet with data: " + msg);
        socket.send(currentSendingPacket);

        // Пытаемся получить подтверждение
        boolean isConfirmed = getConfirmDelivery();
        if(!isConfirmed) {
            return null;
        }
        System.out.println("Delivery confirmed\n");
        return new String(currentReceivingPacket.getData(), 0, currentReceivingPacket.getLength());
    }

    private DatagramPacket makePacket(String msg) {
        buf = msg.getBytes();
        currentSendingPacket = new DatagramPacket(buf, buf.length, address, port);
        return currentSendingPacket;
    }

    private boolean getConfirmDelivery() throws IOException {
        for(int i = 0; i < numberOfTryingReceive; i++) {
            if(deliveryConfirmed()) {
                return true;
            }
            System.out.println("I am trying send again: " + (i + 1) + " attempt(s)");
            socket.send(currentSendingPacket);
            timeDelay *= 2;
        }
        System.out.println("The server does not respond\n");
        return false;
    }

    private boolean deliveryConfirmed()  {
        currentReceivingPacket = new DatagramPacket(buf, buf.length);
        try {
            socket.setSoTimeout(timeDelay);
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

    public boolean establishConnection() throws IOException {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Are you want to establish connection with local host?(yes/no): ");

        while(true) {
            String data = scanner.nextLine();
            System.out.println();
            if(Objects.equals(data, "yes")) {
                String answer = rdtSend("I want to establish connection");
                if(answer != null) {
                    System.out.println("Connection establish successfully, dest host: " + address + "\n");
                    return true;
                }
            }
            else if(Objects.equals(data, "no")) {
                System.out.println("Hmmmmmmm\n");
            }
            else {
                System.out.println("Uncorrected. Type yes/no\n");
            }
            return false;
        }
    }

    private void talkWithHost() throws IOException {
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

    public void close() {
        socket.close();
    }

    public static void main(String[] args) throws Exception {
        Client client = new Client();
        while(!client.establishConnection()) {}
        client.talkWithHost();
    }
}
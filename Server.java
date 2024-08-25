import java.io.*;
import java.net.*;
import java.util.Scanner;

class Server {
    public static void main(String[] args) {
        ServerSocket server = null;
        int count = 0;
        try {
            server = new ServerSocket(1104); // สร้าง ServerSocket เพื่อรองัย client ที่เชื่อมต่อมาที่พอร์ต 1104
            server.setReuseAddress(true); // ตั้งค่าให้ ServerSocket ใช้ที่อยู่ซ้ำได้
            System.out.println("open server"); // พิมพ์ข้อความบอกว่าเซิร์ฟเวอร์เปิดแล้ว

            while (true) {
                count++;
                Socket client = server.accept(); // รอรับการเชื่อมต่อจาก client และสร้าง Socket เพื่อจัดการกับ client นั้น

                System.out.println("New client connected " + count + " " + client.getInetAddress().getHostAddress());
                // พิมพ์ข้อความบอกว่ามี client เชื่อมต่อมาแล้วพร้อมกับ IP address ของ client

                ClientHandler clientSock = new ClientHandler(client); // สร้าง object ClientHandler ใหม่เพื่อจัดการกับ client นี้
                new Thread(clientSock).start(); // เริ่ม thread ใหม่เพื่อรันการจัดการกับ client นี้
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (server != null) {
                try {
                    server.close(); // ปิด ServerSocket เมื่อไม่ต้องการใช้งานแล้ว
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static class ClientHandler implements Runnable {
        private final Socket clientSocket;

        public ClientHandler(Socket socket) {
            this.clientSocket = socket; // เก็บ reference ไปยัง Socket ของ client ที่เชื่อมต่อ
        }

        public void run() {
            try {
                DataInputStream inStream = new DataInputStream(clientSocket.getInputStream());
                DataOutputStream outStream = new DataOutputStream(clientSocket.getOutputStream());
                // สร้าง input/output streams เพื่อรับส่งข้อมูลกับ client

                String filePath = "C:/Users/Raum/Desktop/OS-20230811T170147Z-001/OS/Server/BLACKPINK.mp4";
                // กำหนดเส้นทางของไฟล์ที่จะส่งไปยัง client

                String line = "";
                while (!line.equals("bye")) {
                    line = inStream.readUTF(); // อ่านข้อมูลที่ client ส่งมา
                    if (line.equals("d")) {
                        sendFile(filePath, outStream); // ถ้า client ส่ง "d" มา ให้ส่งไฟล์ไปยัง client
                    }
                    System.out.println("Sent from the client: " + line);
                    outStream.writeUTF("hello client "); // ส่งข้อความ "hello client " กลับไปยัง client
                    outStream.flush(); // บังคับให้ข้อมูลถูกส่งไปยัง client ทันที
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                System.out.println("hello"); // พิมพ์ข้อความ "hello" เมื่อการทำงานสิ้นสุด
            }
        }

        public void sendFile(String path, DataOutputStream outStream) throws Exception {
            int bytes = 0;
            File file = new File(path);
            FileInputStream fileInputStream = new FileInputStream(file);

            outStream.writeLong(file.length()); // ส่งขนาดของไฟล์ไปยัง client
            byte[] buffer = new byte[10 * 1024];
            while ((bytes = fileInputStream.read(buffer)) != -1) {
                outStream.write(buffer, 0, bytes); // ส่งข้อมูลไฟล์ไปยัง client ในรูปแบบของชิ้นย่อย
                outStream.flush(); // บังคับให้ข้อมูลถูกส่งไปยัง client ทันที
            }
            fileInputStream.close();
        }
    }
}
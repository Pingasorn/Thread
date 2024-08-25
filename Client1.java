import java.io.*;
import java.net.*;
import java.util.*;

class Client1 {
    public static void main(String[] args) {
        try (Socket socket = new Socket("localhost", 1104)) { // เชื่อมต่อ server ที่ localhost และพอร์ต 1104
            DataInputStream inStream = new DataInputStream(socket.getInputStream()); // สร้าง stream สำหรับอ่านข้อมูลจาก server
            DataOutputStream outStream = new DataOutputStream(socket.getOutputStream()); // สร้าง stream สำหรับส่งข้อมูลไปยัง server
            Scanner sc = new Scanner(System.in); // สร้าง Scanner สำหรับรับข้อมูลจากผู้ใช้
            String line = "";

            while (!"exit".equalsIgnoreCase(line)) { // วนลูปจนกว่าผู้ใช้จะป้อน "exit"
                line = sc.nextLine(); // รับข้อมูลจากผู้ใช้

                outStream.writeUTF(line); // ส่งข้อมูลไปยังเซิร์ฟเวอร์
                outStream.flush(); // บังคับให้ข้อมูลถูกส่งไปยังเซิร์ฟเวอร์ทันที

                if (line.equals("d")) { // ถ้าผู้ใช้ป้อน "d"
                    int bytes = 0;
                    FileOutputStream fileOutputStream = new FileOutputStream(
                            "C:/Users/Raum/Downloads/OS/OS/Client/BLACKPINK.mp4"); // สร้าง stream สำหรับเขียนข้อมูลลงในไฟล์
                    long size = inStream.readLong(); // อ่านขนาดไฟล์ที่จะถูกส่งมาจากเซิร์ฟเวอร์
                    byte[] buffer = new byte[10 * 1024]; // สร้าง buffer ขนาด 10KB
                    double sum = 0;
                    double size_exis = size;
                    System.out.println("file size :" + size);
                    while (size > 0 && (bytes = inStream.read(buffer, 0,
                            (int) Math.min(buffer.length, size))) != -1) {
                        sum += bytes;
                        System.out.println("Client get file:" + String.format("%.2f", (sum) / size_exis * 100) + " %");
                        fileOutputStream.write(buffer, 0, bytes); // เขียนข้อมูลจาก buffer ไปยังไฟล์
                        size -= bytes; // อ่านข้อมูลจนครบขนาดไฟล์
                    }
                    System.out.println(
                            "successfully get size file:" + String.format("%.2f", size_exis / (1024 * 1024)) + " MB");
                    fileOutputStream.close(); // ปิด stream ที่ใช้เขียนลงในไฟล์
                }

                line = inStream.readUTF(); // อ่านข้อความที่เซิร์ฟเวอร์ส่งกลับมา
                System.out.println("Server replied : " + line); // แสดงคำตอบจากเซิร์ฟเวอร์
            }
        } catch (IOException e) {
            e.printStackTrace(); // แสดง stack trace กรณีเกิดข้อผิดพลาดในการทำงาน
        }
    }
}
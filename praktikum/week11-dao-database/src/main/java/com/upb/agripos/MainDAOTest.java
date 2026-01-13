package com.upb.agripos;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;
import com.upb.agripos.dao.ProductDAO;
import com.upb.agripos.dao.ProductDAOImpl;
import com.upb.agripos.model.Product;

public class MainDAOTest {

    // Konfigurasi Database (Ganti sesuai setting laptop kamu)
    private static final String URL = "jdbc:postgresql://localhost:5432/agripos";
    private static final String USER = "postgres";
    private static final String PASS = "150603"; // <--- WAJIB DIISI

    public static void main(String[] args) {
        // Tambahkan baris ini agar simbol muncul dengan benar di Windows
    System.setProperty("file.encoding", "UTF-8");
    try {
        System.setOut(new java.io.PrintStream(System.out, true, "UTF-8"));
    } catch (Exception e) {}
        System.out.println("==============================================");
        System.out.println("Hello, I am Risky Dimas Nugroho-240202882 (Week11)");
        System.out.println("==============================================\n");

        // Membuka koneksi menggunakan try-with-resources
        try (Connection conn = DriverManager.getConnection(URL, USER, PASS)) {
            
            System.out.println("✅ Koneksi Berhasil!");

            // Create DAO (Mengirim koneksi langsung)
            ProductDAO dao = new ProductDAOImpl(conn);

            // Kita bersihkan data lama dulu (Cleanup Awal) 
            // Agar tidak terjadi error "Duplicate Key P01" saat dijalankan berulang kali
            dao.delete("P01");
            dao.delete("P02");
            dao.delete("P03");

            // ========== CREATE (INSERT) ==========
            System.out.println("\n========== TEST INSERT ==========");
            Product p1 = new Product("P01", "Pupuk Organik", 25000, 10);
            Product p2 = new Product("P02", "Benih Padi", 15000, 50);
            Product p3 = new Product("P03", "Pestisida Alami", 45000, 20);
            
            dao.insert(p1);
            dao.insert(p2);
            dao.insert(p3);
            System.out.println("Data berhasil dimasukkan.");

            // ========== READ (FIND ALL) ==========
            System.out.println("\n========== TEST FIND ALL ==========");
            List<Product> allProducts = dao.findAll();
            for (Product p : allProducts) {
                System.out.println(p);
            }

            // ========== UPDATE ==========
            System.out.println("\n========== TEST UPDATE ==========");
            Product updated = new Product("P01", "Pupuk Organik Premium", 30000, 8);
            dao.update(updated);
            System.out.println("Update berhasil: " + dao.findByCode("P01"));

            // ========== DELETE ==========
            System.out.println("\n========== TEST DELETE ==========");
            dao.delete("P03");
            System.out.println("P03 dihapus. Sisa data: " + dao.findAll().size());

            System.out.println("\n==============================================");
            System.out.println("✅ Semua operasi CRUD berjalan lancar!");
            System.out.println("==============================================");

        } catch (SQLException e) {
            System.err.println("❌ Database Error: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("❌ General Error:");
            e.printStackTrace();
        }
    }
}
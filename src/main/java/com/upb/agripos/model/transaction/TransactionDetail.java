package com.upb.agripos.model.transaction;

import com.upb.agripos.model.Product;

/**
 * Mewakili detail item dalam satu transaksi
 * Person A - TRANSACTION LAYER
 */
public class TransactionDetail {
    private String detailId;
    private String productCode;
    private String productName;
    private int quantity;
    private double unitPrice;
    private double subtotal;
    private double discount;
    private double total;
    
    public TransactionDetail(String detailId, Product product, int quantity, 
                            double discount) {
        this.detailId = detailId;
        this.productCode = product.getCode();
        this.productName = product.getName();
        this.quantity = quantity;
        this.unitPrice = product.getPrice();
        this.discount = discount;
        this.subtotal = unitPrice * quantity;
        this.total = subtotal - discount;
    }
    
    // Getters
    public String getDetailId() { return detailId; }
    public String getProductCode() { return productCode; }
    public String getProductName() { return productName; }
    public int getQuantity() { return quantity; }
    public double getUnitPrice() { return unitPrice; }
    public double getSubtotal() { return subtotal; }
    public double getDiscount() { return discount; }
    public double getTotal() { return total; }
    
    @Override
    public String toString() {
        return String.format(
            "%s | %s x %d @ Rp %.2f = Rp %.2f (Disc: Rp %.2f) = Rp %.2f",
            productCode, productName, quantity, unitPrice,
            subtotal, discount, total
        );
    }
}

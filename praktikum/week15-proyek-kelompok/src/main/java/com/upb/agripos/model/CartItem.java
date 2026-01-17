package com.upb.agripos.model;

/**
 * CartItem Model untuk FR-2
 * 
 * Represents item dalam keranjang belanja
 * 
 * Created by: [Copied from week14, minimal changes]
 * Last modified: 
 */
public class CartItem {
    private Product product;
    private int quantity;

    public CartItem(Product product, int quantity) {
        this.product = product;
        this.quantity = quantity;
    }

    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public double getSubtotal() {
        return product.getPrice() * quantity;
    }

    @Override
    public String toString() {
        return product.getName() + " x" + quantity + " = Rp " + getSubtotal();
    }
}

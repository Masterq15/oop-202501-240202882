package com.upb.agripos.controller;

import java.util.List;

import com.upb.agripos.exception.ValidationException;
import com.upb.agripos.model.CartItem;
import com.upb.agripos.model.Product;
import com.upb.agripos.model.Transaction;
import com.upb.agripos.service.CartService;
import com.upb.agripos.service.PaymentFactory;
import com.upb.agripos.service.PaymentMethod;
import com.upb.agripos.service.PaymentResult;
import com.upb.agripos.service.TransactionService;

/**
 * CartController
 * Controller untuk manajemen keranjang dan checkout
 */
public class CartController {
    private CartService cartService;
    private CartListener listener;

    public interface CartListener {
        void onItemAdded(CartItem item);
        void onItemRemoved(int productId);
        void onQuantityChanged(CartItem item);
        void onCartCleared();
        void onError(String errorMessage);
        void onCheckoutSuccess(String receipt);
    }

    public CartController() {
        this.cartService = new CartService();
    }

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    public void setCartListener(CartListener listener) {
        this.listener = listener;
    }

    /**
     * FR-2: Tambah Produk ke Keranjang
     */
    public void handleAddToCart(Product product, int quantity) {
        try {
            if (product == null) {
                throw new ValidationException("Produk tidak valid");
            }
            if (quantity <= 0) {
                throw new ValidationException("Kuantitas harus lebih dari 0");
            }

            cartService.addItem(product, quantity);
            if (listener != null) {
                CartItem item = findCartItem(product.getId());
                if (item != null) {
                    listener.onItemAdded(item);
                }
            }

        } catch (Exception e) {
            if (listener != null) {
                listener.onError("Error menambah ke keranjang: " + e.getMessage());
            }
        }
    }

    /**
     * FR-2: Ubah Kuantitas Item Keranjang
     */
    public void handleUpdateQuantity(int productId, int newQuantity) {
        try {
            cartService.updateItemQuantity(productId, newQuantity);
            if (listener != null) {
                CartItem item = findCartItem(productId);
                if (item != null) {
                    listener.onQuantityChanged(item);
                }
            }
        } catch (Exception e) {
            if (listener != null) {
                listener.onError("Error mengubah kuantitas: " + e.getMessage());
            }
        }
    }

    /**
     * FR-2: Hapus Item dari Keranjang
     */
    public void handleRemoveFromCart(int productId) {
        try {
            cartService.removeItem(productId);
            if (listener != null) {
                listener.onItemRemoved(productId);
            }
        } catch (Exception e) {
            if (listener != null) {
                listener.onError("Error menghapus item: " + e.getMessage());
            }
        }
    }

    /**
     * FR-2: Kosongkan Keranjang
     */
    public void handleClearCart() {
        cartService.clear();
        if (listener != null) {
            listener.onCartCleared();
        }
    }

    /**
     * Dapatkan semua item keranjang
     */
    public List<CartItem> getCartItems() {
        return cartService.getItems();
    }

    /**
     * Dapatkan subtotal keranjang
     */
    public double getSubtotal() {
        return cartService.getSubtotal();
    }

    /**
     * Dapatkan total keranjang
     */
    public double getTotal() {
        return cartService.getTotal();
    }

    /**
     * Cek apakah keranjang kosong
     */
    public boolean isCartEmpty() {
        return cartService.isEmpty();
    }

    /**
     * FR-3: Proses Checkout dengan Payment
     */
    public void handleCheckout(String paymentMethodName, String paymentInput, String cashierName) {
        try {
            if (cartService.isEmpty()) {
                throw new ValidationException("Keranjang kosong");
            }

            // Buat payment method sesuai tipe
            PaymentMethod paymentMethod = PaymentFactory.createPaymentMethod(paymentMethodName);
            PaymentResult result = paymentMethod.processPayment(cartService.getTotal(), paymentInput);

            if (!result.isSuccess()) {
                throw new ValidationException(result.getMessage());
            }

            // Parse cashPaid dari paymentInput (untuk Tunai) atau gunakan total (E-Wallet)
            double cashPaid = 0;
            try {
                if ("Tunai".equalsIgnoreCase(paymentMethodName)) {
                    cashPaid = Double.parseDouble(paymentInput);
                } else {
                    cashPaid = cartService.getTotal(); // Untuk E-Wallet, assume pembayaran pas
                }
            } catch (NumberFormatException e) {
                cashPaid = cartService.getTotal();
            }

            // Simpan transaksi ke database
            TransactionService transactionService = new TransactionService();
            Transaction transaction = transactionService.createTransaction(
                    paymentMethodName,
                    cartService.getTotal(),
                    cashierName,
                    cartService.getItems(),
                    cashPaid
            );

            // Generate struk
            StringBuilder receipt = new StringBuilder();
            receipt.append("=== AGRI-POS STRUK PEMBELIAN ===\n\n");
            receipt.append("No. Transaksi: ").append(transaction.getCode()).append("\n");
            receipt.append("Kasir: ").append(cashierName).append("\n");
            receipt.append("Metode: ").append(paymentMethodName).append("\n");
            receipt.append("Status: ").append(result.getMessage()).append("\n\n");

            receipt.append("ITEM DIBELI:\n");
            for (CartItem item : cartService.getItems()) {
                receipt.append("- ").append(item.getProduct().getName())
                        .append(" x").append(item.getQuantity())
                        .append(" = Rp ").append(String.format("%,.0f", item.getSubtotal())).append("\n");
            }

            receipt.append("\nTOTAL: Rp ").append(String.format("%,.0f", cartService.getTotal())).append("\n");
            
            if (result.getChange() > 0) {
                receipt.append("Kembalian: Rp ").append(String.format("%,.0f", result.getChange())).append("\n");
            }

            receipt.append("\nTerima Kasih!\n");

            // Clear cart dan notify
            cartService.clear();
            if (listener != null) {
                listener.onCheckoutSuccess(receipt.toString());
            }

        } catch (ValidationException e) {
            if (listener != null) {
                listener.onError(e.getMessage());
            }
        } catch (Exception e) {
            if (listener != null) {
                listener.onError("Error checkout: " + e.getMessage());
            }
        }
    }

    /**
     * Helper: cari item di keranjang
     */
    private CartItem findCartItem(int productId) {
        for (CartItem item : cartService.getItems()) {
            if (item.getProduct().getId() == productId) {
                return item;
            }
        }
        return null;
    }
}

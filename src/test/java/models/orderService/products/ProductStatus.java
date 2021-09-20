package models.orderService.products;

public enum ProductStatus {
    success (Num.success),
    changing (Num.changing),
    pending (Num.pending),
    damaged (Num.damaged),
    deprovisioned (Num.deprovisioned);

    ProductStatus(int status) {
    }

    public static class Num {
        public static final int success = 1;
        public static final int changing = 2;
        public static final int pending = 3;
        public static final int damaged = 4;
        public static final int deprovisioned = 5;
    }
}

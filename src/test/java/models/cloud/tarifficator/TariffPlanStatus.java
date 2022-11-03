package models.cloud.tarifficator;


public enum TariffPlanStatus {
    draft (Num.draft),
    planned (Num.planned),
    active (Num.active),
    archived (Num.archived);

    TariffPlanStatus(int status) {
    }

    public static class Num {
        public static final int draft = 1;
        public static final int planned = 2;
        public static final int active = 3;
        public static final int archived = 4;
    }
}

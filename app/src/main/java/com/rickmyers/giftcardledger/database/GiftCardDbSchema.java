package com.rickmyers.giftcardledger.database;

public class GiftCardDbSchema {

    public static final class GiftCardTable {
        public static final String NAME = "gift_cards";

        public static final class Cols {
            public static final String UUID = "uuid";
            public static final String NAME = "name";
            public static final String BALANCE = "balance";
            public static final String START_DATE = "start";
        }
    }
}

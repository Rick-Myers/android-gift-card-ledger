package com.rickmyers.giftcardledger.database;

/**
 * Defines the schema currently being used by the SQLite database.
 *
 * @author Rick Myers
 */
public class GiftCardDbSchema {

    /**
     * Defines the name of the table.
     */
    public static final class GiftCardTable {
        public static final String NAME = "gift_cards";

        /**
         * Defines the name of the columns in the table.
         */
        public static final class Cols {
            public static final String UUID = "uuid";
            public static final String NAME = "name";
            public static final String BALANCE = "balance";
            public static final String START_DATE = "start";
        }
    }
}

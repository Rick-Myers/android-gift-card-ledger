package com.rickmyers.giftcardledger.database;

/**
 * Defines the schema currently being used by the SQLite database.
 *
 * @author Rick Myers
 */
public class GiftCardDbSchema {
    public static final int VERSION = 1;

    /**
     * Defines the name of the main table where all gift cards are stored.
     */
    public static final class GiftCardTable {

        public static final String NAME = "gift_cards";

        /**
         * Defines the name of the columns in the gift card table.
         */
        public static final class Cols {
            public static final String UUID = "uuid";
            public static final String NAME = "name";
            public static final String BALANCE = "balance";
            //public static final String HISTORY = "history";
            public static final String HISTORY_TABLENAME = "history_table";

            // test
            public static final String LIST_POSITION = "list_position";
        }
    }

    /**
     * Defines the name history table.
     */
    public static final class HistoryTable {

        /**
         * Defines the name of the columns in the history table.
         */
        public static final class Cols {
            public static final String ID = "_id";
            public static final String DATE = "date";
            public static final String TRANSAC = "transac";
            public static final String BALANCE = "balance";
        }
    }

}

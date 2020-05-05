package com.example.carappv3.database;

public class DrawingSchema {
    public static final class DrawingTable{
        public static final String NAME = "drawings";
        public static final class Cols{
            public static final class PATHS{
                public static final class VERTICES{
                    public static final Integer X=0;
                    public static final Integer Y=0;
                }
            }
            public static final String BITMAP = "BITMAP";
        }
    }
}

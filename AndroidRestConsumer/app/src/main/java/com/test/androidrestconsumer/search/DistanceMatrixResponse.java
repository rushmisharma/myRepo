package com.test.androidrestconsumer.search;

import java.util.List;

/**
 * DistanceMatrixResponse class holds results from a query.
 */
public class DistanceMatrixResponse {
  /* Each of the following fields are populated with corresponding tag names in json response. */
  public String status;
  public List<String> destination_addresses;
  public List<String> origin_addresses;
  public List<Rows> rows;

  /**
   * Rows subsection of the json.
   */
  public class Rows {
    /* Holds the elements of each row. */
    public List<Elements> elements;

    /**
     * Element class, that is holds each element subsection in each row. 
     */
    public class Elements {
      /* Fields below correspond to section of the element subsection of the json with corresponding names. */
      public Distance distance;
      public Duration duration;
      public String status;

      /**
       * Holds distance subsection of elements. 
       */
      public class Distance {
        public String text;
        public int value;
      }

      /**
       * Holds duration subsection of elements section.
       */
      public class Duration {
        public String text;
        public int value;
      }
    }
  }
}

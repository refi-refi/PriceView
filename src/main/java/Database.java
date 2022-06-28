import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.jfree.data.time.Millisecond;
import org.jfree.data.time.ohlc.OHLCSeries;
import org.jfree.data.time.ohlc.OHLCSeriesCollection;
import org.jfree.data.xy.OHLCDataset;

public class Database {

  /**
   * Database class to access SQLite DB data.
   */
  String dbPath = "jdbc:sqlite:E:\\projects\\TradingApp\\files\\finance.db";
  String candle_query = "SELECT start_ts, open, high, low, close, volume FROM rest_api_ohlcv " +
      "WHERE start_ts >= ? AND end_ts < ? AND " +
      "symbol_id = (SELECT id FROM rest_api_symbol WHERE name = ?) ORDER BY end_ts";

  public OHLCDataset readOHLC(String dateFrom, String dateTo, String symbol)
      throws ParseException {

    Connection conn;
    PreparedStatement pstmt;
    ResultSet rs;

    Date dateTimeFrom = new SimpleDateFormat("yyyy-MM-dd").parse(dateFrom);
    Date dateTimeTo = new SimpleDateFormat("yyyy-MM-dd").parse(dateTo);

    long tsFrom = dateTimeFrom.getTime() / 1000;
    long tsTo = dateTimeTo.getTime() / 1000;

    OHLCSeries ohlcSeries = new OHLCSeries("Candles");

    try {
      conn = connect();
      pstmt = conn.prepareStatement(candle_query);
      pstmt.setLong(1, tsFrom);
      pstmt.setLong(2, tsTo);
      pstmt.setString(3, symbol);

      rs = pstmt.executeQuery();

      while (rs.next()) {
        Date date = new Date(rs.getLong("start_ts") * 1000);
        double open = rs.getInt("open") * Math.pow(10, -5);
        double high = rs.getInt("high") * Math.pow(10, -5);
        double low = rs.getInt("low") * Math.pow(10, -5);
        double close = rs.getInt("close") * Math.pow(10, -5);
        ohlcSeries.add(new Millisecond(date), open, high, low, close);
      }
    } catch (SQLException e) {
      System.err.print("SQLException: ");
      System.err.println(e.getMessage());
      throw new RuntimeException(e);
    } catch (Exception e) {
      System.err.print("Exception: ");
      System.err.println(e.getMessage());
    }

    OHLCSeriesCollection dataset = new OHLCSeriesCollection();
    dataset.addSeries(ohlcSeries);

    return dataset;
  }

  public Connection connect() {
    Connection conn = null;
    try {
      conn = DriverManager.getConnection(this.dbPath);
      System.out.println("Connection to SQLite has been established.");

    } catch (SQLException e) {
      System.out.println(e.getMessage());
    }
    return conn;
  }

  public static void main(String[] args) throws ParseException {
    Database app = new Database();
    app.readOHLC("2021-01-05", "2021-01-06", "EURUSD");
  }

}

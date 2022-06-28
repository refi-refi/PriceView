import java.text.ParseException;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.OHLCDataset;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

public class OhlcChart extends ApplicationFrame {

  public OhlcChart(String title) throws ParseException {
    super(title);
    Database db = new Database();
    OHLCDataset dataset = db.readOHLC("2021-01-01", "2021-01-5", "EURUSD");

    final JFreeChart chart = createChart(dataset);
    XYPlot plot = (XYPlot) chart.getPlot();
    plot.setRenderer(new CustomRender());

    NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
    rangeAxis.setAutoRangeIncludesZero(false);

    // Create Panel
    ChartPanel panel = new ChartPanel(chart);
    setContentPane(panel);
  }

  private JFreeChart createChart(final OHLCDataset dataset) {
    return ChartFactory.createCandlestickChart(
        "EUR/USD", "Time", "Price", dataset, false
    );
  }

  public static void main(String[] args) throws ParseException {
    OhlcChart app = new OhlcChart("Title");
    app.pack();
    RefineryUtilities.centerFrameOnScreen(app);
    app.setVisible(true);
  }
}

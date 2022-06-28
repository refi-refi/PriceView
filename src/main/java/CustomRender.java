import java.awt.Color;
import java.awt.Paint;
import org.jfree.chart.renderer.xy.CandlestickRenderer;

public class CustomRender extends CandlestickRenderer {

  @Override
  public Paint getItemPaint(int row, int column) {
    return Color.BLACK;
  }
}
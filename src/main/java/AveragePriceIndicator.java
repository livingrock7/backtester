import org.ta4j.core.BarSeries;
import org.ta4j.core.indicators.helpers.PriceIndicator;
import org.ta4j.core.num.DoubleNum;
import org.ta4j.core.num.PrecisionNum;

public class AveragePriceIndicator extends PriceIndicator {

    public AveragePriceIndicator(BarSeries series) {
        super(series, bar -> {
            return bar.getClosePrice().plus(bar.getHighPrice()).plus(bar.getOpenPrice()).plus(bar.getLowPrice()).dividedBy(PrecisionNum.valueOf(4));
        });
    }
}

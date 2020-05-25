package strategy;

import org.ta4j.core.BarSeries;
import org.ta4j.core.BaseStrategy;

public class BollingerBandStrategy implements StrategyRule{

    @Override
    public BaseStrategy getLongStrategy(BarSeries series) {
        return null;
    }

    @Override
    public BaseStrategy getShortStrategy(BarSeries series) {
        return null;
    }
}

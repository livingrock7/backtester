package strategy;

import org.ta4j.core.BarSeries;
import org.ta4j.core.BaseStrategy;

public interface StrategyRule {

    BaseStrategy getLongStrategy(BarSeries series);

    BaseStrategy getShortStrategy(BarSeries series);

}

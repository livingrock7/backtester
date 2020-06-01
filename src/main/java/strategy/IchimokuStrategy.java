package strategy;

//import jdk.internal.org.jline.utils.DiffHelper;
import customindicators.AveragePriceIndicator;
import org.ta4j.core.BarSeries;
import org.ta4j.core.BaseStrategy;
import org.ta4j.core.Rule;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;
import org.ta4j.core.indicators.helpers.DifferenceIndicator;
import org.ta4j.core.indicators.ichimoku.IchimokuKijunSenIndicator;
import org.ta4j.core.indicators.ichimoku.IchimokuSenkouSpanAIndicator;
import org.ta4j.core.indicators.ichimoku.IchimokuSenkouSpanBIndicator;
import org.ta4j.core.indicators.ichimoku.IchimokuTenkanSenIndicator;
import org.ta4j.core.num.PrecisionNum;
import org.ta4j.core.trading.rules.*;

public class IchimokuStrategy implements StrategyRule{

    @Override
    public BaseStrategy getLongStrategy(BarSeries series) {
        // tested on daily 6 aug 2019 to 15 may 2020 // needs to tune and combine indicators
        ClosePriceIndicator close = new ClosePriceIndicator(series);
        IchimokuKijunSenIndicator maroonLine = new IchimokuKijunSenIndicator(series,26);
        IchimokuTenkanSenIndicator blueLine = new IchimokuTenkanSenIndicator(series,9);
        IchimokuSenkouSpanAIndicator greenLine = new IchimokuSenkouSpanAIndicator(series,blueLine,maroonLine);
        IchimokuSenkouSpanBIndicator redLine = new IchimokuSenkouSpanBIndicator(series);
        DifferenceIndicator diff = new DifferenceIndicator(blueLine,redLine);
        AveragePriceIndicator averagePriceIndicator = new AveragePriceIndicator(series);
        System.out.println(maroonLine.getValue(100));
        System.out.println(greenLine.getValue(100));
        System.out.println(blueLine.getValue(100));
        System.out.println(redLine.getValue(100));
        //Rule entryRule = new OverIndicatorRule(close,greenLine).and(new OverIndicatorRule(close,redLine)).and(new CrossedUpIndicatorRule(close,blueLine).and(new OverIndicatorRule(blueLine,maroonLine)));
        //Rule entryRule = new OverIndicatorRule(close,greenLine).and(new OverIndicatorRule(close,redLine)).and(new CrossedUpIndicatorRule(close,blueLine).and(new OverIndicatorRule(blueLine,maroonLine)));
        Rule entryRule = new OverIndicatorRule(blueLine,greenLine).and(new OverIndicatorRule(blueLine,redLine)).and(new OverIndicatorRule(maroonLine,greenLine)).and(new OverIndicatorRule(maroonLine,redLine)).and(new OverIndicatorRule(close,blueLine));
        Rule exitRule = new UnderIndicatorRule(close,blueLine)
                .or(new StopGainRule(close, PrecisionNum.valueOf(35)))
                .or(new StopLossRule(close, PrecisionNum.valueOf(1.5)));
        return new BaseStrategy(entryRule,exitRule);
    }

    @Override
    public BaseStrategy getShortStrategy(BarSeries series) {
        ClosePriceIndicator close = new ClosePriceIndicator(series);
        IchimokuKijunSenIndicator maroonLine = new IchimokuKijunSenIndicator(series);
        IchimokuTenkanSenIndicator blueLine = new IchimokuTenkanSenIndicator(series);
        IchimokuSenkouSpanAIndicator greenLine = new IchimokuSenkouSpanAIndicator(series);
        IchimokuSenkouSpanBIndicator redLine = new IchimokuSenkouSpanBIndicator(series);
        DifferenceIndicator diff = new DifferenceIndicator(blueLine,redLine);
        AveragePriceIndicator averagePriceIndicator = new AveragePriceIndicator(series);
        Rule entryRule = new UnderIndicatorRule(close,greenLine).and(new UnderIndicatorRule(close,redLine)).and(new CrossedDownIndicatorRule(close,maroonLine)).and(new UnderIndicatorRule(blueLine,maroonLine));
        //Rule entryRule = new UnderIndicatorRule(close,greenLine).and(new UnderIndicatorRule(close,redLine)).and(new CrossedDownIndicatorRule(close,blueLine)).and(new UnderIndicatorRule(close,blueLine)).and(new UnderIndicatorRule(close,maroonLine));
        Rule exitRule = new UnderIndicatorRule(blueLine,close)
                .or(new StopGainRule(close, PrecisionNum.valueOf(5)))
                .or(new StopLossRule(close, PrecisionNum.valueOf(1.5)));
        return new BaseStrategy(entryRule,exitRule);
    }
}

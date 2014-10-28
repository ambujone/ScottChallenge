package trading;

import java.util.ArrayList;

import game.DailyOutput;
import game.TradingManager;
import tradingstrategy.BaseTradingStrategy;
import dataobjects.DailyInput;
import exceptions.InsufficientFundsException;
import exceptions.InsufficientSharesException;
import static commons.Util.*;

public class TradingStrategy extends BaseTradingStrategy {
    private final double riskPercentage = 100;
    private final int maRange = 1;
    private final double maThreshold = 0.3;

    private ArrayList<Double> maCandles = new ArrayList<Double>(maRange);

    private double maxMADeviation = 0;
    /*
     * Linear Moving average strategy
     * Parameters:
     *      Range (candles)
     */

    /*
     * Returns:
     *      1 for buy
     *      0 for nothing
     *      -1 for sell
     */
    private int makeDecision(DailyInput input, double dayMA)
    {
        /*
         * Rules:
         *
         */
        if(dayMA == -1) return 0;

        double dev = input.getClose() - dayMA;
        double absDev = Math.abs(dev);
        if(absDev > maxMADeviation)
            maxMADeviation = absDev;

        double factor = dev/maxMADeviation;
        /*
         * Factor:
         *      f < (-1 + maThreshold) or (f > (0 + maThreshold) && f < (0 + 2*maThreshold)) then sell zone
         *      f > (1 - maThreshold) or (f < (0 - maThreshold) && f > (0 - 2*maThreshold)) then buy zone
        */

        if (factor <= (-1 + maThreshold) || (factor >= (0 + maThreshold) && factor <= (0 + 2*maThreshold)))
            return -1;
        else if(factor >= (1 - maThreshold) || (factor <= (0 - maThreshold) && factor >= (0 - 2*maThreshold)))
            return 1;

        return 0;
    }

    /*
     * Returns:
     *      Avg of last *maRange* number of candles
     *      Or -1 if not enough data
     */
    private double getMA(double dayAvg)
    {
        if(maCandles.isEmpty())
            maCandles.add(dayAvg);
        else
            maCandles.add(1, dayAvg);


        if(maCandles.size() < maRange)
            return -1;


        double sum = 0;
        for(int i = 0; i < maRange; i++)
            sum += maCandles.get(i);

        return sum/maRange;
    }

    public TradingStrategy(TradingManager tradingManager) {
        super.tradingManager = tradingManager;
    }

    @Override
    public DailyOutput makeDailyTrade(DailyInput input) throws InsufficientFundsException, InsufficientSharesException {
        //use the trading manager to make trades based on input
        double dayMA = getMA((double) (input.getHigh() + input.getOpen() + input.getClose() + input.getLow())/4);

        final int buyShareValue = (int) (tradingManager.getAvailableFunds() * (riskPercentage / 100));
        final int sellShareValue = (int) (tradingManager.getInvestmentAmount(input) * (riskPercentage / 100));

        DailyOutput decision;

        switch(makeDecision(input, dayMA))
        {
            case 1:
                decision = tradingManager.buySharesOfValue(input, buyShareValue);
                break;
            case -1:
                decision = tradingManager.sellSharesOfValue(input, sellShareValue);
                break;
            case 0:
            default:
                decision = tradingManager.doNothing(input);
        }

        return decision;
    }

}

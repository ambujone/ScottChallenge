package trading;

import game.DailyOutput;
import game.TradingManager;
import tradingstrategy.BaseTradingStrategy;
import dataobjects.DailyInput;
import exceptions.InsufficientFundsException;
import exceptions.InsufficientSharesException;

public class TradingStrategy extends BaseTradingStrategy {

	public TradingStrategy(TradingManager tradingManager) {
		super.tradingManager = tradingManager;
	}

	@Override
	public DailyOutput makeDailyTrade(DailyInput input) throws InsufficientFundsException, InsufficientSharesException {
		//use the trading manager to make trades based on input
		
		
		DailyOutput output;
		double variance = 100 - input.getLow()/input.getHigh() * 100;
		//System.out.println(variance);
		//250 days trading
		//System.out.println(input);
		if (variance > 15) {
			output = tradingManager.sellAllShares(input);
		}				
		else if (input.getOpen() - input.getClose() > 1) {
			output = tradingManager.sellNumberOfShares(input, tradingManager.getSharesOwned());
		}
		else if (input.getOpen() - input.getClose() > 0.4) {
			output = tradingManager.sellNumberOfShares(input, tradingManager.getSharesOwned()/2);		
		} 
		else if (input.getOpen() - input.getClose() < -0.1) {
			output = tradingManager.buySharesOfValue(input, tradingManager.getAvailableFunds());		
		} 
		else if (input.getOpen() - input.getClose() < 0.2) {
			output = tradingManager.buySharesOfValue(input, tradingManager.getAvailableFunds()/2);		
		} 
		else {
			output = tradingManager.doNothing(input);
		}
		
		return output;
	}

}

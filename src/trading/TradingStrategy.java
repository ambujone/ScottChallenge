package trading;

import java.util.ArrayList;
import java.util.List;

import game.DailyOutput;
import game.TradingManager;
import tradingstrategy.BaseTradingStrategy;
import dataobjects.DailyInput;
import exceptions.InsufficientFundsException;
import exceptions.InsufficientSharesException;

public class TradingStrategy extends BaseTradingStrategy {
	List<Double> prices;
	
	public TradingStrategy(TradingManager tradingManager) {
		super.tradingManager = tradingManager;
		this.prices = new ArrayList<Double>();
	}

	@Override
	public DailyOutput makeDailyTrade(DailyInput input) throws InsufficientFundsException, InsufficientSharesException {
		//use the trading manager to make trades based on input
//		System.out.println(input.getOpen() + " " + input.getClose());
		prices.add(input.getOpen());
		double variance = 100 - input.getLow() * 100 / input.getHigh();
		DailyOutput output;
		double sum = 0.0;
		double pct = 0.5;
		for(double i: prices) {
			sum += i;
		}
//		System.out.println(tradingManager.getAvailableFunds());
//		System.out.println((sum / prices.size()) + " " + input.getOpen() + " " + input.getClose() );
		double avg = sum / prices.size();
		double avg2 = (input.getOpen() + input.getClose()) / 2;
		if (variance > 15 || input.getOpen() > input.getClose()) {
			output = tradingManager.sellNumberOfShares(input, (int)(tradingManager.getSharesOwned()));
		} else {
			output = tradingManager.buySharesOfValue(input, (int)(tradingManager.getAvailableFunds()));
		}
			//		if(variance > 15 || input.getOpen() > input.getClose()) {
//			output = tradingManager.sellNumberOfShares(input, (int)(tradingManager.getSharesOwned()));
////			output = tradingManager.doNothing(input);
//		} 
//		else if (avg2 - avg > 10) {
//			//System.out.println("Here");
//			output = tradingManager.sellNumberOfShares(input, (int)(tradingManager.getSharesOwned()));
//
//		}
//		else if (avg2 - avg > 8) {
//			output = tradingManager.doNothing(input);
//		}
//		else {
//				output = tradingManager.buySharesOfValue(input, (int)(tradingManager.getAvailableFunds()));
//		}
//		if(variance > 10 && pct < 0.9) {
//			pct += 0.1;
//		} else if(variance < 10 && pct > 0.1) {
//			pct -= 0.1;
//		}
		
		prices.add(input.getClose());
		
		return output;
	}

}

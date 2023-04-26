
package com.crio.warmup.stock.portfolio;

import static java.time.temporal.ChronoUnit.DAYS;
import static java.time.temporal.ChronoUnit.SECONDS;

import com.crio.warmup.stock.dto.AnnualizedReturn;
import com.crio.warmup.stock.dto.Candle;
import com.crio.warmup.stock.dto.PortfolioTrade;
import com.crio.warmup.stock.dto.TiingoCandle;
import com.crio.warmup.stock.quotes.StockQuotesService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import javax.management.RuntimeErrorException;
import org.springframework.web.client.RestTemplate;



class stockComparator implements Comparator<AnnualizedReturn>{

  @Override
  public int compare(AnnualizedReturn ar1, AnnualizedReturn ar2) {
     
    if(ar2.getAnnualizedReturn()-ar1.getAnnualizedReturn()>0)
      return 1;
      else if(ar2.getAnnualizedReturn()-ar1.getAnnualizedReturn()<0)
        return -1;
       else{
         return 0;
       }


 //  return (int)(ar1.getAnnualizedReturn()-ar2.getAnnualizedReturn());

  }




}


public class PortfolioManagerImpl implements PortfolioManager {

RestTemplate restTemplate=new RestTemplate();

private StockQuotesService stockQuotesService;
 PortfolioManagerImpl(StockQuotesService stockQuotesService)
   {
    this.stockQuotesService=stockQuotesService;
   }


  @Override
  public List<AnnualizedReturn> calculateAnnualizedReturn(List<PortfolioTrade> portfolioTrades,LocalDate endDate) throws JsonProcessingException
  {
//     RestTemplate restTemplate=new RestTemplate();
//     List<AnnualizedReturn> annualizedReturns=new ArrayList<>();

//            for(PortfolioTrade pft: portfolioTrades)
//           {
//              String url=buildUri(pft.getSymbol(),pft.getPurchaseDate(), endDate);
             
//              TiingoCandle [] tiingoCandles=restTemplate.getForObject(url,TiingoCandle[].class);
             
//              AnnualizedReturn annret= calculateAnnualReturns(endDate, pft, tiingoCandles[0].getOpen(), tiingoCandles[tiingoCandles.length-1].getClose());

//                annualizedReturns.add(annret);

//           }
    
//           Collections.sort(annualizedReturns,new stockComparator());

//   //System.out.println("after sort");
     
//  // for(AnnualizedReturn ar: annualizedReturns)
// //{
// //  System.out.println(ar.getAnnualizedReturn());
// //}


//           return annualizedReturns;

List<AnnualizedReturn> annualizedReturns = new ArrayList<>();
    for (PortfolioTrade portfolioTrade : portfolioTrades) {
      List<Candle> candles = getStockQuote(portfolioTrade.getSymbol(), portfolioTrade.getPurchaseDate(), endDate);
      AnnualizedReturn annualizedReturn = calculateAnnualReturns(endDate, portfolioTrade,
          getOpeningPriceOnStartDate(candles), getClosingPriceOnEndDate(candles));
      annualizedReturns.add(annualizedReturn);
    }
    return annualizedReturns.stream().sorted(getComparator()).collect(Collectors.toList());


  }

  private Double getOpeningPriceOnStartDate(List<Candle> candles) {
    return candles.get(0).getOpen();
  }

  private Double getClosingPriceOnEndDate(List<Candle> candles) {
    return candles.get(candles.size() - 1).getClose();
  }




  public static int getTimeDiffInDays(LocalDate startDate, LocalDate endDate)
  {
    
    String str1[]=startDate.toString().split("-");
    String str2[]=endDate.toString().split("-");
    
    Date st=new Date(Integer.parseInt(str1[2]),Integer.parseInt(str1[1]),Integer.parseInt(str1[0]));
     Date en=new Date(Integer.parseInt(str2[2]),Integer.parseInt(str2[1]),Integer.parseInt(str2[0]));
  
  return getDifference(st,en);
  
  
  }
  // Caution: Do not delete or modify the constructor, or else your build will break!
  // This is absolutely necessary for backward compatibility


  protected PortfolioManagerImpl(RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }


  //TODO: CRIO_TASK_MODULE_REFACTOR
  // 1. Now we want to convert our code into a module, so we will not call it from main anymore.
  //    Copy your code from Module#3 PortfolioManagerApplication#calculateAnnualizedReturn
  //    into #calculateAnnualizedReturn function here and ensure it follows the method signature.
  // 2. Logic to read Json file and convert them into Objects will not be required further as our
  //    clients will take care of it, going forward.

  // Note:
  // Make sure to exercise the tests inside PortfolioManagerTest using command below:
  // ./gradlew test --tests PortfolioManagerTest

  //CHECKSTYLE:OFF




  private Comparator<AnnualizedReturn> getComparator() {
    return Comparator.comparing(AnnualizedReturn::getAnnualizedReturn).reversed();
  }

  //CHECKSTYLE:OFF

  // TODO: CRIO_TASK_MODULE_REFACTOR
  //  Extract the logic to call Tiingo third-party APIs to a separate function.
  //  Remember to fill out the buildUri function and use that.


  public List<Candle> getStockQuote(String symbol, LocalDate from, LocalDate to)
      throws JsonProcessingException {

        return stockQuotesService.getStockQuote(symbol, from, to);
     
     /*   RestTemplate restTemplate=new RestTemplate();
     //   TiingoCandle[] result=restTemplate.getForObject( buildUri(symbol, from, to),TiingoCandle[].class);
      String response=restTemplate.getForObject(buildUri(symbol, from, to), String.class);
             
         ObjectMapper obj=new ObjectMapper();
         obj.registerModule(new JavaTimeModule());
         TiingoCandle[] result=obj.readValue(response, TiingoCandle[].class);
         
         if(result==null)
          {
          return new ArrayList<>();
            }

            return Arrays.asList(result); */

  }

  protected String buildUri(String symbol, LocalDate startDate, LocalDate endDate) {
    
String uriTemplate = "https://api.tiingo.com/tiingo/daily/"+symbol+"/prices?startDate="+startDate.toString()+"&endDate="+endDate.toString()+"&token=b92af9bbb3c785abfd85b9e8203b7f995790c9aa";

            return uriTemplate;
  }

/*
Logic to calculate annualized returns below


*/


public static AnnualizedReturn calculateAnnualReturns(LocalDate endDate,
PortfolioTrade trade, Double buyPrice, Double sellPrice) {
  
//    Double totalReturn=((sellPrice-buyPrice)/buyPrice);
// double TotalDays=getTimeDiffInDays(trade.getPurchaseDate(), endDate);      
//   Double time=(double) (TotalDays/365);

//    Double annualReturn =Math.pow((1+totalReturn),(1/time))-1;

//   //System.out.println("annualized return is "+annualReturn);


// return new AnnualizedReturn(trade.getSymbol(),annualReturn, totalReturn);
double total_num_years = DAYS.between(trade.getPurchaseDate(), endDate) / 365.2422;
    double totalReturns = (sellPrice - buyPrice) / buyPrice;
    double annualized_returns = Math.pow((1.0 + totalReturns), (1.0 / total_num_years)) - 1;
    return new AnnualizedReturn(trade.getSymbol(), annualized_returns, totalReturns);

}
////////////////////

static class Date 
{
   int d, m, y;

   public Date(int d, int m, int y)
   {
       this.d = d;
       this.m = m;
       this.y = y;
   }

};

// To store number of days in 
// all months from January to Dec.
static int monthDays[] = {31, 28, 31, 30, 31, 30,
                       31, 31, 30, 31, 30, 31};

// This function counts number of 
// leap years before the given date
static int countLeapYears(Date d) 
{
   int years = d.y;

   // Check if the current year needs to be considered
   // for the count of leap years or not
   if (d.m <= 2) 
   {
       years--;
   }

   // An year is a leap year if it is a multiple of 4,
   // multiple of 400 and not a multiple of 100.
   return years / 4 - years / 100 + years / 400;
}

// This function returns number 
// of days between two given dates
static int getDifference(Date dt1, Date dt2)
{
   // COUNT TOTAL NUMBER OF DAYS BEFORE FIRST DATE 'dt1'

   // initialize count using years and day
   int n1 = dt1.y * 365 + dt1.d;

   // Add days for months in given date
   for (int i = 0; i < dt1.m - 1; i++) 
   {
       n1 += monthDays[i];
   }

   // Since every leap year is of 366 days,
   // Add a day for every leap year
   n1 += countLeapYears(dt1);

   // SIMILARLY, COUNT TOTAL NUMBER OF DAYS BEFORE 'dt2'
   int n2 = dt2.y * 365 + dt2.d;
   for (int i = 0; i < dt2.m - 1; i++)
   {
       n2 += monthDays[i];
   }
   n2 += countLeapYears(dt2);

   // return difference between two counts
   return (n2 - n1);
}


public static String getToken() {
 return "b92af9bbb3c785abfd85b9e8203b7f995790c9aa";
       
}







}

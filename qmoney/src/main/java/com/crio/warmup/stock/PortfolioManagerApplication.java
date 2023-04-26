
package com.crio.warmup.stock;


import com.crio.warmup.stock.dto.*;
import com.crio.warmup.stock.log.UncaughtExceptionHandler;
import com.crio.warmup.stock.portfolio.PortfolioManager;
import com.crio.warmup.stock.portfolio.PortfolioManagerFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.http.HttpHeaders;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.lang.model.util.ElementScanner6;
import org.apache.logging.log4j.ThreadContext;
import org.springframework.cglib.core.Local;
import org.springframework.http.MediaType;
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



public class PortfolioManagerApplication {




 // TODO: CRIO_TASK_MODULE_CALCULATIONS
 //  Now that you have the list of PortfolioTrade and their data, calculate annualized returns
 //  for the stocks provided in the Json.
 //  Use the function you just wrote #calculateAnnualizedReturns.
 //  Return the list of AnnualizedReturns sorted by annualizedReturns in descending order.

 // Note:
 // 1. You may need to copy relevant code from #mainReadQuotes to parse the Json.
 // 2. Remember to get the latest quotes from Tiingo API.




 // TODO:
 //  Ensure all tests are passing using below commandclear
 
 //  ./gradlew test --tests ModuleThreeRefactorTest
 static Double getOpeningPriceOnStartDate(List<Candle> candles) {
    return candles.get(0).getOpen();
 }


 public static Double getClosingPriceOnEndDate(List<Candle> candles) {
    return candles.get(candles.size()-1).getClose();
 }


 public static List<Candle> fetchCandles(PortfolioTrade trade, LocalDate endDate, String token) {
  
  RestTemplate rest=new RestTemplate();
 String url=prepareUrl(trade, endDate, token);

 TiingoCandle tiingoCandle[] =rest.getForObject(url,TiingoCandle[].class  );
  
      List<Candle> candleList=Arrays.asList(tiingoCandle);

  return candleList;
  
  
  //return Collections.emptyList();
 }

 public static List<AnnualizedReturn> mainCalculateSingleReturn(String[] args)throws IOException, URISyntaxException {
    
  List<AnnualizedReturn> annualizedReturns=new ArrayList<>();
          
  File file= resolveFileFromResources(args[0]);
     
  ObjectMapper obj=getObjectMapper();

  PortfolioTrade trades[]=obj.readValue(file,PortfolioTrade[].class);
  
  DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
  LocalDate localDate = LocalDate.parse(args[1], formatter);
  
  for(PortfolioTrade pft: trades)
  {

     String url=prepareUrl(pft,localDate , "b92af9bbb3c785abfd85b9e8203b7f995790c9aa");      
        
     RestTemplate restTemp=new RestTemplate();
     TiingoCandle tiingoCandle[]=restTemp.getForObject(url,TiingoCandle[].class);

     AnnualizedReturn annRet=calculateAnnualizedReturns(localDate, pft, tiingoCandle[0].getOpen(),tiingoCandle[tiingoCandle.length-1].getClose()); 
       
     annualizedReturns.add(annRet);

  }
  
  //Logic to perform sorting based on Annual returns;
//  System.out.println("before comparator!");
//  for(AnnualizedReturn a: annualizedReturns)
   //  {
  //    System.out.println(a.getAnnualizedReturn());
  //   }

 Collections.sort(annualizedReturns,new stockComparator());

  //System.out.println("final annualize returns!");
        // for(AnnualizedReturn a: annualizedReturns)
         //   {
          //   System.out.println(a.getAnnualizedReturn());
          //  }

  
  
  return annualizedReturns;
 }

 // TODO: CRIO_TASK_MODULE_CALCULATIONS
 //  Return the populated list of AnnualizedReturn for all stocks.
 //  Annualized returns should be calculated in two steps:
 //   1. Calculate totalReturn = (sell_value - buy_value) / buy_value.
 //      1.1 Store the same as totalReturns
 //   2. Calculate extrapolated annualized returns by scaling the same in years span.
 //      The formula is:
 //      annualized_returns = (1 + total_returns) ^ (1 / total_num_years) - 1
 //      2.1 Store the same as annualized_returns
 //  Test the same using below specified command. The build should be successful.
 //     ./gradlew test --tests PortfolioManagerApplicationTest.testCalculateAnnualizedReturn


public static int getTimeDiffInDays(LocalDate startDate, LocalDate endDate)
{
  
  String str1[]=startDate.toString().split("-");
  String str2[]=endDate.toString().split("-");
  
  Date st=new Date(Integer.parseInt(str1[2]),Integer.parseInt(str1[1]),Integer.parseInt(str1[0]));
   Date en=new Date(Integer.parseInt(str2[2]),Integer.parseInt(str2[1]),Integer.parseInt(str2[0]));

return getDifference(st,en);


}





 public static AnnualizedReturn calculateAnnualizedReturns(LocalDate endDate,
     PortfolioTrade trade, Double buyPrice, Double sellPrice) {
       
        Double totalReturn=((sellPrice-buyPrice)/buyPrice);
  double TotalDays=getTimeDiffInDays(trade.getPurchaseDate(), endDate);      
       Double time=(double) (TotalDays/365);

        Double annualReturn =Math.pow((1+totalReturn),(1/time))-1;

   //  System.out.println("annualized return is "+annualReturn);


     return new AnnualizedReturn(trade.getSymbol(),annualReturn, totalReturn);
 }


//////////////////////////old methods below
public static List<String> mainReadQuotes(String[] args) throws IOException, URISyntaxException {
       
  File file= resolveFileFromResources(args[0]);
     
  ObjectMapper obj=getObjectMapper();

  PortfolioTrade trades[]=obj.readValue(file,PortfolioTrade[].class);
 
   DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
   LocalDate localDate = LocalDate.parse(args[1], formatter);
      

 List<TotalReturnsDto> totalReturnsDto = new ArrayList<TotalReturnsDto>();
                          
  for(PortfolioTrade pft: trades)
      {
            String url=prepareUrl(pft,localDate,"b92af9bbb3c785abfd85b9e8203b7f995790c9aa");
            RestTemplate restTemp=new RestTemplate();
         TiingoCandle[]  tiingoCandle=restTemp.getForObject(url,TiingoCandle[].class);
    

      totalReturnsDto.add(new TotalReturnsDto(pft.getSymbol(), tiingoCandle[tiingoCandle.length - 1].getClose()));
      }



     List <String> incCloseDate= new  ArrayList<String>(); 
      TotalReturnsDto[] totalruturn = new TotalReturnsDto[totalReturnsDto.size()];
      totalruturn = totalReturnsDto.toArray(totalruturn);
  //Logic to perform sorting based on closevalue of Tiingo's api data.
         int size=trades.length;

         for(int i=0;i<size-1;i++)
         {
          Double min=Double.MAX_VALUE;
          int idx=0;
              for(int j=i;j<size;j++)
                {
                     if(totalruturn[j].getClosingPrice()<min)
                     {
                       min=totalruturn[j].getClosingPrice();
                       idx=j;
                     }

                }
                 
                TotalReturnsDto temp=totalruturn[idx];
                totalruturn[idx]=totalruturn[i];
                totalruturn[i]=temp;
         }

          for(int i = 0; i < totalruturn.length; i++){
        incCloseDate.add(totalruturn[i].getSymbol());
       }

return incCloseDate;
}

public static List<PortfolioTrade> readTradesFromJson(String filename) throws IOException, URISyntaxException {
   
  File file= resolveFileFromResources(filename);
        
  ObjectMapper obj=getObjectMapper();

  PortfolioTrade trades[]=obj.readValue(file,PortfolioTrade[].class);

  List <PortfolioTrade> answer= Arrays.asList(trades);

  return answer;
}


 public static String prepareUrl (PortfolioTrade trade, LocalDate endDate, String token) {
 
  return  "https://api.tiingo.com/tiingo/daily/"+ trade.getSymbol() +"/prices?startDate="+ 
  trade.getPurchaseDate() +"&endDate="+ endDate +"&token=" + token;
}

private static void printJsonObject(Object object) throws IOException {
  Logger logger = Logger.getLogger(PortfolioManagerApplication.class.getCanonicalName());
  ObjectMapper mapper = new ObjectMapper();
  logger.info(mapper.writeValueAsString(object));
}

private static File resolveFileFromResources(String filename) throws URISyntaxException {
  return Paths.get(
      Thread.currentThread().getContextClassLoader().getResource(filename).toURI()).toFile();
}

private static ObjectMapper getObjectMapper() {
  ObjectMapper objectMapper = new ObjectMapper();
  objectMapper.registerModule(new JavaTimeModule());
  return objectMapper;
}

public static List<String> mainReadFile(String[] args) throws IOException, URISyntaxException {

  // System.out.println("args is "+args[1]);
 
     File file= resolveFileFromResources(args[0]);
        
     ObjectMapper obj=getObjectMapper();
 
     PortfolioTrade trades[]=obj.readValue(file,PortfolioTrade[].class);
 
     List <String> stocknames= new  ArrayList<String>(); 
       
     for(PortfolioTrade pft : trades)
     {
          stocknames.add(pft.getSymbol());
     }
 
 
 return stocknames;
 
     // return Collections.emptyList();
   }
 

   public static List<String> debugOutputs() {

     String valueOfArgument0 = "trades.json";
     String resultOfResolveFilePathArgs0 = "trades.json";
     String toStringOfObjectMapper = "ObjectMapper";
     String functionNameFromTestFileInStackTrace = "mainReadFile";
     String lineNumberFromTestFileInStackTrace = "";


    return Arrays.asList(new String[]{valueOfArgument0, resultOfResolveFilePathArgs0,
        toStringOfObjectMapper, functionNameFromTestFileInStackTrace,
        lineNumberFromTestFileInStackTrace});
  }








 public static void main(String[] args) throws Exception {
   Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler());
   ThreadContext.put("runId", UUID.randomUUID().toString());



   printJsonObject(mainCalculateSingleReturn(args));

 }

//Below is the date diff calculation


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
